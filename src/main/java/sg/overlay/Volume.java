package sg.overlay;

import java.util.*;

public class Volume {
    private final Map<String, Entry> lookup = new TreeMap<>();


    private Volume baseVolume = null;

    private Volume() {}

    public static Volume emptyVolume() { return new Volume();}
    public static Volume ofEntries(Entry ... entries) {
        Volume vo = new Volume();
        Arrays.stream(entries)
                .forEach(vo::addEntry);
        return vo;
    }

    public Volume withBase(Volume volume) {
        baseVolume = volume;
        return this;
    }

    public Optional<Entry> getEntry(String key) {
        return Optional.ofNullable(lookup.get(key))
                .or(() -> baseVolume!=null?baseVolume.getEntry(key):Optional.empty());
    }

    private void addEntry(Entry entry) {
        lookup.put(entry.path(), entry);
    }

    public Volume getBase() {
        return baseVolume;
    }
}
