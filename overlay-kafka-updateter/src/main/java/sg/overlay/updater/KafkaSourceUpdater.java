package sg.overlay.updater;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sg.overlay.Entry;
import sg.overlay.IVolume;
import sg.overlay.Metadata;
import sg.overlay.updater.model.EntryDeleteEvent;
import sg.overlay.updater.model.EntryEvent;
import sg.overlay.updater.model.EntryUpdateEvent;

import java.time.Duration;
import java.util.List;
import java.util.Properties;

public class KafkaSourceUpdater implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(KafkaSourceUpdater.class);
    private final KafkaConsumer<String, EntryEvent> consumer;
    private final String volumeName;
    private final IVolume targetVolume;

    public KafkaSourceUpdater(String volumeName, IVolume targetVolume, Properties kafkaProperties) {
        this.volumeName = volumeName;
        this.targetVolume = targetVolume;
        consumer = new KafkaConsumer<>(kafkaProperties, new StringDeserializer(), new EntryEventDeserializer());
    }

    @Override
    public void run() {
        consumer.subscribe(List.of(volumeName));
        try {
            while (true) {
                var records = consumer.poll(Duration.ofSeconds(3));
                for (ConsumerRecord<String, EntryEvent> record : records) {
                    processRecord(record.value());
                }
            }
        } finally {
            consumer.close();
        }
    }

    void processRecord(EntryEvent event) {
        if (event instanceof EntryUpdateEvent update) {
            targetVolume.addEntry(new Entry(update.getKey(), update.getContent(), Metadata.DEFAULT));
        } else if (event instanceof EntryDeleteEvent delete) {
            targetVolume.deleteEntry(delete.getKey());
        } else {
            log.error("Event not processed: ({}) {}", event.getKey(), event);
        }

    }
}
