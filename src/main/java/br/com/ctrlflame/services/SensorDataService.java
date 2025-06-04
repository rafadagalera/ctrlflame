package br.com.ctrlflame.services;

import br.com.ctrlflame.model.SensorData;
import br.com.ctrlflame.repository.SensorDataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SensorDataService {
    private static final Logger logger = LoggerFactory.getLogger(SensorDataService.class);
    
    @Autowired
    private SensorDataRepository sensorDataRepository;
    private final NotificationService notificationService;

    public SensorDataService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    public List<SensorData> findAll() {
        return sensorDataRepository.findAll();
    }

    public List<SensorData> findByDeviceId(String deviceId) {
        if (deviceId == null || deviceId.trim().isEmpty()) {
            throw new IllegalArgumentException("Device ID cannot be empty");
        }
        return sensorDataRepository.findByDeviceId(deviceId);
    }

    public List<SensorData> findByFireRiskLevel(Integer level) {
        if (level == null || level < 1 || level > 3) {
            throw new IllegalArgumentException("Risk level must be between 1 and 3");
        }
        return sensorDataRepository.findByFireRiskLevel(level);
    }

    public long countByFireRiskLevel(int riskLevel) {
        return sensorDataRepository.countByFireRiskLevel(riskLevel);
    }

    public SensorData processAndSaveData(String deviceId, Double temperature,
                                       Double humidity, Double latitude, Double longitude) {
        SensorData data = new SensorData();
        data.setDeviceId(deviceId);
        data.setTemperature(temperature);
        data.setHumidity(humidity);
        data.setLatitude(latitude);
        data.setLongitude(longitude);
        data.setTimestamp(LocalDateTime.now());
        data.calculateFireRisk();

        data = sensorDataRepository.save(data);

        // Send notifications based on risk level
        switch (data.getFireRiskLevel()) {
            case 3:
                logger.info("High fire risk detected for device: {}. Sending notification.", deviceId);
                notificationService.sendHighRiskAlert(data);
                break;
            case 2:
                logger.info("Medium fire risk detected for device: {}. Sending notification.", deviceId);
                notificationService.sendMediumRiskAlert(data);
                break;
            default:
                logger.debug("Low fire risk level for device: {}. No notification needed.", deviceId);
        }

        return data;
    }

    public void save(SensorData sensorData) {
        sensorDataRepository.save(sensorData);
    }
}
