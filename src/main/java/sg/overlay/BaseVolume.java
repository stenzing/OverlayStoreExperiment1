package sg.overlay;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

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
}
