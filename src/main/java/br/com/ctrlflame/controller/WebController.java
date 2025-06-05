package br.com.ctrlflame.controller;

import br.com.ctrlflame.services.InpeFireService;
import br.com.ctrlflame.services.SensorDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    @Autowired
    private InpeFireService inpeFireService;

    @Autowired
    private SensorDataService sensorDataService;

    @GetMapping("/")
    public String home(Model model) {
        // Add counts for the statistics section
        long highRiskCount = sensorDataService.countByFireRiskLevel(3);
        long mediumRiskCount = sensorDataService.countByFireRiskLevel(2);
        
        model.addAttribute("highRiskCount", highRiskCount);
        model.addAttribute("mediumRiskCount", mediumRiskCount);
        model.addAttribute("fireData", inpeFireService.getAllFireDataAsMap());
        
        return "home";
    }

    @GetMapping("/historical-map")
    public String historicalMap(Model model) {
        model.addAttribute("fireData", inpeFireService.getAllFireDataAsMap());
        return "historical-map";
    }

    @GetMapping("/understanding")
    public String understanding() {
        return "understanding";
    }

    @GetMapping("/sensor-map")
    public String sensorMap(Model model) {
        model.addAttribute("sensorData", sensorDataService.findAll());
        return "sensor-map";
    }
} 