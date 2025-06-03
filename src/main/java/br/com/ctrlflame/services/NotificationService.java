package br.com.ctrlflame.services;

import br.com.ctrlflame.config.TwilioConfig;
import br.com.ctrlflame.model.SensorData;
import br.com.ctrlflame.model.WhatsAppMessage;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
public class NotificationService {
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);
    private static final String WHATSAPP_PREFIX = "whatsapp:";
    private static final String AUTHORITY_NUMBER = "+5511979702758";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private final TwilioConfig twilioConfig;

    public NotificationService(TwilioConfig twilioConfig) {
        this.twilioConfig = twilioConfig;
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

            sendWhatsAppMessage(message);
            
            logger.info("Alert sent via WhatsApp for device: {} with risk level: {}", 
                       sensorData.getDeviceId(), 
                       sensorData.getFireRiskLevel());
        } catch (Exception e) {
            logger.error("Failed to send WhatsApp alert: {}", e.getMessage(), e);
        }
    }

    private void sendWhatsAppMessage(WhatsAppMessage message) {
        try {
            String fromNumber = twilioConfig.getFromNumber().replaceAll("[^0-9+]", "");
            String toNumber = AUTHORITY_NUMBER.replaceAll("[^0-9+]", "");
            
            Message.creator(
                    new PhoneNumber(WHATSAPP_PREFIX + toNumber),
                    new PhoneNumber(WHATSAPP_PREFIX + fromNumber),
                    message.getFormattedMessage()
            ).create();
            
            logger.debug("WhatsApp message sent successfully from {} to {}", fromNumber, toNumber);
        } catch (Exception e) {
            logger.error("Failed to send WhatsApp message: {}", e.getMessage(), e);
            throw e;
        }
    }
} 