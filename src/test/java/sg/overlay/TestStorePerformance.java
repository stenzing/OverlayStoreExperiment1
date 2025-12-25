package sg.overlay;

import org.apache.commons.lang.time.StopWatch;
import org.junit.jupiter.api.Test;

import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class TestStorePerformance {
    private static final Random rnd = new Random();

    @Test
    void test_big_data() {
        Runtime runtime = Runtime.getRuntime();
        long usedMemoryBefore = runtime.totalMemory() - runtime.freeMemory();
        Entry[] entries = getEntries(10000000);
        long usedMemoryAfter = runtime.totalMemory() - runtime.freeMemory();
        System.out.println("Memory used by " + entries.length + " entries: " + (usedMemoryAfter - usedMemoryBefore) / (1024 * 1024.0) + " MB");
        var watch = new StopWatch();

        usedMemoryBefore = runtime.totalMemory() - runtime.freeMemory();
        watch.start();
        var store = Store.fromVolume(Volume.ofEntries(entries));
        watch.stop();
        usedMemoryAfter = runtime.totalMemory() - runtime.freeMemory();
        System.out.println("Memory overhead for volume of " + entries.length + " entries: " + (usedMemoryAfter - usedMemoryBefore) / (1024 * 1024.0) + " MB");
        System.out.println("DEBUG: Loading into volume: " + watch.getTime() + " ms");

        watch.reset();
        usedMemoryBefore = runtime.totalMemory() - runtime.freeMemory();
        watch.start();
        var layerSize = 100000;
        buildRandomOverlay(entries, store, layerSize);
        watch.stop();
        usedMemoryAfter = runtime.totalMemory() - runtime.freeMemory();
        System.out.println("Memory increased for volume of " + layerSize + " entries: " + (usedMemoryAfter - usedMemoryBefore) / (1024 * 1024.0) + " MB");
        System.out.println("DEBUG: Loading into volume: " + watch.getTime() + " ms");

        watch.reset();
        usedMemoryBefore = runtime.totalMemory() - runtime.freeMemory();
        watch.start();
        buildRandomOverlay(entries, store, layerSize);
        watch.stop();
        usedMemoryAfter = runtime.totalMemory() - runtime.freeMemory();
        System.out.println("Memory increased for volume of " + layerSize + " entries: " + (usedMemoryAfter - usedMemoryBefore) / (1024 * 1024.0) + " MB");
        System.out.println("DEBUG: Loading into volume: " + watch.getTime() + " ms");

        watch.reset();
        usedMemoryBefore = runtime.totalMemory() - runtime.freeMemory();
        watch.start();
        layerSize = 1000;
        buildRandomOverlay(entries, store, layerSize);
        watch.stop();
        usedMemoryAfter = runtime.totalMemory() - runtime.freeMemory();
        System.out.println("Memory increased for volume of " + layerSize + " entries: " + (usedMemoryAfter - usedMemoryBefore) / (1024 * 1024.0) + " MB");
        System.out.println("DEBUG: Loading into volume: " + watch.getTime() + " ms");

        watch.reset();
        watch.start();
        int reads = 10000000;
        getRandomFileStream(entries.length, reads)
                .parallel()
                .forEach(store::getEntry);
        watch.stop();
        System.out.println("DEBUG: Querying " + reads + " times: " + watch.getTime() + " ms");
        System.out.println("DEBUG: TPS " + (reads / (watch.getTime() / 1000.0)) + " read/s");
    }

    private static void buildRandomOverlay(Entry[] entries, Store store, int size) {
        System.out.println("DEBUG: Building volume of  " + size + " entries");
        var overlay = getIntegerStream(entries.length, size)
                .map(id -> entries[id])
                .toArray(Entry[]::new);
        store.addOverlay(Volume.ofEntries(overlay));
    }

    private static Stream<String> getRandomFileStream(int bucket, int size) {
        return getIntegerStream(bucket, size)
                .map(i -> "file_" + i);
    }

    private static Stream<Integer> getIntegerStream(int bucket, int size) {
        return IntStream.range(0, size)
                .boxed()
                .map(_ -> rnd.nextInt(1, bucket));
    }

    private static Entry[] getEntries(int num) {
        String content = rnd.ints(100).boxed().map(Object::toString).collect(Collectors.joining());
        return IntStream.range(1, num+1)
                .mapToObj(i -> new Entry("file_" + i, content, Metadata.DEFAULT))
                .toArray(Entry[]::new);
    }
}
