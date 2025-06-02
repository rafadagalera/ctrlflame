package br.com.ctrlflame.config;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;

@Configuration
public class MqttConfig {

    private static final Logger logger = LoggerFactory.getLogger(MqttConfig.class);
    private final MqttProperties mqttProperties;

    public MqttConfig(MqttProperties mqttProperties) {
        this.mqttProperties = mqttProperties;
    }

    @Bean
    public MqttPahoClientFactory mqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        MqttConnectOptions options = new MqttConnectOptions();

        options.setServerURIs(new String[]{mqttProperties.getBrokerUrl()});
        options.setCleanSession(true);
        options.setAutomaticReconnect(true);
        options.setConnectionTimeout(30);
        options.setKeepAliveInterval(60);
        options.setMaxReconnectDelay(5000);
        
        // For debugging - will print all MQTT protocol messages
        System.setProperty("org.eclipse.paho.client.mqttv3.trace", "true");

        logger.info("Configuring MQTT connection - Broker: {}, ClientId: {}, Topic: {}",
                mqttProperties.getBrokerUrl(),
                mqttProperties.getClientId(),
                mqttProperties.getTopic());

        factory.setConnectionOptions(options);
        return factory;
    }

    @Bean
    public MessageChannel mqttInputChannel() {
        return new DirectChannel();
    }

    @Bean
    public MqttPahoMessageDrivenChannelAdapter mqttInbound(
            MqttPahoClientFactory factory) {

        String clientId = mqttProperties.getClientId() + "-inbound";
        String topic = mqttProperties.getTopic();
        // Add wildcard subscription for debugging
        String[] topics = new String[]{
            topic,              // Original topic
            "ctrlflame/#",     // Wildcard for all ctrlflame topics
            topic + "/#"       // Wildcard for subtopics
        };

        logger.info("Setting up MQTT inbound adapter - ClientId: {}, Topics: {}", 
                   clientId, String.join(", ", topics));

        MqttPahoMessageDrivenChannelAdapter adapter =
                new MqttPahoMessageDrivenChannelAdapter(
                        clientId,
                        factory,
                        topics);

        adapter.setQos(1);
        adapter.setOutputChannel(mqttInputChannel());
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setCompletionTimeout(5000);

        return adapter;
    }
}