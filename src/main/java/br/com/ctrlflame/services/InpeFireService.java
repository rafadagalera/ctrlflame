package br.com.ctrlflame.services;

import br.com.ctrlflame.model.InpeFire;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class InpeFireService {
    private static final Logger logger = LoggerFactory.getLogger(InpeFireService.class);
    private static final String CSV_PATH = "templates/data/dados_inpe.csv";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

    public List<InpeFire> getAllFireData() {
        List<InpeFire> fires = new ArrayList<>();
        
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new ClassPathResource(CSV_PATH).getInputStream()))) {
            
            // Skip header
            br.readLine();
            
            String line;
            while ((line = br.readLine()) != null) {
                try {
                    InpeFire fire = parseFireData(line);
                    if (fire != null) {
                        fires.add(fire);
                    }
                } catch (Exception e) {
                    logger.warn("Failed to parse line: {}. Error: {}", line, e.getMessage());
                }
            }
        } catch (Exception e) {
            logger.error("Failed to read INPE fire data: {}", e.getMessage(), e);
        }
        
        return fires;
    }

    private InpeFire parseFireData(String line) {
        String[] parts = line.split(",");
        if (parts.length < 12) return null;

        InpeFire fire = new InpeFire();
        try {
            fire.setDateTime(LocalDateTime.parse(parts[0], DATE_FORMATTER));
            fire.setSatellite(parts[1]);
            fire.setCountry(parts[2]);
            fire.setState(parts[3]);
            fire.setCity(parts[4]);
            fire.setBiome(parts[5]);
            fire.setDaysWithoutRain(parseDoubleOrNull(parts[6]));
            fire.setPrecipitation(parseDoubleOrNull(parts[7]));
            fire.setFireRisk(parseDoubleOrNull(parts[8]));
            fire.setLatitude(parseDoubleOrNull(parts[9]));
            fire.setLongitude(parseDoubleOrNull(parts[10]));
            fire.setFrp(parseDoubleOrNull(parts[11]));
        } catch (Exception e) {
            logger.warn("Error parsing fire data: {}", e.getMessage());
            return null;
        }
        
        return fire;
    }

    private Double parseDoubleOrNull(String value) {
        if (value == null || value.trim().isEmpty()) return null;
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
} 