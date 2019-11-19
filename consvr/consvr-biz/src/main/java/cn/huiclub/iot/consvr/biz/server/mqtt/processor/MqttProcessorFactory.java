package cn.huiclub.iot.consvr.biz.server.mqtt.processor;

import io.netty.handler.codec.mqtt.MqttMessageType;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: Lu Zhaohui
 * @description:
 * @date: Created on 2019/11/20
 * @modified By:
 */
public class MqttProcessorFactory {
    private static Map<MqttMessageType, MqttProcessor> processorMap = new HashMap<>();

    static {

    }

    public static MqttProcessor get(MqttMessageType messageType) {
        return processorMap.get(messageType);
    }
}
