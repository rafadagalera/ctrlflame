package br.com.ctrlflame.service;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FireDataService {
    private static final Logger logger = LoggerFactory.getLogger(FireDataService.class);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

    public List<Map<String, Object>> getAllFireData() {
        List<Map<String, Object>> fireData = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new InputStreamReader(
                new ClassPathResource("data/dados_inpe.csv").getInputStream()))) {
            
            // Skip header
            reader.readNext();
            
            String[] line;
            while ((line = reader.readNext()) != null) {
                try {
                    Map<String, Object> fire = new HashMap<>();
                    
                    // Parse data from CSV with Portuguese column names
                    fire.put("dateTime", LocalDateTime.parse(line[0], formatter).toString());  // DataHora
                    fire.put("satellite", line[1]);                                           // Satelite
                    fire.put("country", line[2]);                                            // Pais
                    fire.put("state", line[3]);                                              // Estado
                    fire.put("city", line[4]);                                               // Municipio
                    fire.put("biome", line[5]);                                              // Bioma
                    fire.put("daysWithoutRain", parseDoubleOrNull(line[6]));                // DiaSemChuva
                    fire.put("precipitation", parseDoubleOrNull(line[7]));                   // Precipitacao
                    fire.put("fireRisk", parseDoubleOrNull(line[8]));                       // RiscoFogo
                    fire.put("latitude", parseDoubleOrNull(line[9]));                        // Latitude
                    fire.put("longitude", parseDoubleOrNull(line[10]));                      // Longitude
                    fire.put("frp", parseDoubleOrNull(line[11]));                           // FRP
                    
                    fireData.add(fire);
                } catch (Exception e) {
                    logger.warn("Failed to parse line: {}. Error: {}", String.join(",", line), e.getMessage());
                }
            }
        } catch (IOException | CsvValidationException e) {
            logger.error("Failed to read fire data: {}", e.getMessage(), e);
        }
        
        return fireData;
    }

    private Double parseDoubleOrNull(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
} 