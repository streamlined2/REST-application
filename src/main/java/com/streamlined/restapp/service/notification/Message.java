package com.streamlined.restapp.service.notification;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Message implements Serializable {

	private final Instant instant;
	private final Contact sender;
	private final List<Contact> recipients;
	private final String subject;
	private final String content;

	public Message(Contact sender, List<Contact> recipients, String subject, String content) {
		this.instant = Instant.now();
		this.sender = sender;
		this.recipients = recipients;
		this.subject = subject;
		this.content = content;
	}

	@Override
	public int hashCode() {
		return Objects.hash(instant, sender, subject);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Message event) {
			return Objects.equals(instant, event.instant) && Objects.equals(sender, event.sender)
					&& Objects.equals(subject, event.subject);
		}
		return false;
	}

	@Override
	public String toString() {
		return "Sender=%s, recipients=%s, subject=%s, content=%s".formatted(sender, recipients, subject, content);
	}

}
