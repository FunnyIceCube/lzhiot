package cn.huiclub.iot.consvr.biz.server.mqtt;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.mqtt.MqttDecoder;
import io.netty.handler.codec.mqtt.MqttEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

/**
 * @author: Lu Zhaohui
 * @description:
 * @date: Created on 2019/11/19
 * @modified By:
 */
@Component
public class MqttServer {

    @Autowired
    private MqttHandler mqttHandler;

    @PostConstruct
    private void init() {
        EventLoopGroup bossLoopGroup = new NioEventLoopGroup();
        EventLoopGroup workLoopGroup = new NioEventLoopGroup();

        ServerBootstrap serverBootstrap = new ServerBootstrap().group(bossLoopGroup, workLoopGroup)
                .channel(NioServerSocketChannel.class).option(ChannelOption.SO_BACKLOG, 4096)
                .childOption(ChannelOption.SO_KEEPALIVE, true).childOption(ChannelOption.TCP_NODELAY, true)
                .handler(new LoggingHandler(LogLevel.INFO)).childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new IdleStateHandler(300, 0, 0, TimeUnit.SECONDS));
                        ch.pipeline().addLast("logging", new LoggingHandler());
                        ch.pipeline().addLast(new MqttDecoder(20480));
                        ch.pipeline().addLast(MqttEncoder.INSTANCE);
                        ch.pipeline().addLast(mqttHandler);
                    }
                });
        try {
            ChannelFuture channelFuture = serverBootstrap.bind(5005).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossLoopGroup.shutdownGracefully();
            workLoopGroup.shutdownGracefully();
        }
    }
}
