package sg.overlay;

import java.util.*;

public class Store {
    private Volume lookup;

    private Store(Volume lookup) {
        this.lookup = lookup;
    }

    public static Store fromVolume(Volume volume) { return new Store(volume);}
    public static Store empty() {return new Store(Volume.emptyVolume());}

    public void addOverlay(Volume newLookup) {
        lookup = newLookup.withBase(lookup);
    }

    public void removeLastOverlay() {
        var prevBase = lookup;
        lookup = lookup.getBase();
        prevBase.withBase(null);
    }

    public Optional<Entry> getEntry(String key) {
        return lookup.getEntry(key);
    }
}
