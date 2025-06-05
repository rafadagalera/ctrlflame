package br.com.ctrlflame.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import com.twilio.Twilio;

import jakarta.annotation.PostConstruct;

@Configuration
public class TwilioConfig {
    private static final Logger logger = LoggerFactory.getLogger(TwilioConfig.class);

    @Value("${twilio.account.sid}")
    private String accountSid;

    @Value("${twilio.auth.token}")
    private String authToken;

    @Value("${twilio.whatsapp.from}")
    private String fromNumber;

    @Value("${twilio.enabled:true}")
    private boolean enabled;

    @PostConstruct
    public void initTwilio() {
        if (enabled) {
            logger.info("Initializing Twilio with account SID: {}...", maskString(accountSid));
            logger.info("WhatsApp from number configured as: {}", fromNumber);
            
            if (accountSid == null || accountSid.trim().isEmpty() ||
                authToken == null || authToken.trim().isEmpty()) {
                logger.error("Twilio credentials are missing! Check your environment variables.");
                return;
            }
            
            Twilio.init(accountSid, authToken);
            logger.info("Twilio initialization completed successfully");
        } else {
            logger.warn("Twilio integration is disabled");
        }
    }

    private String maskString(String input) {
        if (input == null || input.length() < 8) {
            return "***";
        }
        return input.substring(0, 4) + "..." + input.substring(input.length() - 4);
    }

    public String getAccountSid() {
        return accountSid;
    }

    public String getAuthToken() {
        return authToken;
    }

    public String getFromNumber() {
        return fromNumber;
    }

    public boolean isEnabled() {
        return enabled;
    }
}