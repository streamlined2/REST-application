package com.streamlined.restapp.service.notification;

public interface NotificationService {

	void notify(String entityDescritpion, String cause);

	void notify(String entityType, Object entity, String cause);

}
