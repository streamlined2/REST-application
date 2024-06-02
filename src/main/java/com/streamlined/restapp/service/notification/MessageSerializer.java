package com.streamlined.restapp.service.notification;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import org.apache.kafka.common.serialization.Serializer;
import com.streamlined.restapp.exception.KafkaMessageSerializationException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MessageSerializer implements Serializer<Message> {

	private static final int INITIAL_BYTE_STREAM_SIZE = 1000;

	@Override
	public byte[] serialize(String topic, Message event) {
		try (ByteArrayOutputStream byteStream = new ByteArrayOutputStream(INITIAL_BYTE_STREAM_SIZE);
				ObjectOutputStream dataStream = new ObjectOutputStream(byteStream)) {
			dataStream.writeObject(event);
			return byteStream.toByteArray();
		} catch (IOException e) {
			log.error("impossible to serialize Kafka message {}", event.toString());
			throw new KafkaMessageSerializationException(
					"impossible to serialize Kafka message %s".formatted(event.toString()), e);
		}
	}

}
