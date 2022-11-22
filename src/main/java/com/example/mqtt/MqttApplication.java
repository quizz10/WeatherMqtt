package com.example.mqtt;

import lombok.AllArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@AllArgsConstructor
public class MqttApplication {

    public static void main(String[] args) {
        SpringApplication.run(MqttApplication.class, args);
    }

}
