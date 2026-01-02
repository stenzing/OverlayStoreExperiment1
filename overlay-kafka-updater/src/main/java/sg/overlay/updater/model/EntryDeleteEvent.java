package sg.overlay.updater.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("DeleteEvent")
public class EntryDeleteEvent extends EntryEvent {
    @JsonCreator
    public EntryDeleteEvent(@JsonProperty("key") String key) {
        super(key);
    }
}
