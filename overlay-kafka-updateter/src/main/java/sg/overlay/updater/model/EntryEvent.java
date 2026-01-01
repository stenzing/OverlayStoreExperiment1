package sg.overlay.updater.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = EntryDeleteEvent.class, name = "DeleteEvent"),
        @JsonSubTypes.Type(value = EntryUpdateEvent.class, name = "UpdateEvent")
})
public abstract class EntryEvent {
    private final String key;

    protected EntryEvent(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
