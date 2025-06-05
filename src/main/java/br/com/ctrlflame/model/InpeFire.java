package br.com.ctrlflame.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class InpeFire {
    private LocalDateTime dateTime;
    private String satellite;
    private String country;
    private String state;
    private String city;
    private String biome;
    private Double daysWithoutRain;
    private Double precipitation;
    private Double fireRisk;
    private Double latitude;
    private Double longitude;
    private Double frp; // Fire Radiative Power
} 