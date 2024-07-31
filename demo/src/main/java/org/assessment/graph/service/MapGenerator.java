package org.assessment.graph.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.OptionalInt;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import org.assessment.graph.random.RandomCityGenerator;
import org.assessment.graph.pojos.City;
import org.assessment.graph.pojos.LaneType;
import org.assessment.graph.pojos.Road;

public class MapGenerator {

    private List<Road> roads;
    private List<City> cityList;
    private List<City> leader;
    private int lanes;
    private int nationalHighways;
    private int cities;

    public MapGenerator(int cities, int lanes, int nationalHighways) {
        this.cities = cities;
        this.lanes = lanes;
        this.nationalHighways = nationalHighways;
        Map map = new Map();
        map.generateRandomCities(cities);
        cityList = map.getCities();
        leader = new ArrayList<>();
        roads = new ArrayList<>();
    }

    public List<Road> getRoads() {
        return roads;
    }

    public List<City> getCityList() {
        return cityList;
    }

    public void generateGraph()
        throws ExecutionException, InterruptedException {

        if (nationalHighways == 0) {
            doDFS(0, cities, lanes, cityList, true);
            System.out.println("roads");
            roads.forEach(System.out::println);
            System.out.println("leader");
            leader.forEach(System.out::println);
            return;
        }

        // we are creating some connected components
        // if national highways = 3
        // create 4 components 1st then connect them together through NH

        lanes -= nationalHighways * 4;

        int numberOfComponents = nationalHighways + 1;
        int citiesPerComponent = cities / numberOfComponents;
        int lanesPerComponent = lanes / numberOfComponents;

        int extraCities = cities % numberOfComponents;
        int extraLanes = lanes % numberOfComponents;
        int startIndex = 0;

        for (int i = 0; i < numberOfComponents; i++) {
            int componentCities = citiesPerComponent + (i < extraCities ? 1 : 0);
            int componentLanes = lanesPerComponent + (i < extraLanes ? 1 : 0);
            List<City> componentCityList = cityList.subList(startIndex, startIndex + componentCities);
            doDFS(i, componentCities, componentLanes, componentCityList, false);
            startIndex += componentCities;
        }


        for(int i = 0; i < leader.size(); i++) {
            if(i == leader.size() - 1) continue;
            roads.add(new Road(leader.get(i), leader.get(i + 1), LaneType.NATIONAL_HIGHWAY));
        }

        System.out.println("roads");
        roads.forEach(System.out::println);
        System.out.println("leader");
        leader.forEach(System.out::println);
    }

    public void doDFS(int city, int cities, int lanes, List<City> cityList, boolean alone)
        throws InterruptedException, ExecutionException {
        int attempt = 0;
        while (attempt < Integer.MAX_VALUE) {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<?> future = executor.submit(() -> start(city, cities, lanes, cityList, alone));

            try {
                future.get(60, TimeUnit.SECONDS);
                return;
            } catch (TimeoutException e) {
                System.out.println("DFS Attempt " + (attempt + 1) + " timed out. Retrying...");
                future.cancel(true);
                attempt++;
            } finally {
                executor.shutdown();
            }
        }

    }


    private void start(int city, int cities, int lanes, List<City> cityList, boolean alone) {

//        boolean[][] linkEstablished = new boolean[cities][cities];
        int[][] roadWeights = new int[cities + 2][cities + 2];
//        boolean[] visited = new boolean[cities + 2];
//        List<Set<Integer>> hasLink = new ArrayList<>();
//        for(int i = 0; i <= cities; i++) hasLink.add(new HashSet<>());

        System.out.println(city);
//        System.out.println(Arrays.toString(visited));
        System.out.println(cities);
//        visited[0] = true;
        if(!alone) {
            roadWeights[0][0] = 4;
        }


        Set<Integer> travelled = new HashSet<>();
        travelled.add(city);
//        dfs(city, 0, linkEstablished, roadWeights, lanes, visited, hasLink, cities, new RandomCityGenerator(cities), travelled);
        boolean[][][] cantMakeMemo = new boolean[cities + 2][lanes + 1][1 << (cities + 1)];
        dfsDP(city, roadWeights, lanes, cities, cantMakeMemo, 1);
        leader.add(cityList.get(0));

        if(!alone) {
            roadWeights[0][0] = 0;
        }

        System.out.println("CC created " + city);

        for(int i = 0; i < cities; i++) {
            for(int j = 0; j < cities; j++) {
                int weight = roadWeights[i][j];
                if(weight != 0) {
                    roads.add(new Road(cityList.get(i), cityList.get(j), LaneType.getLane(weight)));
                    roadWeights[i][j] = 0;  roadWeights[j][i] = 0;
                }
            }
        }

    }

