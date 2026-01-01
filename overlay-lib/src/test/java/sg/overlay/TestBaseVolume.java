package sg.overlay;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class TestBaseVolume {

    public static final String DUMMY_KEY = "sample1";
    public static final Entry ENTRY = new Entry(DUMMY_KEY, "myContent", Metadata.DEFAULT);

    @Test
    void testInit() {
        var target = new BaseVolume();
        Assertions.assertNotNull(target);
    }

    @Test
    void testAddingAnItem() {
        var target = new BaseVolume();
        target.addEntry(ENTRY);
        Assertions.assertTrue(target.getEntry(DUMMY_KEY).isPresent());
    }

    @Test
    void testItemNotAdded() {
        var target = new BaseVolume();
        Assertions.assertFalse(target.getEntry(DUMMY_KEY).isPresent());
    }

    @Test
    void testRemovingItem() {
        var target = BaseVolume.ofEntries(ENTRY);
        Assertions.assertTrue(target.getEntry(DUMMY_KEY).isPresent());
        target.deleteEntry(DUMMY_KEY);
        Assertions.assertFalse(target.getEntry(DUMMY_KEY).isPresent());
    }

    @Test
    void testListingByPrefix() {
        var entry1 = new Entry("prefixa/11", "contnt1", Metadata.DEFAULT);
        var entry2 = new Entry("prefixa/12", "contnt1", Metadata.DEFAULT);
        var entry3 = new Entry("prefixb/11", "contnt1", Metadata.DEFAULT);

        var target = new BaseVolume();
        target.addEntry(entry1);
        target.addEntry(entry2);
        target.addEntry(entry3);

        var result = target.getEntriesByPrefix("prefixa");

        Assertions.assertTrue(result.containsAll(List.of(entry1, entry2)));
        Assertions.assertFalse(result.stream().anyMatch(e -> e.equals(entry3)));
    }
}
