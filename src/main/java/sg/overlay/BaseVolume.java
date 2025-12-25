package sg.overlay;

import java.util.*;
import java.util.stream.Collectors;

public class BaseVolume {
    private final Map<String, Entry> lookup = new TreeMap<>();

    BaseVolume() {
    }

    public Optional<Entry> getEntry(String key) {
        return Optional.ofNullable(lookup.get(key));
    }

    protected void addEntry(Entry entry) {
        lookup.put(entry.path(), entry);
    }

    public Collection<Entry> getEntriesByPrefix(String prefix) {
        return lookup.entrySet()
                .stream()
                .filter(e -> e.getKey().startsWith(prefix))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }
}
