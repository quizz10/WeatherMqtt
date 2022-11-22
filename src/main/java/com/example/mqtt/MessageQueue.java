package com.example.mqtt;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@EnableScheduling
@Configuration

public class MessageQueue {
    float celsius = -500f;

    CityRepository cityRepository;

    @Autowired
    public MessageQueue(CityRepository cityRepository) {
        this.cityRepository = cityRepository;

    }

    @Scheduled(cron = "0/15 * * * * *")
    public void run() throws MqttException, IOException, InterruptedException {
        String city = this.cityRepository.findCityById(1L).getCityName();
        System.out.println("HEJHEJ");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=34f1dfe47079741556f631b2639de87d"))
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        float temperature = (new JSONObject(response.body()).getJSONObject("main").getBigDecimal("temp").floatValue()) - 273.15f;

        if (celsius < (temperature - 0.1) || celsius > (temperature + 0.1f)) {
            celsius = temperature;
            String celsiusString = "Temperature: " + String.format("%.2f", celsius);
            String publisherId = UUID.randomUUID().toString();

            IMqttClient client = new MqttClient("tcp://83.251.117.120", publisherId, new MqttDefaultFilePersistence("src/main/resources/queue"));

            while (!client.isConnected()) {
                System.out.println("attempting to connect");
                client.connect();
            }
            client.publish("mytopic/test", new MqttMessage(celsiusString.getBytes(StandardCharsets.UTF_8)));
            System.out.println(celsiusString);
        } else {
            System.out.println("SKIPPED, value was " + String.format("%.2f", temperature));
        }

    }
}
