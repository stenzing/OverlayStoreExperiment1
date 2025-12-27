package sg.overlay;

import java.util.Collection;
import java.util.Optional;

public interface IVolume {
    Optional<Entry> getEntry(String key);
    Collection<Entry> getEntriesByPrefix(String prefix);
}
