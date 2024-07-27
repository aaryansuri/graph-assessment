package org.assessment.graph.pojos;

public class Road {
    private City city1;
    private City city2;
    private LaneType laneType;

    public Road(City city1, City city2, LaneType laneType) {
        this.city1 = city1;
        this.city2 = city2;
        this.laneType = laneType;
    }

    public City getCity1() {
        return city1;
    }

    public City getCity2() {
        return city2;
    }

    public LaneType getLaneType() {
        return laneType;
    }

    @Override
    public String toString() {
        return "Road{" +
            "A=" + city1 +
            ", B=" + city2 +
            ", laneType=" + laneType +
            '}';
    }
}
