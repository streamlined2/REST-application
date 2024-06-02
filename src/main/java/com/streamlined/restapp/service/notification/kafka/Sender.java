package com.streamlined.restapp.service.notification.kafka;

import com.streamlined.restapp.service.notification.Message;

public interface Sender {

	void send(Message notification);

}
