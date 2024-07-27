package org.assessment.graph.random;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class RandomUtil {

    private static final int LATITUDE_MIN = -90;
    private static final int LATITUDE_MAX = 90;
    private static final int LONGITUDE_MIN = -180;
    private static final int LONGITUDE_MAX = 180;
    private static final Random random = new Random();
    private static final List<String> cities;

    static {
        String fileName = "cities.txt";
        cities = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                cities.add(line.trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static String getCity() {
        if(cities.isEmpty()) return "";
        int randomIndex = random.nextInt(cities.size());
        return cities.remove(randomIndex);
    }

    public static double getLatitude() {
        return random.nextDouble(LATITUDE_MAX - LATITUDE_MIN + 1) + LATITUDE_MIN;
    }

    public static double getLongitude() {
        return random.nextDouble(LONGITUDE_MAX - LATITUDE_MIN + 1) + LONGITUDE_MIN;
    }
}
