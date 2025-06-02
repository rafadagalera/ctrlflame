package br.com.ctrlflame.services;

import br.com.ctrlflame.config.MqttProperties;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class MqttService implements IMqttMessageListener {

    private static final Logger logger = LoggerFactory.getLogger(MqttService.class);
    private static final String MESSAGE_PATTERN = "([^;]+);([\\d.]+);([\\d.]+);([\\d.-]+);([\\d.-]+)";

    private final SensorDataService sensorDataService;
    private final MqttProperties mqttProperties;
    private final Pattern pattern;

    public MqttService(SensorDataService sensorDataService, MqttProperties mqttProperties) {
        this.sensorDataService = sensorDataService;
        this.mqttProperties = mqttProperties;
        this.pattern = Pattern.compile(MESSAGE_PATTERN);
        logger.info("MqttService initialized with topic: {}", mqttProperties.getTopic());
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) {
        try {
            String payload = new String(message.getPayload());
            logger.info("Message received - Topic: {}, QoS: {}, Retained: {}",
                    topic, message.getQos(), message.isRetained());
            logger.debug("Message payload: {}", payload);

            Matcher matcher = pattern.matcher(payload);

            if (!matcher.matches()) {
                logger.warn("Invalid message format received. Expected pattern: {}, Received payload: {}",
                        MESSAGE_PATTERN, payload);
                return;
            }

            try {
                String deviceId = matcher.group(1);
                Double temperature = parseDoubleSafely(matcher.group(2), "temperatura");
                Double humidity = parseDoubleSafely(matcher.group(3), "umidade");
                Double latitude = parseDoubleSafely(matcher.group(4), "latitude");
                Double longitude = parseDoubleSafely(matcher.group(5), "longitude");

                if (temperature == null || humidity == null || latitude == null || longitude == null) {
                    logger.warn("One or more required values are null - DeviceId: {}, Temp: {}, Humidity: {}, Lat: {}, Long: {}",
                            deviceId, temperature, humidity, latitude, longitude);
                    return;
                }

                logger.info("Processing data from device {} - Temp: {}°C, Humidity: {}%, Location: ({}, {})",
                        deviceId, temperature, humidity, latitude, longitude);

                sensorDataService.processAndSaveData(deviceId, temperature, humidity, latitude, longitude);

            } catch (IllegalStateException e) {
                logger.error("Error extracting data from message: {}", e.getMessage());
            }

        } catch (Exception e) {
            logger.error("Unexpected error processing MQTT message", e);
        }
    }

    private Double parseDoubleSafely(String value, String fieldName) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            logger.error("Invalid value for {}: {}", fieldName, value);
            return null;
        }
    }

    public String getTopic() {
        return mqttProperties.getTopic();
    }

    public String getClientId() {
        return mqttProperties.getClientId();
    }
}