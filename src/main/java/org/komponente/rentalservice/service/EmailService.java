package org.komponente.rentalservice.service;

import java.io.Serializable;

public interface EmailService {
    void sendMessage(Serializable message, String queueName);
}