    private boolean dfs(
        int city,
        int curr,
        boolean[][] linkEstablished,
        int[][] roadWeights,
        int lanes,
        boolean[] visited,
        List<Set<Integer>> hasLink,
        int cities,
        RandomCityGenerator random,
        Set<Integer> travelled
    ) {

        if(checkInvalidMove(roadWeights)) return false;
        if(travelled.size() == cities - 1 && lanes == 0) {
            Arrays.stream(roadWeights).map(Arrays::toString).forEach(System.out::println);
            hasLink.forEach(System.out::println);
            System.out.println(travelled.size());
            System.out.println(city);
            return true;
        }

        if(lanes <= 0) return false;
        if(lanes < cities - curr - 1) return false;
        if(curr == cities - 1) {
            random.reset();
        }

        for(int i = 0; i < cities; i++) {
            int neighbour = random.getRandomCity();
            if(neighbour == city) continue;
            Set<Integer> currWeights = hasLink.get(city);
            if(linkEstablished[city][neighbour]) continue; // already connected

            for(int randomNextWeight : connectionPossibleAndReturnRandom(city, neighbour, hasLink)) {
                currWeights.add(randomNextWeight);
                hasLink.get(neighbour).add(randomNextWeight);
                linkEstablished[city][neighbour] = true;
                linkEstablished[neighbour][city] = true;
                roadWeights[city][neighbour] = randomNextWeight;
                roadWeights[neighbour][city] = randomNextWeight;
                boolean cooked = false;
                visited[city] = true;
                travelled.add(city);
                if(visited[neighbour]) {
                    cooked = dfsDP(neighbour, curr, linkEstablished, roadWeights, lanes - randomNextWeight, visited, hasLink, cities, random, travelled);
                } else {
                    cooked = dfsDP(neighbour, curr + 1, linkEstablished, roadWeights, lanes - randomNextWeight, visited, hasLink, cities, random, travelled);
                }
                if(cooked) {
                    return true;
                }
                travelled.remove(city);
                visited[city] = false;
                roadWeights[city][neighbour] = 0;
                roadWeights[neighbour][city] = 0;
                hasLink.get(neighbour).remove(randomNextWeight);
                currWeights.remove(randomNextWeight);
                linkEstablished[city][neighbour] = false;
                linkEstablished[neighbour][city] = false;
            }

        }

        return false;
    }

    private boolean dfsDP(
        int city,
        int[][] roadWeights,
        int lanes,
        int cities,
        boolean[][][] cantMake,
        int mask
    ) {

        if((mask == (1 << cities) - 1) && lanes == 0) {
//            Arrays.stream(roadWeights).map(Arrays::toString).forEach(System.out::println);
            System.out.println(city);
            return true;
        }

        if(lanes <= 0) return false;
        if(lanes < cities - Integer.bitCount(mask) - 1) return false;
        if(cantMake[city][lanes][mask]) {
            return false;
        }

        for(int neighbour = 0; neighbour < cities; neighbour++) {
            if(neighbour == city) continue;
            // 1. Travel neighbor if no link or if neighbour has not been covered yet in the map
            if((mask & (1 << neighbour)) == 0 || roadWeights[city][neighbour] == 0) {
                for(int conn : possibleConnection(city, neighbour, roadWeights)) {
                    roadWeights[city][neighbour] = conn;
                    roadWeights[neighbour][city] = conn;
                    int newMask = mask | (1 << neighbour);
                    boolean makeConnectionAndGoNeigh = dfsDP(neighbour, roadWeights, lanes - conn, cities, cantMake, newMask);
                    if(makeConnectionAndGoNeigh) return true;
                    boolean makeConnectionAndStay = dfsDP(city, roadWeights, lanes - conn, cities, cantMake, newMask);
                    if(makeConnectionAndStay) return true;
                    roadWeights[city][neighbour] = 0;
                    roadWeights[neighbour][city] = 0;
                }
            }
        }

        cantMake[city][lanes][mask] = true;

        return false;
    }

