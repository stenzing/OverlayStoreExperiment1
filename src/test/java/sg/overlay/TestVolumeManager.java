package sg.overlay;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class TestVolumeManager {
    public static final String SOME_PATH = "some_path";
    Entry MOCK_ENTRY_1 = new Entry(SOME_PATH, "some content", Metadata.DEFAULT);
    Entry MOCK_ENTRY_2 = new Entry("some_path", "", Metadata.DELETED);

    @Test
    void testInit() {
        var target = new VolumeManager();
        Assertions.assertNotNull(target);
    }

    @Test
    void testRegistering() {
        var target = new VolumeManager();

        target.register("01", BaseVolume.ofEntries());

        Assertions.assertNotNull(target.ofStructure(List.of("01")));
    }

    @Test
    void testOrdering1() {
        var target = new VolumeManager();

        target.register("01", BaseVolume.ofEntries(MOCK_ENTRY_1));
        target.register("02", BaseVolume.ofEntries(MOCK_ENTRY_2));

        var storage = target.ofStructure(List.of("01","02"));

        Assertions.assertEquals(MOCK_ENTRY_1, storage.getEntry(SOME_PATH).orElseThrow());
    }

    @Test
    void testOrdering2() {
        var target = new VolumeManager();

        target.register("01", BaseVolume.ofEntries(MOCK_ENTRY_2));
        target.register("02", BaseVolume.ofEntries(MOCK_ENTRY_1));

        var storage = target.ofStructure(List.of("01","02"));

        Assertions.assertFalse(storage.getEntry(SOME_PATH).isPresent());
    }
}
