package com.heasy.knowroute.mqtt;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;

@Configuration
@IntegrationComponentScan
public class MQTTConfiguration {
	public static final String CHANNEL_NAME_OUT = "mqttOutputChannel";

	@Value("${mqtt.host}")
	private String host;

	@Value("${mqtt.clientId}")
    private String clientId;

	@Value("${mqtt.topic}")
    private String topic;

	@Value("${mqtt.username}")
    private String username;

	@Value("${mqtt.password}")
    private String password;

	@Value("${mqtt.qos}")
    private Integer qos;

	@Value("${mqtt.timeout}")
    private Integer timeout;

	@Value("${mqtt.keepalive}")
    private Integer keepalive;
	
	@Bean
	public MqttConnectOptions mqttConnectOptions() {
		MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
        options.setCleanSession(true);
        options.setUserName(username);
        options.setPassword(password.toCharArray());
        options.setConnectionTimeout(timeout);
        options.setKeepAliveInterval(keepalive);
        options.setServerURIs(new String[]{host});
        return options;
	}
	
	@Bean
    public MqttPahoClientFactory mqttPahoClientFactory(){
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        factory.setConnectionOptions(mqttConnectOptions());
        return factory;
    }
	
	/**
	 * MQTT信息通道（生产者）
	 */
	@Bean(name = CHANNEL_NAME_OUT)
    public MessageChannel mqttOutputChannel() {
        return new DirectChannel();
    }
	
	/**
	 * MQTT消息处理器（生产者）
	 */
	@Bean
    @ServiceActivator(inputChannel = CHANNEL_NAME_OUT)
    public MessageHandler mqttOutbound() {
        MqttPahoMessageHandler messageHandler = new MqttPahoMessageHandler(clientId, mqttPahoClientFactory());
        messageHandler.setAsync(true);
        messageHandler.setDefaultTopic(topic);
        return messageHandler;
    }
	
	

	
	/**
	 * MQTT信息通道（消费者）
	 */
	@Bean(name = "mqttInputChannel")
    public MessageChannel mqttInputChannel() {
        return new DirectChannel();
    }

	/**
	 * MQTT消息处理器
	 */
	@Bean
    public MessageProducer inbound() {
        MqttPahoMessageDrivenChannelAdapter adapter =
                new MqttPahoMessageDrivenChannelAdapter(clientId, mqttPahoClientFactory(), topic);
        adapter.setCompletionTimeout(timeout);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(qos);
        adapter.setOutputChannel(mqttInputChannel());
        return adapter;
    }
	
	//通过通道获取数据
	@Bean
    @ServiceActivator(inputChannel = "mqttInputChannel")
    public MessageHandler handler() {
		return new MessageHandler() {
			@Override
			public void handleMessage(Message<?> message) throws MessagingException {
				message.getHeaders().forEach((k,v) -> {
					System.out.println(k + "=" + v);
				});
				
				String topic = (String)message.getHeaders().get(MqttHeaders.RECEIVED_TOPIC);
				boolean retained = (Boolean)message.getHeaders().get(MqttHeaders.RECEIVED_RETAINED);
	            int qos = (int) message.getHeaders().get(MqttHeaders.RECEIVED_QOS);
	            String payload = (String)message.getPayload();
	            
	            System.out.println("topic=" + topic + ", retained=" + retained + ", qos=" + qos + ", payload=" + payload);
			}
		};
    }
	
}
