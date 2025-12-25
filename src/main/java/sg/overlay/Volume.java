package sg.overlay;

import java.util.*;
import java.util.stream.Collectors;

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
                .or(() -> baseVolume != null ? baseVolume.getEntry(key) : Optional.empty())
                .filter(e -> !e.metadata().isDeleted());
    }

    public Volume withBase(Volume volume) {
        baseVolume = volume;
        return this;
    }

    public Volume getBase() {
        return baseVolume;
    }

    public Collection<Entry> getEntriesByPrefix(String prefix) {
        LinkedHashMap<String, Entry> aggr = new LinkedHashMap<>();
        super.getEntriesByPrefix(prefix).forEach(e -> aggr.put(e.path(), e));
        if (baseVolume!= null) {
            baseVolume.getEntriesByPrefix(prefix)
                    .forEach(e -> aggr.putIfAbsent(e.path(), e));
        }
        return aggr.values().stream()
                .filter(e -> !e.metadata().isDeleted())
                .collect(Collectors.toList());
    }
}
