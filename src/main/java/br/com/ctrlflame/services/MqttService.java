package br.com.ctrlflame.services;

import br.com.ctrlflame.config.MqttProperties;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class MqttService implements IMqttMessageListener {

    private final SensorDataService sensorDataService;
    private final MqttProperties mqttProperties;

    public MqttService(SensorDataService sensorDataService, MqttProperties mqttProperties) {
        this.sensorDataService = sensorDataService;
        this.mqttProperties = mqttProperties;
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) {
        String payload = new String(message.getPayload());

        Pattern pattern = Pattern.compile("([^;]+);([\\d.]+);([\\d.]+);([\\d.-]+);([\\d.-]+)");
        Matcher matcher = pattern.matcher(payload);

        if (matcher.matches()) {
            String deviceId = matcher.group(1);
            Double temperature = Double.parseDouble(matcher.group(2));
            Double humidity = Double.parseDouble(matcher.group(3));
            Double latitude = Double.parseDouble(matcher.group(4));
            Double longitude = Double.parseDouble(matcher.group(5));

            sensorDataService.processAndSaveData(deviceId, temperature, humidity, latitude, longitude);
        }
    }

    public String getTopic() {
        return mqttProperties.getTopic();
    }

    public String getClientId() {
        return mqttProperties.getClientId();
    }
}
