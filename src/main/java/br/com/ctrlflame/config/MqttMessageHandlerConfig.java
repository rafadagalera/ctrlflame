package br.com.ctrlflame.config;

import br.com.ctrlflame.services.MqttService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.MessageHandler;

@Configuration
public class MqttMessageHandlerConfig {

    @Autowired
    private MqttService mqttService;

    @Bean
    @ServiceActivator(inputChannel = "mqttInputChannel")
    public MessageHandler mqttMessageHandler() {
        return message -> {
            String topic = (String) message.getHeaders().get("mqtt_receivedTopic");
            byte[] payload = (byte[]) message.getPayload();
            mqttService.messageArrived(topic, new org.eclipse.paho.client.mqttv3.MqttMessage(payload));
        };
    }
}