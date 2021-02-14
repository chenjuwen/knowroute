package com.heasy.knowroute.mqtt;

import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.handler.annotation.Header;

/**
 * 发送信息到MQTT服务器
 */
@MessagingGateway(defaultRequestChannel = MQTTConfiguration.CHANNEL_NAME_OUT)
public interface MQTTPublisherGateway {
	void publish(String payload);
	void publish(@Header(MqttHeaders.TOPIC)String topic, String payload);
	void publish(@Header(MqttHeaders.TOPIC)String topic, @Header(MqttHeaders.QOS)int qos, String payload);
}
