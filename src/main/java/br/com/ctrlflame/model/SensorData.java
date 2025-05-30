package br.com.ctrlflame.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "SENSOR_DATA")
public class SensorData {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sensor_seq")
    @SequenceGenerator(name = "sensor_seq", sequenceName = "SENSOR_SEQ", allocationSize = 1)
    private Long id;

    @Column(name = "DEVICE_ID", nullable = false)
    private String deviceId;

    @Column(name = "TEMPERATURE", nullable = false)
    private Double temperature;

    @Column(name = "HUMIDITY", nullable = false)
    private Double humidity;

    @Column(name = "LATITUDE")
    private Double latitude;

    @Column(name = "LONGITUDE")
    private Double longitude;

    @Column(name = "TIMESTAMP", nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "FIRE_RISK_LEVEL")
    private Integer fireRiskLevel;

    // Método para calcular o risco de fogo baseado nos dados
    public void calculateFireRisk() {
        // Lógica simples de cálculo de risco
        if (temperature > 40 && humidity < 30) {
            this.fireRiskLevel = 3; // Alto risco
        } else if (temperature > 35 && humidity < 40) {
            this.fireRiskLevel = 2; // Médio risco
        } else {
            this.fireRiskLevel = 1; // Baixo risco
        }
    }
}