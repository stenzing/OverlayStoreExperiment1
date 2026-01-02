package sg.overlay.updater;

import com.fasterxml.jackson.databind.JsonSerializer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.testcontainers.kafka.KafkaContainer;
import sg.overlay.IVolume;
import sg.overlay.updater.model.EntryDeleteEvent;
import sg.overlay.updater.model.EntryEvent;

import java.util.Properties;

public class KafkaSourceUpdaterTest {
    private static KafkaContainer kafka = new KafkaContainer("apache/kafka-native:3.8.0");

    private static KafkaSourceUpdater connector;
    private static KafkaProducer<String, EntryEvent> producer;

    @Mock
    private IVolume targetVolume;

    @BeforeAll
    static void beforeAll() {
        kafka.start();
    }

    @BeforeEach
    void beforeEach() {
        Mockito.reset(targetVolume);
        var props = consumerProperties();
        connector = new KafkaSourceUpdater("test1", targetVolume, props);
        producer = getProducer();
    }

    @Test
    void testMessageReceive() {
        producer.send(new ProducerRecord<>("stream-01", new EntryDeleteEvent("001")));

        Mockito.verify(targetVolume).deleteEntry(ArgumentMatchers.eq("001"));
    }

    private static @NotNull KafkaProducer<String, EntryEvent> getProducer() {

        var props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class.getName());
        return new KafkaProducer<>(props);
    }

    private static @NotNull Properties consumerProperties() {
        var props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers());
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return props;
    }
}
