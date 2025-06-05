package br.com.ctrlflame.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/env-test")
public class EnvTestController {

    @Value("${twilio.account.sid:not_set}")
    private String accountSid;

    @Value("${twilio.auth.token:not_set}")
    private String authToken;

    @Value("${twilio.whatsapp.from:not_set}")
    private String whatsappFrom;

    @GetMapping("/check")
    public Map<String, String> checkEnv() {
        Map<String, String> status = new HashMap<>();
        
        // For security, we'll just show if they're set or not
        status.put("twilio.account.sid", isSet(accountSid));
        status.put("twilio.auth.token", isSet(authToken));
        status.put("twilio.whatsapp.from", isSet(whatsappFrom));
        
        return status;
    }

    private String isSet(String value) {
        if ("not_set".equals(value)) {
            return "❌ Not set";
        }
        return "✅ Set (" + maskString(value) + ")";
    }

    private String maskString(String input) {
        if (input == null || input.length() < 8) {
            return "***";
        }
        return input.substring(0, 4) + "..." + input.substring(input.length() - 4);
    }
} 