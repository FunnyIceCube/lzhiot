package cn.huiclub.iot.consvr.biz.server.mqtt.processor;


import io.netty.handler.codec.mqtt.MqttMessage;

/**
 * @author: Lu Zhaohui
 * @description:
 * @date: Created on 2019/11/20
 * @modified By:
 */
public interface MqttProcessor {

    MqttMessage handle(MqttMessage mqttMessage);
}
