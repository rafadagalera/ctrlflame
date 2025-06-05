package br.com.ctrlflame.config;

import br.com.ctrlflame.services.MqttService;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.MessageHandler;

@Configuration
public class MqttMessageHandlerConfig {
    private final Logger logger = LoggerFactory.getLogger(MqttMessageHandlerConfig.class);
    private final MqttService mqttService;

    public MqttMessageHandlerConfig(MqttService mqttService) {
        this.mqttService = mqttService;
    }

    @Bean
    @ServiceActivator(inputChannel = "mqttInputChannel")
    public MessageHandler mqttMessageHandler() {
        return message -> {
            try {
                String topic = (String) message.getHeaders().get("mqtt_receivedTopic");
                Object payload = message.getPayload();
                
                byte[] payloadBytes = (payload instanceof String) 
                    ? ((String) payload).getBytes() 
                    : (byte[]) payload;

                logger.debug("Processing message from topic: {} with payload: {}", 
                           topic, new String(payloadBytes));
                
                mqttService.messageArrived(topic, new MqttMessage(payloadBytes));
            } catch (Exception e) {
                logger.error("Failed to process MQTT message: {}", e.getMessage(), e);
            }
        };
    }
}