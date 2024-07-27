package org.assessment.graph.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
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
            doDFS(0, cities, lanes, cityList);
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
            doDFS(i, componentCities, componentLanes, componentCityList);
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

    public void doDFS(int city, int cities, int lanes, List<City> cityList)
        throws InterruptedException, ExecutionException {
        int attempt = 0;
        while (attempt < Integer.MAX_VALUE) {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<?> future = executor.submit(() -> start(city, cities, lanes, cityList));

            try {
                future.get(1, TimeUnit.SECONDS);
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


    private void start(int city, int cities, int lanes, List<City> cityList) {

        boolean[][] linkEstablished = new boolean[cities][cities];
        int[][] roadWeights = new int[cities][cities];
        boolean[] visited = new boolean[cities];
        List<Set<Integer>> hasLink = new ArrayList<>();
        for(int i = 0; i < cities; i++) hasLink.add(new HashSet<>());

        visited[city] = true;
        hasLink.get(city).add(4);

        dfs(city, 0, linkEstablished, roadWeights, lanes, visited, hasLink, cities, new RandomCityGenerator(cities), new HashSet<>());
        leader.add(cityList.get(city));


        for(int i = 0; i < cities; i++) {
            for(int j = 0; j < roadWeights[i].length; j++) {
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

        if(travelled.size() == cities - 1 && lanes == 0) {
            Arrays.stream(roadWeights).map(Arrays::toString).forEach(System.out::println);
            System.out.println(travelled.size());
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
                    cooked = dfs(neighbour, curr, linkEstablished, roadWeights, lanes - randomNextWeight, visited, hasLink, cities, random, travelled);
                } else {
                    cooked = dfs(neighbour, curr + 1, linkEstablished, roadWeights, lanes - randomNextWeight, visited, hasLink, cities, random, travelled);
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

    private static Set<Integer> connectionPossibleAndReturnRandom(int mine, int neighbour, List<Set<Integer>> hasLink) {

        Set<Integer> neighbourLinks = hasLink.get(neighbour);
        Set<Integer> currLinks = hasLink.get(mine);

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

}
