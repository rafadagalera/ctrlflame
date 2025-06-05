package br.com.ctrlflame.controller;

import br.com.ctrlflame.model.User;
import br.com.ctrlflame.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    @Autowired
    private UserService userService;

    @GetMapping
    public String showProfile(Model model, Authentication authentication) {
        String email = authentication.getName();
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        model.addAttribute("user", user);
        return "profile";
    }

    @PostMapping("/update-phone")
    public String updatePhone(@RequestParam String phone, 
                            Authentication authentication,
                            RedirectAttributes redirectAttributes) {
        try {
            String email = authentication.getName();
            userService.updatePhone(email, phone);
            redirectAttributes.addFlashAttribute("success", "Número de telefone atualizado com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erro ao atualizar o número de telefone: " + e.getMessage());
        }
        return "redirect:/profile";
    }
} 