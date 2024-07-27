package org.assessment.graph.random;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

public class RandomCityGenerator {
    private List<Integer> cities;
    private List<Integer> availableCities;
    private Random random = new Random();

    public RandomCityGenerator(int cities) {
        this.cities = IntStream.range(0, cities).boxed().toList();
        reset();
    }

    // Returns a unique city from the list
    public Integer getRandomCity() {
        if (availableCities.isEmpty()) {
            reset();
        }

        int index = random.nextInt(availableCities.size());
        return availableCities.remove(index);
    }

    // Resets the generator to allow the reuse of city names
    public void reset() {
        // Copy the initial list of city names to availableCities
        availableCities = new ArrayList<>(cities);
    }
}
