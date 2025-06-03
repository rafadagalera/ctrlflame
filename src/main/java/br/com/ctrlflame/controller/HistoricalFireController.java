package br.com.ctrlflame.controller;

import br.com.ctrlflame.services.InpeFireService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/historical")
public class HistoricalFireController {
    
    private final InpeFireService inpeFireService;

    public HistoricalFireController(InpeFireService inpeFireService) {
        this.inpeFireService = inpeFireService;
    }

    @GetMapping("/map")
    public String showHistoricalMap(Model model) {
        model.addAttribute("fireData", inpeFireService.getAllFireData());
        return "historical-map";
    }
} 