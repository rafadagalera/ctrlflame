package br.com.ctrlflame.controller;

import br.com.ctrlflame.model.SensorData;
import br.com.ctrlflame.services.SensorDataService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/sensor-info")
public class SensorInfoController {

    private final SensorDataService sensorDataService;

    public SensorInfoController(SensorDataService sensorDataService) {
        this.sensorDataService = sensorDataService;
    }

    @GetMapping
    public String showSensorInfo(Model model) {
        List<SensorData> recentData = sensorDataService.findAll();
        model.addAttribute("sensorData", recentData);
        return "sensor-info";
    }
} 