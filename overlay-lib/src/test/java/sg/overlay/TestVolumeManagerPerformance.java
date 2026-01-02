package sg.overlay;

import org.apache.commons.lang.time.StopWatch;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class TestVolumeManagerPerformance {
    private static final Random rnd = new Random();
    private static final Runtime runtime = Runtime.getRuntime();

    public static class Measurement implements AutoCloseable {
        private final String step;
        private final Number units;
        private final long memoryBefore;
        private final StopWatch watch;


        public Measurement(String step) {
            this(step,null);
        }

        public Measurement(String step, Number units) {
            this.step = step;
            watch = new StopWatch();
            this.units = units;
            this.memoryBefore = runtime.totalMemory() - runtime.freeMemory();
            watch.start();
        }

        @Override
        public void close() {
            watch.stop();
            var usedMemoryAfter = runtime.totalMemory() - runtime.freeMemory();
            System.out.println("Memory usage for " + step + ": " + (usedMemoryAfter - this.memoryBefore) / (1024 * 1024.0) + " MB");
            System.out.println("Time taken for " + step + ": " + watch.getTime() + " ms");
            if (units != null) {
                System.out.println("EPS " + (units.longValue() / (watch.getTime() / 1000.0)) + " exec/sec");
            }
        }
    }

    @Test
    void test_big_data() {

        long usedMemoryBefore = runtime.totalMemory() - runtime.freeMemory();
        Entry[] entries = getEntries(10000000);
        long usedMemoryAfter = runtime.totalMemory() - runtime.freeMemory();
        System.out.println("Memory used by " + entries.length + " entries: " + (usedMemoryAfter - usedMemoryBefore) / (1024 * 1024.0) + " MB");
        var store = new VolumeManager();

        try (Measurement _ = new Measurement("Load Volume 1")){
            store.register("base", BaseVolume.ofEntries(entries));
        }

        var layerSize = 100000;
        try (Measurement _ = new Measurement("Load Volume 2 ("+layerSize+" entries)")){
            store.register("01", buildRandomOverlay(entries, layerSize));
        }
        layerSize = 100000;
        try (Measurement _ = new Measurement("Load Volume 3 ("+layerSize+" entries)")){
            store.register("02", buildRandomOverlay(entries, layerSize));
        }
        layerSize = 10000;
        try (Measurement _ = new Measurement("Load Volume 4 ("+layerSize+" entries)")){
            store.register("03", buildRandomOverlay(entries, layerSize));
        }
        int reads = 1000;
        try (Measurement _ = new Measurement("Random reads ("+reads+" reads)", reads)){
            getRandomStringStream(entries.length, reads)
                    .parallel()
                    .forEach(key -> store.ofStructure(List.of("base", "01", "02", "03")).getEntry(key));
        }
        reads = 100000;
        try (Measurement _ = new Measurement("Random reads ("+reads+" reads)", reads)){
            getRandomStringStream(entries.length, reads)
                    .parallel()
                    .forEach(key -> store.ofStructure(List.of("base", "01", "02", "03")).getEntry(key));
        }
        reads = 10000000;
        try (Measurement _ = new Measurement("Random reads ("+reads+" reads)", reads)){
            getRandomStringStream(entries.length, reads)
                    .parallel()
                    .forEach(key -> store.ofStructure(List.of("base", "01", "02", "03")).getEntry(key));
        }
    }

    private static IVolume buildRandomOverlay(Entry[] entries, int size) {
        var overlay = getRandomIntegerStream(entries.length, size)
                .map(id -> entries[id])
                .toArray(Entry[]::new);
        return BaseVolume.ofEntries(overlay);
    }

    private static Stream<String> getRandomStringStream(int bucket, int size) {
        return getRandomIntegerStream(bucket, size)
                .map(i -> "file_" + i);
    }

    private static Stream<Integer> getRandomIntegerStream(int bucket, int size) {
        return IntStream.range(0, size)
                .boxed()
                .map(_ -> rnd.nextInt(1, bucket));
    }

    private static Entry[] getEntries(int num) {
        String content = rnd.ints(100).boxed().map(Object::toString).collect(Collectors.joining());
        return IntStream.range(1, num + 1)
                .mapToObj(i -> new Entry("file_" + i, content, Metadata.DEFAULT))
                .toArray(Entry[]::new);
    }
}
