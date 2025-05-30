package br.com.ctrlflame.repository;

import br.com.ctrlflame.model.SensorData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SensorDataRepository extends JpaRepository<SensorData, Long> {

    List<SensorData> findByDeviceId(String deviceId);
    List<SensorData> findByFireRiskLevel(Integer level);
    List<SensorData> findByTemperatureGreaterThan(Double temperature);
}