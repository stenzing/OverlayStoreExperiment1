package sg.overlay;

import java.util.*;

public class Volume extends BaseVolume {

    private Volume baseVolume = null;

    private Volume() {
        super();
    }

    public static Volume emptyVolume() {
        return new Volume();
    }

    public static Volume ofEntries(Entry... entries) {
        Volume vo = new Volume();
        Arrays.stream(entries)
                .forEach(vo::addEntry);
        return vo;
    }

    public Optional<Entry> getEntry(String key) {
        return super.getEntry(key)
                .or(() -> baseVolume != null ? baseVolume.getEntry(key) : Optional.empty());
    }

    public Volume withBase(Volume volume) {
        baseVolume = volume;
        return this;
    }

    public Volume getBase() {
        return baseVolume;
    }
}
