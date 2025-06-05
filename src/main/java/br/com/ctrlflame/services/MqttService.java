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
    private static final Pattern pattern = Pattern.compile(MESSAGE_PATTERN);

    private final SensorDataService sensorDataService;
    private final MqttProperties mqttProperties;

    public MqttService(SensorDataService sensorDataService, MqttProperties mqttProperties) {
        this.sensorDataService = sensorDataService;
        this.mqttProperties = mqttProperties;
        logger.info("MQTT Service initialized for topic: {}", mqttProperties.getTopic());
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) {
        try {
            String payload = new String(message.getPayload());
            Matcher matcher = pattern.matcher(payload);

            if (!matcher.matches()) {
                logger.warn("Invalid message format. Expected pattern: deviceId;temperature;humidity;latitude;longitude");
                return;
            }

            String deviceId = matcher.group(1);
            Double temperature = parseDouble(matcher.group(2), "temperature");
            Double humidity = parseDouble(matcher.group(3), "humidity");
            Double latitude = parseDouble(matcher.group(4), "latitude");
            Double longitude = parseDouble(matcher.group(5), "longitude");

            if (temperature == null || humidity == null || latitude == null || longitude == null) {
                logger.warn("Invalid sensor data values for device: {}", deviceId);
                return;
            }

            logger.info("Processing sensor data - Device: {}, Temperature: {}°C, Humidity: {}%", 
                       deviceId, temperature, humidity);

            sensorDataService.processAndSaveData(deviceId, temperature, humidity, latitude, longitude);

        } catch (Exception e) {
            logger.error("Failed to process sensor data: {}", e.getMessage(), e);
        }
    }

    private Double parseDouble(String value, String field) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            logger.warn("Invalid {} value: {}", field, value);
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