package sg.overlay.updater;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import sg.overlay.IVolume;

import java.io.IOException;
import java.util.Properties;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KafkaSourceFactory {
    private static final Logger log = LoggerFactory.getLogger(KafkaSourceFactory.class);

    public static KafkaSourceUpdater getNewUpdater(String volumeName, IVolume targetVolume) {
        Properties props = new Properties();
        try {
            props.load(KafkaSourceFactory.class.getResourceAsStream("consumer.properties"));
        } catch (NullPointerException | IOException e) {
            log.warn("Configuration for consumer not found.");
        }
        props.setProperty(ConsumerConfig.CLIENT_ID_CONFIG, "overlay-updater-" + volumeName + "-" + UUID.randomUUID());
        props.setProperty(ConsumerConfig.GROUP_ID_CONFIG, "overlay-updater-" + volumeName);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        return new KafkaSourceUpdater(volumeName, targetVolume, props);
    }
}
