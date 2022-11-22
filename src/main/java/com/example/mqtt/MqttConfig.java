package com.example.mqtt;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MqttConfig {

    @Bean
    public CommandLineRunner loadDataOnStartup(CityRepository cityRepository) {
        return (args) -> {
            cityRepository.deleteAll();
            City city = new City(1L, "stockholm");
            cityRepository.save(city);
        };
    }
}
