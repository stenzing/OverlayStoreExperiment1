package sg.overlay.updater;

import org.junit.jupiter.api.Test;
import sg.overlay.updater.model.EntryDeleteEvent;
import sg.overlay.updater.model.EntryUpdateEvent;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class EntryEventDeserializerTest {
    @Test
    void testInit() {
        var target = new EntryEventDeserializer();
        assertNotNull(target);
    }

    @Test
    void testDecodeDeleteEvent() {
        var target = new EntryEventDeserializer();

        String content = "{ \"key\": \"0001\", \"type\": \"DeleteEvent\"}";
        var result = target.deserialize(content, content.getBytes(StandardCharsets.UTF_8));
        assertNotNull(result);
        assertEquals("0001", result.getKey());
        assertInstanceOf(EntryDeleteEvent.class, result);
    }

    @Test
    void testDecodeUpdateEvent() {
        var target = new EntryEventDeserializer();

        String content = "{ \"key\": \"0001\", \"type\": \"UpdateEvent\", \"content\": \"someData\"}";
        var result = target.deserialize(content, content.getBytes(StandardCharsets.UTF_8));
        assertNotNull(result);
        assertEquals("0001", result.getKey());
        assertInstanceOf(EntryUpdateEvent.class, result);
        assertEquals("someData", ((EntryUpdateEvent) result).getContent());

    }
}