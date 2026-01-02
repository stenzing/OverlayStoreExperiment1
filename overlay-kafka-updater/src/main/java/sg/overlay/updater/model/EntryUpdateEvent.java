package sg.overlay.updater.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("UpdateEvent")
public class EntryUpdateEvent extends EntryEvent {
    private final String content;

    @JsonCreator
    public EntryUpdateEvent(@JsonProperty("key") String key, @JsonProperty("content") String content) {
        super(key);
        this.content = content;
    }

    public String getContent() {
        return content;
    }
}
