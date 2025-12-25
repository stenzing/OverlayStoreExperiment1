package sg.overlay;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestStore {

    @Test
    void testInit() {
        var target = Store.empty();
        Assertions.assertNotNull(target);
    }

    @Test
    void testGetSomethingNotExisting() {
        var target = Store.empty();
        Assertions.assertThrows(Exception.class, () -> target.getEntry("sample1").orElseThrow());
    }

    @Test
    void testGetSomethingExisting() {
        var target = Store.fromVolume(Volume.ofEntries(new Entry("sample1", "myContent", Metadata.DEFAULT)));
        Assertions.assertNotNull(target.getEntry("sample1"));
    }

    @Test
    void testOverlayMatchExisting() {
        var target = Store.fromVolume(Volume.ofEntries(new Entry("sample1", "myContent", Metadata.DEFAULT)));
        Entry newEntry = new Entry("sample1", "myContent2", Metadata.DEFAULT);
        target.addOverlay(Volume.ofEntries(newEntry));
        Assertions.assertEquals(target.getEntry("sample1").orElseThrow(), newEntry);
    }

    @Test
    void testOverlayMatchOnly() {
        var target = Store.fromVolume(Volume.emptyVolume());
        Entry newEntry = new Entry("sample1", "myContent2", Metadata.DEFAULT);
        target.addOverlay(Volume.ofEntries(newEntry));
        Assertions.assertEquals(target.getEntry("sample1").orElseThrow(), newEntry);
    }

    @Test
    void testOverlayMatchSkipped() {
        Entry newEntry = new Entry("sample1", "myContent2", Metadata.DEFAULT);
        var target = Store.fromVolume(Volume.ofEntries(newEntry));
        target.addOverlay(Volume.emptyVolume());
        Assertions.assertEquals(target.getEntry("sample1").orElseThrow(), newEntry);
    }

    @Test
    void testOverlayMiss() {
        var target = Store.empty();
        target.addOverlay(Volume.emptyVolume());
        Assertions.assertThrows(Exception.class, () -> target.getEntry("sample1").orElseThrow());
    }

    @Test
    void testOverlayDeletes() {
        Entry originalEntry = new Entry("sample1", "myContent2", Metadata.DEFAULT);
        var target = Store.fromVolume(Volume.ofEntries(originalEntry));
        var deletingVolume = Volume.ofEntries(new Entry("sample1", "", Metadata.DELETED));
        target.addOverlay(deletingVolume);
        Assertions.assertTrue(target.getEntry("sample1").isEmpty());
    }

    @Test
    void testSeparateOverlays() {
        Entry originalEntry = new Entry("sample1", "myContent2", Metadata.DEFAULT);
        Volume baseVolume = Volume.ofEntries(originalEntry);
        var deletingVolume = Volume.ofEntries(new Entry("sample1", "", Metadata.DELETED));
        var store1 = Store.fromVolume(baseVolume);
        store1.addOverlay(deletingVolume);
        var store2 = Store.fromVolume(baseVolume);
        Entry newEntry = new Entry("sample1", "contentB", Metadata.DEFAULT);
        var updatingVolume = Volume.ofEntries(newEntry);
        store2.addOverlay(updatingVolume);
        Assertions.assertTrue(store1.getEntry("sample1").isEmpty());
        Assertions.assertEquals(newEntry, store2.getEntry("sample1").orElseThrow());
    }
}
