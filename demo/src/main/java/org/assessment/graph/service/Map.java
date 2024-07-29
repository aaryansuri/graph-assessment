package org.assessment.graph.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;
import org.assessment.graph.pojos.City;
import org.assessment.graph.random.RandomUtil;

public class Map {
    private List<City> cities;

    public void generateRandomCities(int num) {
        cities = new ArrayList<>();
        IntStream.range(0, num).forEach(i -> {
            String name = RandomUtil.getCity();
            double latitude = RandomUtil.getLatitude();
            double longitude = RandomUtil.getLongitude();
            City city = new City(name, latitude, longitude);
            if(Objects.equals(name, "")) return;
            System.out.println(city);
            cities.add(city);
        });
    }

    public List<City> getCities() {
        return cities;
    }
}
