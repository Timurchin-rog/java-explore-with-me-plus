package ru.practicum.ewm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
public class EWMServer {
	public static void main(String[] args) {
		SpringApplication.run(EWMServer.class, args);
	}

}
