package com.example.mqtt;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class City {
    @Id
    private Long id;
    private String cityName;
    private float temperature;

    public City(Long id, String cityName) {
        this.id = id;
        this.cityName = cityName;
    }
}
