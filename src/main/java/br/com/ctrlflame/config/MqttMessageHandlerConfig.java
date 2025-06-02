package br.com.ctrlflame.config;

import br.com.ctrlflame.services.MqttService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.eclipse.paho.client.mqttv3.MqttMessage;

@Configuration
public class MqttMessageHandlerConfig {

    private static final Logger logger = LoggerFactory.getLogger(MqttMessageHandlerConfig.class);

    @Autowired
    private MqttService mqttService;

    @Bean
    @ServiceActivator(inputChannel = "mqttInputChannel")
    public MessageHandler mqttMessageHandler() {
        return message -> {
            try {
                String topic = (String) message.getHeaders().get("mqtt_receivedTopic");
                Object payload = message.getPayload();
                
                logger.info("Received MQTT message - Topic: {}", topic);
                logger.debug("Message headers: {}", message.getHeaders());
                
                byte[] payloadBytes;
                if (payload instanceof byte[]) {
                    payloadBytes = (byte[]) payload;
                    logger.debug("Raw payload (byte[]): {}", new String(payloadBytes));
                } else if (payload instanceof String) {
                    payloadBytes = ((String) payload).getBytes();
                    logger.debug("Raw payload (String): {}", payload);
                } else {
                    logger.error("Unexpected payload type: {}", payload.getClass());
                    return;
                }
                
                MqttMessage mqttMessage = new MqttMessage(payloadBytes);
                mqttService.messageArrived(topic, mqttMessage);
            } catch (Exception e) {
                logger.error("Error processing MQTT message: {}", e.getMessage(), e);
            }
        };
    }
}