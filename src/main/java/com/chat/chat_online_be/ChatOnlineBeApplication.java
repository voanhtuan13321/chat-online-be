package com.chat.chat_online_be;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class ChatOnlineBeApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChatOnlineBeApplication.class, args);
	}

}
