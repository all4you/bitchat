package io.bitchat.im.server;

import io.bitchat.server.ServerBootstrap;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @author houyi
 */
@SpringBootApplication(scanBasePackages = "io.bitchat.im.server")
@MapperScan("io.bitchat.im.server.dao")
public class ServerApplication {

    public static void main(String[] args) {
        // 先启动Spring容器
        SpringApplication.run(ServerApplication.class, args);
        // 再启动Server
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.channelListener(SimpleChannelListener.class)
                .start(8864);
    }

}
