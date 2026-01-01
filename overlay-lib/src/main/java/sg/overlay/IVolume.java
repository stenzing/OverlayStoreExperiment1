package sg.overlay;

import java.util.Collection;
import java.util.Optional;

public interface IVolume {
    void addEntry(Entry entry);
    void deleteEntry(String key);
    Optional<Entry> getEntry(String key);
    Collection<Entry> getEntriesByPrefix(String prefix);
}
