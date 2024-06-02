package com.streamlined.restapp.service.notification;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.streamlined.restapp.service.notification.kafka.Sender;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DefaultNotificationService implements NotificationService {

	private static final long MESSAGE_DELIVERY_DELAY = 1_000L;

	private final Contact senderContact;
	private final List<Contact> recipientContacts;
	private final BlockingQueue<Message> messageQueue;
	private final Sender sender;

	public DefaultNotificationService(@Qualifier("sender") Contact senderContact,
			@Qualifier("recipients") List<Contact> recipientContacts, Sender sender) {
		this.senderContact = senderContact;
		this.recipientContacts = recipientContacts;
		this.messageQueue = new LinkedBlockingQueue<>();
		this.sender = sender;
	}

	@Override
	public void notify(String entityDescritpion, String cause) {
		String subject = "%s %s".formatted(entityDescritpion, cause);
		LocalDateTime timestamp = LocalDateTime.now();
		String content = "Entity %s has been %s on %tF %tT".formatted(entityDescritpion, cause, timestamp, timestamp);
		addMessage(subject, content);
	}

	@Override
	public void notify(String entityType, Object entity, String cause) {
		String subject = "%s %s".formatted(entityType, cause);
		LocalDateTime timestamp = LocalDateTime.now();
		String content = "Entity %s has been %s on %tF %tT".formatted(entity, cause, timestamp, timestamp);
		addMessage(subject, content);
	}

	private void addMessage(String subject, String content) {
		Message message = new Message(senderContact, recipientContacts, subject, content);
		messageQueue.add(message);
	}

	@Scheduled(fixedDelay = MESSAGE_DELIVERY_DELAY)
	private void deliver() {
		for (Message message; (message = messageQueue.poll()) != null;) {
			log.info(message.toString());
			sender.send(message);
		}
	}

}
