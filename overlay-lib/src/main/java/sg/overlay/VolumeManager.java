package sg.overlay;

import java.util.*;
import java.util.stream.Gatherers;

public class VolumeManager {
    private final Map<String, IVolume> registry = new HashMap<>();

    public void register(String id, IVolume newLookup) {
        registry.put(id, newLookup);
    }

    public IVolume ofStructure(Collection<String> ids) {
        return ids.stream()
                .map(id -> registry.get(id))
                .gather(Gatherers.scan(() -> LayerableVolume.emptyVolume(), (layerableVolume, iVolume) -> (LayerableVolume) LayerableVolume.from(iVolume).withParent(layerableVolume)))
                .findFirst().orElseThrow();

    }
}
