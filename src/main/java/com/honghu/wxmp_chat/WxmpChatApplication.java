package com.honghu.wxmp_chat;

import io.github.asleepyfish.annotation.EnableChatGPT;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author honghu
 */
@SpringBootApplication
@EnableChatGPT
public class WxmpChatApplication {

    public static void main(String[] args) {
        SpringApplication.run(WxmpChatApplication.class, args);
    }

}
