package sg.overlay;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

@SuppressWarnings("SpellCheckingInspection")
public class TestLayerableVolume {

    @Test
    void testInit() {
        var target = LayerableVolume.emptyVolume();
        Assertions.assertNotNull(target);
    }

    @Test
    void testGetSomethingNotExisting() {
        var target = LayerableVolume.emptyVolume();
        Assertions.assertThrows(Exception.class, () -> target.getEntry("sample1").orElseThrow());
    }

    @Test
    void testGetSomethingExisting() {
        var target = LayerableVolume.from(BaseVolume.ofEntries(new Entry("sample1", "myContent", Metadata.DEFAULT)));
        Assertions.assertNotNull(target.getEntry("sample1"));
    }

    @Test
    void testOverlayMatchExisting() {
        var base = LayerableVolume.from(BaseVolume.ofEntries(new Entry("sample1", "myContent", Metadata.DEFAULT)));
        Entry entry = new Entry("sample1", "myContent2", Metadata.DEFAULT);
        var target = LayerableVolume.from(BaseVolume.ofEntries(entry));
        target.withParent(base);
        Assertions.assertEquals(target.getEntry("sample1").orElseThrow(), entry);
    }

    @Test
    void testOverlayMatchOnly() {
        var base = LayerableVolume.emptyVolume();
        Entry newEntry = new Entry("sample1", "myContent2", Metadata.DEFAULT);
        var target = LayerableVolume.from(BaseVolume.ofEntries(newEntry));
        target.withParent(base);
        Assertions.assertEquals(target.getEntry("sample1").orElseThrow(), newEntry);
    }

    @Test
    void testOverlayMatchSkipped() {
        Entry newEntry = new Entry("sample1", "myContent2", Metadata.DEFAULT);
        var base = LayerableVolume.from(BaseVolume.ofEntries(newEntry));
        var target = LayerableVolume.emptyVolume();
        target.withParent(base);
        Assertions.assertEquals(target.getEntry("sample1").orElseThrow(), newEntry);
    }

    @Test
    void testOverlayMiss() {
        var base = LayerableVolume.emptyVolume();
        var target = LayerableVolume.emptyVolume();
        target.withParent(base);
        Assertions.assertThrows(Exception.class, () -> target.getEntry("sample1").orElseThrow());
    }

    @Test
    void testOverlayDeletes() {
        Entry originalEntry = new Entry("sample1", "myContent2", Metadata.DEFAULT);
        var base = BaseVolume.ofEntries(originalEntry);
        var deletingVolume = LayerableVolume.from(BaseVolume.ofEntries(new Entry("sample1", "", Metadata.DELETED)));
        deletingVolume.withParent(base);
        Assertions.assertTrue(deletingVolume.getEntry("sample1").isEmpty());
    }

    @Test
    void testSeparateOverlays() {
        Entry originalEntry = new Entry("sample1", "myContent2", Metadata.DEFAULT);
        var baseVolume = BaseVolume.ofEntries(originalEntry);
        var deletingVolume = BaseVolume.ofEntries(new Entry("sample1", "", Metadata.DELETED));
        var store1 = LayerableVolume.from(deletingVolume).withParent(baseVolume);
        Entry newEntry = new Entry("sample1", "contentB", Metadata.DEFAULT);
        var store2 = LayerableVolume.from(BaseVolume.ofEntries(newEntry)).withParent(baseVolume);

        Assertions.assertTrue(store1.getEntry("sample1").isEmpty());
        Assertions.assertEquals(newEntry, store2.getEntry("sample1").orElseThrow());
    }

    @Test
    void testListingByPrefix() {
        var entry1 = new Entry("prefixa/11", "contnt1", Metadata.DEFAULT);
        var entry2 = new Entry("prefixa/12", "contnt2", Metadata.DEFAULT);
        var entry3 = new Entry("prefixa/13", "contnt1", Metadata.DEFAULT);
        var entry3b = new Entry("prefixa/13", "", Metadata.DELETED);
        var entry4 = new Entry("prefixa/14", "contnt1", Metadata.DEFAULT);
        var entry4b = new Entry("prefixa/14", "contnt2", Metadata.DEFAULT);
        var entry5 = new Entry("prefixb/11", "contnt1", Metadata.DEFAULT);
        var entry6 = new Entry("prefixb/12", "contnt2", Metadata.DEFAULT);
        var store = LayerableVolume
                .from(BaseVolume.ofEntries(entry2, entry3b, entry4b, entry6))
                .withParent(BaseVolume.ofEntries(entry1, entry3, entry4, entry5));

        var result = store.getEntriesByPrefix("prefixa");

        Assertions.assertTrue(result.containsAll(List.of(entry1, entry2, entry4b)));
        Assertions.assertTrue(result.stream().noneMatch(e -> List.of(entry3, entry4, entry5, entry6).contains(e)));
    }
}
