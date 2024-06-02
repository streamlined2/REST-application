package com.streamlined.restapp.service.notification;

import java.io.Serializable;
import java.util.Objects;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class Contact implements Serializable {

	private String name;
	private String email;

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Contact contact) {
			return Objects.equals(email, contact.email);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(email);
	}

	@Override
	public String toString() {
		return "%s (%s)".formatted(name, email);
	}

}
