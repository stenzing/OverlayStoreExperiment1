package sg.overlay.api;

import io.netty.handler.codec.http.HttpResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.DisposableServer;
import reactor.netty.http.server.HttpServer;
import sg.overlay.*;
import sg.overlay.api.service.TestDataGenerator;
import sg.overlay.backend.DatabaseStateLoader;
import sg.overlay.updater.KafkaSourceFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.IntStream;

public class Application {

    private static final VolumeManager manager = new VolumeManager();
    private static final Logger log = LoggerFactory.getLogger(Application.class);


    static void main(String[] args) throws SQLException, IOException {

        var connection = getConnection();

        TestDataGenerator.generateData(connection);
        DisposableServer server =
                HttpServer.create()
                        .host("localhost")
                        .port(8080)
                        .route(routes -> routes.get("/{volumes}/{path}",
                                (request, response) -> {
                                    var entry = manager
                                            .ofStructure(Arrays.stream(request.param("volumes").split("\\.")).toList())
                                            .getEntry(request.param("path"));
                                    if (entry.isPresent()) {
                                        return response.status(HttpResponseStatus.OK)
                                                .sendString(Mono.just(entry.get().content()));
                                    }
                                    return response.sendNotFound();
                                })
                                .get("/search/{volumes}/{prefix}",
                                        (request, response) -> {
                                            var items = manager
                                                    .ofStructure(Arrays.stream(request.param("volumes").split("\\.")).toList())
                                                    .getEntriesByPrefix(request.param("prefix")).stream();
                                            return response.status(HttpResponseStatus.OK)
                                                    .sendString(Flux.fromStream(items).map(Entry::content));
                                        }
                                        ))
                        .bindNow();


        try (ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor()) {
            addVolume("01", manager, executorService, connection);
            addVolume("02", manager, executorService, connection);
            addVolume("03a", manager, executorService, connection);
            addVolume("03b", manager, executorService, connection);
            server.onDispose()
                    .block();
            executorService.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static Connection getConnection() throws SQLException, IOException {
        var connection = DriverManager.getConnection("jdbc:h2:mem:testcase", "sa", null);
        var stmt = connection.createStatement();
        try (var db_script = Application.class.getClassLoader().getResourceAsStream("schema.sql")) {
            if (db_script != null) {
                new BufferedReader(new InputStreamReader(db_script))
                        .lines()
                        .forEach(
                                l -> {
                                    try {
                                        if (!l.isBlank()) stmt.execute(l);
                                    } catch (SQLException e) {
                                        log.error("DB INIT FAILED, ", e);
                                    }
                                }
                        );
            } else {
                throw new IOException("Schema file missing");
            }
        }
        connection.commit();
        return connection;
    }


    private static void addVolume(String name, VolumeManager manager, ExecutorService executorService, Connection connection) {
        IVolume volume;
        try {
            volume = DatabaseStateLoader.fromDatabase(connection, name);
        } catch (SQLException e) {
            log.error("Error in loading initial state", e);
            volume = BaseVolume.ofEntries();
        }
        manager.register(name, volume);
        var updater = KafkaSourceFactory.getNewUpdater("stream-" + name, volume);
        executorService.submit(updater);
    }
}
