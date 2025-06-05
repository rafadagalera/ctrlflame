package br.com.ctrlflame.services;

import br.com.ctrlflame.model.InpeFire;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class InpeFireService {
    private static final Logger logger = LoggerFactory.getLogger(InpeFireService.class);
    private static final String CSV_PATH = "data/dados_inpe.csv";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
    
    private final Map<String, List<InpeFire>> fireDataCache = new ConcurrentHashMap<>();
    private volatile boolean isDataLoaded = false;

    @PostConstruct
    public void init() {
        // Load data asynchronously on startup
        new Thread(() -> {
            loadFireData();
            isDataLoaded = true;
            logger.info("Fire data loaded successfully");
        }).start();
    }

    private void loadFireData() {
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
            
            // Group fires by state for faster querying
            Map<String, List<InpeFire>> firesByState = new HashMap<>();
            for (InpeFire fire : fires) {
                firesByState.computeIfAbsent(fire.getState(), k -> new ArrayList<>()).add(fire);
            }
            
            fireDataCache.putAll(firesByState);
            
        } catch (Exception e) {
            logger.error("Failed to read INPE fire data: {}", e.getMessage(), e);
        }
    }

    @Cacheable(value = "allFireData")
    public List<InpeFire> getAllFireData() {
        ensureDataLoaded();
        return fireDataCache.values().stream()
                .flatMap(List::stream)
                .collect(java.util.stream.Collectors.toList());
    }

    @Cacheable(value = "fireDataByState", key = "#state")
    public List<InpeFire> getFireDataByState(String state) {
        ensureDataLoaded();
        return fireDataCache.getOrDefault(state, Collections.emptyList());
    }

    @Cacheable(value = "fireDataAsMap")
    public List<Map<String, Object>> getAllFireDataAsMap() {
        List<Map<String, Object>> fireData = new ArrayList<>();
        getAllFireData().forEach(fire -> {
            Map<String, Object> fireMap = new HashMap<>();
            fireMap.put("dateTime", fire.getDateTime().toString());
            fireMap.put("satellite", fire.getSatellite());
            fireMap.put("country", fire.getCountry());
            fireMap.put("state", fire.getState());
            fireMap.put("city", fire.getCity());
            fireMap.put("biome", fire.getBiome());
            fireMap.put("daysWithoutRain", fire.getDaysWithoutRain());
            fireMap.put("precipitation", fire.getPrecipitation());
            fireMap.put("fireRisk", fire.getFireRisk());
            fireMap.put("latitude", fire.getLatitude());
            fireMap.put("longitude", fire.getLongitude());
            fireMap.put("frp", fire.getFrp());
            fireData.add(fireMap);
        });
        
        return fireData;
    }

    private void ensureDataLoaded() {
        if (!isDataLoaded) {
            synchronized (this) {
                if (!isDataLoaded) {
                    loadFireData();
                    isDataLoaded = true;
                }
            }
        }
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