    private static Set<Integer> connectionPossibleAndReturnRandom(int mine, int neighbour, List<Set<Integer>> hasLink) {

        Set<Integer> neighbourLinks = hasLink.get(neighbour);
        Set<Integer> currLinks = hasLink.get(mine);

        if(currLinks.isEmpty() && neighbourLinks.isEmpty()) {
            return Set.of(1, 2, 3);
        }

        if (neighbourLinks.isEmpty() && currLinks.size() == 1) {
            int num = currLinks.iterator().next();
            switch (num) {
                case 4:
                    return Set.of(3);
                case 3:
                    return Set.of(2, 3);
                case 1:
                    return Set.of(1, 2);
                case 2:
                    return Set.of(3, 2, 1);
            }
        }

        for (int curr : currLinks) {
            for (int neigh : neighbourLinks) {
                if (Math.abs(curr - neigh) > 1) {
                    return Set.of();
                }
            }
        }

        if (currLinks.equals(neighbourLinks)) {
            return new HashSet<>(currLinks);
        }


        Set<Integer> filteredCurrLinks = currLinks.stream().filter(n -> n != 0 && n != 4).collect(Collectors.toSet());
        Set<Integer> filteredNeighbourLinks = neighbourLinks.stream().filter(n -> n != 0 && n != 4).collect(Collectors.toSet());

        return filteredCurrLinks.size() >= filteredNeighbourLinks.size() ? filteredCurrLinks : filteredNeighbourLinks;
    }


    // stream is slow
    private static Set<Integer> possibleConnection(int mine, int neighbour, int[][] roadWeights) {
        Set<Integer> neighbourLinks = new HashSet<>();
        Set<Integer> currLinks = new HashSet<>();

        // Collect non-zero road weights
        for (int weight : roadWeights[neighbour]) {
            if (weight != 0) neighbourLinks.add(weight);
        }
        for (int weight : roadWeights[mine]) {
            if (weight != 0) currLinks.add(weight);
        }

        // Case when both sets are empty
        if (currLinks.isEmpty() && neighbourLinks.isEmpty()) {
            return Set.of(1, 2, 3);
        }

        // Case when neighbourLinks is empty and currLinks has only one element
        if (neighbourLinks.isEmpty() && currLinks.size() == 1) {
            int num = currLinks.iterator().next();
            switch (num) {
                case 4:
                    return Set.of(3);
                case 3:
                    return Set.of(2, 3);
                case 1:
                    return Set.of(1, 2);
                case 2:
                    return Set.of(1, 2, 3);
            }
        }

        // Check if any difference between elements is greater than 1
        for (int curr : currLinks) {
            for (int neigh : neighbourLinks) {
                if (Math.abs(curr - neigh) > 1) {
                    return Set.of();
                }
            }
        }

        // If sets are equal, return a new set with the same elements
        if (currLinks.equals(neighbourLinks)) {
            return new HashSet<>(currLinks);
        }

        // Filter out 0 and 4 from both sets
        Set<Integer> filteredCurrLinks = new HashSet<>();
        Set<Integer> filteredNeighbourLinks = new HashSet<>();
        for (int n : currLinks) {
            if (n != 0 && n != 4) filteredCurrLinks.add(n);
        }
        for (int n : neighbourLinks) {
            if (n != 0 && n != 4) filteredNeighbourLinks.add(n);
        }

        // Return the larger set
        return filteredCurrLinks.size() >= filteredNeighbourLinks.size() ? filteredCurrLinks : filteredNeighbourLinks;
    }

    private static boolean checkInvalidMove(int[][] array) {
        for (int[] row : array) {
            // Filter out zeros
            int[] nonZeroElements = Arrays.stream(row)
                .filter(value -> value != 0)
                .toArray();

            // If the row has only one or no non-zero elements, skip it
            if (nonZeroElements.length <= 1) {
                continue;
            }

            // Find min and max of the filtered row
            OptionalInt min = Arrays.stream(nonZeroElements).min();
            OptionalInt max = Arrays.stream(nonZeroElements).max();

            // Check if the difference is greater than 1
            if (min.isPresent() && max.isPresent() && (max.getAsInt() - min.getAsInt() > 1)) {
                return true;
            }
        }
        return false;
    }

}
