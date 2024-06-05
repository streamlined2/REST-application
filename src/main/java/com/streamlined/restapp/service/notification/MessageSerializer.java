package com.streamlined.restapp.service.notification;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import org.apache.kafka.common.serialization.Serializer;
import com.streamlined.restapp.exception.KafkaMessageSerializationException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MessageSerializer implements Serializer<Message> {

	private static final int INITIAL_BYTE_STREAM_SIZE = 1000;

	@Override
	public byte[] serialize(String topic, Message message) {
		try (ByteArrayOutputStream byteStream = new ByteArrayOutputStream(INITIAL_BYTE_STREAM_SIZE);
				DataOutputStream dataStream = new DataOutputStream(byteStream)) {
			dataStream.writeLong(message.getInstant().getEpochSecond());
			dataStream.writeUTF(message.getSender().getName());
			dataStream.writeUTF(message.getSender().getEmail());
			dataStream.writeInt(message.getRecipients().size());
			for (Contact recipient : message.getRecipients()) {
				dataStream.writeUTF(recipient.getName());
				dataStream.writeUTF(recipient.getEmail());
			}
			dataStream.writeUTF(message.getSubject());
			dataStream.writeUTF(message.getContent());
			return byteStream.toByteArray();
		} catch (IOException e) {
			log.error("impossible to serialize Kafka message {}", message.toString());
			throw new KafkaMessageSerializationException(
					"impossible to serialize Kafka message %s".formatted(message.toString()), e);
		}
	}

}
