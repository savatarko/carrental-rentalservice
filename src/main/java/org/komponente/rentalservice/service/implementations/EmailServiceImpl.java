package org.komponente.rentalservice.service.implementations;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.komponente.rentalservice.service.EmailService;
import org.springframework.stereotype.Service;

import javax.jms.*;
import java.io.Serializable;

@Service
public class EmailServiceImpl implements EmailService {
    @Override
    public void sendMessage(Serializable content, String queueName) {
        try {
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
            Connection connection = connectionFactory.createConnection();
            connection.start();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination destination = new ActiveMQQueue(queueName);
            MessageProducer producer = session.createProducer(destination);
            //ObjectMessage message = session.createObjectMessage();
            ObjectMessage message = session.createObjectMessage(content);
            producer.send(message);
            producer.close();
            session.close();
            connection.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
