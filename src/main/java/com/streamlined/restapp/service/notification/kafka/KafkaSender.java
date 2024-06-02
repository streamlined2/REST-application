package com.streamlined.restapp.service.notification.kafka;

import java.util.concurrent.CompletableFuture;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import com.streamlined.restapp.exception.CantSendKafkaMessageException;
import com.streamlined.restapp.service.notification.Message;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class KafkaSender implements Sender {

	private final KafkaAdmin kafkaAdmin;
	private final KafkaTemplate<String, Message> kafkaTemplate;
	private final NewTopic topic;

	private KafkaSender(@Value("${notification.topic-name}") String topicName,
			@Value("${notification.number-of-partitions}") int numberOfPartitions,
			@Value("${notification.replication-factor}") short replicationFactor, KafkaAdmin kafkaAdmin,
			KafkaTemplate<String, Message> kafkaTemplate) {
		this.topic = new NewTopic(topicName, numberOfPartitions, replicationFactor);
		this.kafkaAdmin = kafkaAdmin;
		this.kafkaTemplate = kafkaTemplate;
	}

	@Override
	public void send(Message event) {
		kafkaAdmin.createOrModifyTopics(topic);
		CompletableFuture<SendResult<String, Message>> result = kafkaTemplate.send(topic.name(), event);
		result.whenComplete((rst, exc) -> {
			if (exc != null) {
				log.error("impossible to send message {} to Kafka topic {}", rst.getProducerRecord().value(),
						rst.getProducerRecord().topic());
				throw new CantSendKafkaMessageException("impossible to send message %s to Kafka topic %s"
						.formatted(rst.getProducerRecord().value().toString(), rst.getProducerRecord().topic()), exc);
			}
		});
	}

}
