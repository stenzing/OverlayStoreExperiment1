package sg.overlay;

import java.util.*;
import java.util.stream.Collectors;

public class LayerableVolume implements IVolume, HasParent<IVolume> {

    private final IVolume baseVolume;
    private IVolume parentVolume = null;

    private LayerableVolume(IVolume baseVolume) {
        this.baseVolume= baseVolume;
    }

    public static LayerableVolume emptyVolume() {
        return new LayerableVolume(new BaseVolume());
    }

    public static LayerableVolume from(IVolume base) {
        return new LayerableVolume(base);
    }


    public Optional<Entry> getEntry(String key) {
        return baseVolume.getEntry(key)
                .or(() -> parentVolume != null ? parentVolume.getEntry(key) : Optional.empty())
                .filter(e -> !e.metadata().isDeleted());
    }

    public IVolume withParent(IVolume volume) {
        parentVolume = volume;
        return this;
    }

    public IVolume getParent() {
        return parentVolume;
    }

    public Collection<Entry> getEntriesByPrefix(String prefix) {
        LinkedHashMap<String, Entry> aggr = new LinkedHashMap<>();
        baseVolume.getEntriesByPrefix(prefix).forEach(e -> aggr.put(e.path(), e));
        if (parentVolume!= null) {
            parentVolume.getEntriesByPrefix(prefix)
                    .forEach(e -> aggr.putIfAbsent(e.path(), e));
        }
        return aggr.values().stream()
                .filter(e -> !e.metadata().isDeleted())
                .collect(Collectors.toList());
    }
}
