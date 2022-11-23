package com.example.mqtt;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@AllArgsConstructor
public class CityService {
    CityRepository cityRepository;

    @Transactional
    public void updateCity(String cityName) {
        City city = cityRepository.findCityById(1L);
        city.setCityName(cityName);
    }

    public City getCurrentCityById() {
        return cityRepository.findCityById(1L);
    }
}
