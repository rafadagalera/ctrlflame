package br.com.ctrlflame.controller;

import br.com.ctrlflame.services.SensorDataService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final SensorDataService service;

    public DashboardController(SensorDataService service) {
        this.service = service;
    }

    @GetMapping("/")
    public String dashboard(Model model) {
        model.addAttribute("highRiskData", service.findByFireRiskLevel(3));
        model.addAttribute("mediumRiskData", service.findByFireRiskLevel(2));
        return "dashboard";
    }

    @GetMapping("/mapa")
    public String mapa(Model model) {
        model.addAttribute("allData", service.findAll());
        return "mapa";
    }
}