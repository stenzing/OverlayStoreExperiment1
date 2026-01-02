package sg.overlay.api;

import io.netty.handler.codec.http.HttpResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.netty.DisposableServer;
import reactor.netty.http.server.HttpServer;
import sg.overlay.*;
import sg.overlay.backend.DatabaseStateLoader;
import sg.overlay.updater.KafkaSourceFactory;

import java.util.List;
import java.util.concurrent.*;

public class Application {

    private static final VolumeManager manager = new VolumeManager();
    private static final Logger log = LoggerFactory.getLogger(Application.class);

    static void main(String[] args) {
        DisposableServer server =
                HttpServer.create()
                        .host("localhost")
                        .port(8080)
                        .route(routes -> routes.get("/{path}",
                                (request, response) -> {
                                    log.info("Received path: {}", request.path());
                                    var entry = manager
                                            .ofStructure(List.of("01"))
                                            .getEntry(request.param("path"));
                                    if (entry.isPresent()) {
                                        return response.status(HttpResponseStatus.OK)
                                                .sendString(Mono.just(entry.get().content()));
                                    }
                                    return response.sendNotFound();
                                }))
                        .bindNow();


        try (ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor()) {
            addVolume("01", manager, executorService);
            server.onDispose()
                    .block();
            executorService.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static void addVolume(String name, VolumeManager manager, ExecutorService executorService) {
        IVolume volume = DatabaseStateLoader.fromDatabase(name);
        manager.register(name, volume);
        var updater = KafkaSourceFactory.getNewUpdater("stream-"+name, volume);
        executorService.submit(updater);
    }
}
