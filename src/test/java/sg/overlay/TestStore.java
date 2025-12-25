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
        var target = Store.fromVolume(Volume.ofEntries(new Entry("sample1", "myContent", new Metadata())));
        Assertions.assertNotNull(target.getEntry("sample1"));
    }

    @Test
    void testOverlayMatchExisting() {
        var target = Store.fromVolume(Volume.ofEntries(new Entry("sample1", "myContent", new Metadata())));
        Entry newEntry = new Entry("sample1", "myContent2", new Metadata());
        target.addOverlay(Volume.ofEntries(newEntry));
        Assertions.assertEquals(target.getEntry("sample1").get(), newEntry);
    }

    @Test
    void testOverlayMatchOnly() {
        var target = Store.fromVolume(Volume.emptyVolume());
        Entry newEntry = new Entry("sample1", "myContent2", new Metadata());
        target.addOverlay(Volume.ofEntries(newEntry));
        Assertions.assertEquals(target.getEntry("sample1").get(), newEntry);
    }
    @Test
    void testOverlayMatchSkipped() {
        Entry newEntry = new Entry("sample1", "myContent2", new Metadata());
        var target = Store.fromVolume(Volume.ofEntries(newEntry));
        target.addOverlay(Volume.emptyVolume());
        Assertions.assertEquals(target.getEntry("sample1").get(), newEntry);
    }
    @Test
    void testOverlayMiss() {
        var target = Store.empty();
        target.addOverlay(Volume.emptyVolume());
        Assertions.assertThrows(Exception.class, () -> target.getEntry("sample1").orElseThrow());
    }
}
