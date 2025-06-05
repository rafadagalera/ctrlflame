package br.com.ctrlflame.services;

import br.com.ctrlflame.config.TwilioConfig;
import br.com.ctrlflame.model.SensorData;
import br.com.ctrlflame.model.User;
import br.com.ctrlflame.model.WhatsAppMessage;
import br.com.ctrlflame.services.UserService;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private final TwilioConfig twilioConfig;
    private final UserService userService;

    public NotificationService(TwilioConfig twilioConfig, UserService userService) {
        this.twilioConfig = twilioConfig;
        this.userService = userService;
    }

    public void sendHighRiskAlert(SensorData sensorData) {
        sendAlert(sensorData, null);
    }

    public void sendHighRiskAlert(SensorData sensorData, String additionalInfo) {
        sendAlert(sensorData, additionalInfo);
    }

    public void sendMediumRiskAlert(SensorData sensorData) {
        sendAlert(sensorData, null);
    }

    public void sendMediumRiskAlert(SensorData sensorData, String additionalInfo) {
        sendAlert(sensorData, additionalInfo);
    }

    private void sendAlert(SensorData sensorData, String additionalInfo) {
        try {
            WhatsAppMessage message = WhatsAppMessage.fromSensorData(sensorData);
            if (additionalInfo != null) {
                message.setAdditionalInfo(additionalInfo);
            }

            // Get all users with registered phone numbers
            List<User> usersWithPhones = userService.findAllUsers().stream()
                    .filter(user -> user.getPhone() != null && !user.getPhone().trim().isEmpty())
                    .collect(Collectors.toList());

            if (usersWithPhones.isEmpty()) {
                logger.warn("No users with registered phone numbers found. Alert not sent.");
                return;
            }

            // Send message to each user
            for (User user : usersWithPhones) {
                try {
                    sendWhatsAppMessage(message, user.getPhone());
                    logger.info("Alert sent via WhatsApp to user: {} for device: {} with risk level: {}", 
                              user.getEmail(), sensorData.getDeviceId(), sensorData.getFireRiskLevel());
                } catch (Exception e) {
                    logger.error("Failed to send WhatsApp alert to user {}: {}", user.getEmail(), e.getMessage());
                }
            }
        } catch (Exception e) {
            logger.error("Failed to process WhatsApp alerts: {}", e.getMessage(), e);
        }
    }

    private void sendWhatsAppMessage(WhatsAppMessage message, String toNumber) {
        if (toNumber == null || toNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Recipient phone number cannot be empty");
        }

        try {
            // Ensure the number starts with "whatsapp:"
            String fromNumber = twilioConfig.getFromNumber();
            if (!fromNumber.startsWith("whatsapp:")) {
                fromNumber = "whatsapp:" + fromNumber;
            }

            String formattedToNumber = toNumber.trim();
            if (!formattedToNumber.startsWith("whatsapp:")) {
                formattedToNumber = "whatsapp:" + formattedToNumber;
            }

            Message.creator(
                    new PhoneNumber(formattedToNumber),
                    new PhoneNumber(fromNumber),
                    message.getFormattedMessage()
            ).create();
            
            logger.debug("WhatsApp message sent successfully to {}", toNumber);
        } catch (Exception e) {
            logger.error("Failed to send WhatsApp message to {}: {}", toNumber, e.getMessage());
            throw e;
        }
    }
} 