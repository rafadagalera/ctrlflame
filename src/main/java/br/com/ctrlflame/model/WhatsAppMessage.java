package br.com.ctrlflame.model;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@Builder
public class WhatsAppMessage {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private String deviceId;
    private LocalDateTime timestamp;
    private Double temperature;
    private Double humidity;
    private Double latitude;
    private Double longitude;
    private Integer riskLevel;
    private String additionalInfo;

    public static WhatsAppMessage fromSensorData(SensorData sensorData) {
        return WhatsAppMessage.builder()
                .deviceId(sensorData.getDeviceId())
                .timestamp(sensorData.getTimestamp())
                .temperature(sensorData.getTemperature())
                .humidity(sensorData.getHumidity())
                .latitude(sensorData.getLatitude())
                .longitude(sensorData.getLongitude())
                .riskLevel(sensorData.getFireRiskLevel())
                .build();
    }

    public String formatHighRiskMessage() {
        return String.format("""
            🚨 *ALERTA DE ALTO RISCO DE INCÊNDIO* 🚨
            
            Dispositivo: %s
            Data/Hora: %s
            
            📍 Localização:
            Latitude: %.6f
            Longitude: %.6f
            
            🌡️ Temperatura: %.1f°C
            💧 Umidade: %.1f%%
            
            ⚠️ Nível de Risco: ALTO
            
            %s
            
            Por favor, tome as medidas necessárias imediatamente.
            """,
            deviceId,
            timestamp.format(DATE_FORMATTER),
            latitude,
            longitude,
            temperature,
            humidity,
            additionalInfo != null ? additionalInfo : ""
        );
    }

    public String formatMediumRiskMessage() {
        return String.format("""
            ⚠️ *ALERTA DE RISCO MÉDIO DE INCÊNDIO* ⚠️
            
            Dispositivo: %s
            Data/Hora: %s
            
            📍 Localização:
            Latitude: %.6f
            Longitude: %.6f
            
            🌡️ Temperatura: %.1f°C
            💧 Umidade: %.1f%%
            
            ⚠️ Nível de Risco: MÉDIO
            
            %s
            
            Recomenda-se monitoramento constante da área.
            """,
            deviceId,
            timestamp.format(DATE_FORMATTER),
            latitude,
            longitude,
            temperature,
            humidity,
            additionalInfo != null ? additionalInfo : ""
        );
    }

    public String getFormattedMessage() {
        return switch (riskLevel) {
            case 3 -> formatHighRiskMessage();
            case 2 -> formatMediumRiskMessage();
            default -> throw new IllegalStateException("Mensagens são enviadas apenas para níveis de risco médio ou alto");
        };
    }
} 