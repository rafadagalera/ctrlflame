package br.com.ctrlflame.services;

import br.com.ctrlflame.model.SensorData;
import br.com.ctrlflame.repository.SensorDataRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SensorDataService {

    private final SensorDataRepository repository;

    public SensorDataService(SensorDataRepository repository) {
        this.repository = repository;
    }


    public List<SensorData> findAll() {
        return repository.findAll();
    }
    public List<SensorData> findByDeviceId(String deviceId) {
        if (deviceId == null || deviceId.trim().isEmpty()) {
            throw new IllegalArgumentException("ID do dispositivo não pode ser vazio");
        }
        return repository.findByDeviceId(deviceId);
    }
    public List<SensorData> findByFireRiskLevel(Integer level) {
        if (level == null || level < 1 || level > 3) {
            throw new IllegalArgumentException("Nível de risco deve ser entre 1 e 3");
        }
        return repository.findByFireRiskLevel(level);
    }
    public SensorData processAndSaveData(String deviceId, Double temperature,
                                         Double humidity, Double latitude, Double longitude) {
        SensorData data = new SensorData();
        data.setDeviceId(deviceId);
        data.setTemperature(temperature);
        data.setHumidity(humidity);
        data.setLatitude(latitude);
        data.setLongitude(longitude);
        data.calculateFireRisk();

        return repository.save(data);
    }
}
