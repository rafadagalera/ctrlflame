package br.com.ctrlflame;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;

@SpringBootApplication
public class CtrlFlameApplication {
    
    private static final Logger logger = LoggerFactory.getLogger(CtrlFlameApplication.class);
    private final Environment env;

    public CtrlFlameApplication(Environment env) {
        this.env = env;
    }

    public static void main(String[] args) {
        SpringApplication.run(CtrlFlameApplication.class, args);
    }

    @EventListener(ApplicationStartedEvent.class)
    public void checkEnvironmentVariables() {
        logger.info("Checking environment variables...");
        
        checkEnvVariable("twilio.account.sid");
        checkEnvVariable("twilio.auth.token");
        checkEnvVariable("twilio.whatsapp.from");
        
        logger.info("Environment variable check completed.");
    }

    private void checkEnvVariable(String name) {
        String value = env.getProperty(name);
        if (value == null || value.trim().isEmpty()) {
            logger.warn("❌ Environment variable {} is not set", name);
        } else {
            String maskedValue = value.length() > 8 
                ? value.substring(0, 4) + "..." + value.substring(value.length() - 4)
                : "***";
            logger.info("✅ Environment variable {} is set: {}", name, maskedValue);
        }
    }
}
