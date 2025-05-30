package br.com.ctrlflame.controller;

import br.com.ctrlflame.model.SensorData;
import br.com.ctrlflame.services.SensorDataService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sensor-data")
public class SensorDataApiController {

    private final SensorDataService service;

    public SensorDataApiController(SensorDataService service) {
        this.service = service;
    }

    @GetMapping
    public List<SensorData> getAll() {
        return service.findAll();
    }

    @GetMapping("/device/{deviceId}")
    public List<SensorData> getByDevice(@PathVariable String deviceId) {
        return service.findByDeviceId(deviceId);
    }

    @GetMapping("/risk/{level}")
    public List<SensorData> getByRiskLevel(@PathVariable Integer level) {
        return service.findByFireRiskLevel(level);
    }
}