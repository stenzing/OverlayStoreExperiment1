package sg.overlay.updater;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.message.AlterPartitionResponseDataJsonConverter;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.shaded.com.google.protobuf.DescriptorProtos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sg.overlay.updater.model.EntryEvent;

import java.io.IOException;
import java.util.Map;

public class EntryEventDeserializer implements Deserializer<EntryEvent> {
    private static final Logger log = LoggerFactory.getLogger(EntryEventDeserializer.class);
    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        Deserializer.super.configure(configs, isKey);
    }

    @Override
    public EntryEvent deserialize(String s, byte[] bytes) {

        log.debug("Received message {}", new String(bytes));
        try {
            return mapper.readValue(bytes, EntryEvent.class);
        } catch (IOException e) {
            log.error("Failed to parse message '{}': {}", new String(bytes), e.getMessage(), e);
            return null;
        }
    }
}
