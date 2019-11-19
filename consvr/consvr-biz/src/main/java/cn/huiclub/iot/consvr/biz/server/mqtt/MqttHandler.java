package cn.huiclub.iot.consvr.biz.server.mqtt;

import cn.huiclub.iot.consvr.biz.server.mqtt.processor.MqttProcessor;
import cn.huiclub.iot.consvr.biz.server.mqtt.processor.MqttProcessorFactory;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.mqtt.MqttConnAckVariableHeader;
import io.netty.handler.codec.mqtt.MqttConnectReturnCode;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageFactory;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.netty.handler.codec.mqtt.MqttUnacceptableProtocolVersionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author: Lu Zhaohui
 * @description:
 * @date: Created on 2019/11/20
 * @modified By:
 */
@Component
public class MqttHandler extends SimpleChannelInboundHandler<MqttMessage> {

    Logger LOGGER = LoggerFactory.getLogger(MqttHandler.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        LOGGER.info("MqttProcessor channelActive()");
        super.channelActive(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MqttMessage mqttMessage) throws Exception {
        MqttMessage resultMessage;

        // 验证MQTT协议解析是否正常
        if (mqttMessage.decoderResult().isFailure()) {
            Throwable cause = mqttMessage.decoderResult().cause();
            LOGGER.error("MqttProcessor parse MQTT error ", cause);

            MqttConnectReturnCode errorCode = MqttConnectReturnCode.CONNECTION_REFUSED_IDENTIFIER_REJECTED;

            if (cause instanceof MqttUnacceptableProtocolVersionException) {
                errorCode = MqttConnectReturnCode.CONNECTION_REFUSED_UNACCEPTABLE_PROTOCOL_VERSION;
            }
            resultMessage = MqttMessageFactory.newMessage(
                    new MqttFixedHeader(MqttMessageType.CONNACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
                    new MqttConnAckVariableHeader(errorCode, false),
                    null);
        } else {
            // 处理MQTT信息
            MqttMessageType mqttMessageType = mqttMessage.fixedHeader().messageType();
            MqttProcessor mqttProcessor = MqttProcessorFactory.get(mqttMessageType);
            resultMessage = mqttProcessor.handle(mqttMessage);
        }

        System.out.println(resultMessage);
        ctx.writeAndFlush(resultMessage);
        ctx.close();
    }
    
}
