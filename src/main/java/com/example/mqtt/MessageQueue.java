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
import org.springframework.util.StringUtils;

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

    @Scheduled(cron = "0 */2 * * * *")
    public void run() throws MqttException, IOException, InterruptedException {
        HttpResponse<String> response = getWeatherFromWeatherMap();

        float temperature = (new JSONObject(response.body()).getJSONObject("main").getBigDecimal("temp").floatValue()) - 273.15f;
        String weatherDescription = StringUtils.capitalize(new JSONObject(response.body()).getJSONArray("weather").getJSONObject(0).getString("description"));
        String cityName = (new JSONObject(response.body()).getString("name"));
        if (celsius < (temperature - 0.1) || celsius > (temperature + 0.1f)) {
            celsius = temperature;
            String celsiusString = cityName + ": " + String.format("%.2f", celsius) + ";" + weatherDescription;
            String publisherId = UUID.randomUUID().toString();
            IMqttClient client = new MqttClient("tcp://83.251.117.120", publisherId, new MqttDefaultFilePersistence("src/main/resources/queue"));

            while (!client.isConnected()) {
                System.out.println("attempting to connect");
                client.connect();
            }
            client.publish("weatherdata/temperature", new MqttMessage(celsiusString.getBytes(StandardCharsets.UTF_8)));
            System.out.println(celsiusString);

        }

    }

    private HttpResponse<String> getWeatherFromWeatherMap() throws IOException, InterruptedException {
        String city = this.cityRepository.findCityById(1L).getCityName();
        if (city.contains(" ")) {
            city = city.replaceAll(" ", "%20");
        }
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.openweathermap.org/data/2.5/weather?q=" + city.trim() + "&appid=34f1dfe47079741556f631b2639de87d"))
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        return response;
    }
}
