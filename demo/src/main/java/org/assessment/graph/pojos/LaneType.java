package org.assessment.graph.pojos;


import scala.io.BytePickle.PU;

/**
 * 4 lanes: National Highways
 * 3 lanes: Inter-State Highways
 * 2 lanes: Highways
 * 1 lane: Main Roads
 */
public enum LaneType {

    NATIONAL_HIGHWAY(4, "nh"),
    INTER_STATE_HIGHWAY(3, "ish"),
    HIGHWAYS(2, "h"),
    MAIN_ROADS(1, "mr");

    private final int lanes;
    private final String style;

    LaneType(int lanes, String style) {
        this.lanes = lanes;
        this.style = style;
    }

    @Override
    public String toString() {
        return style;
    }

    public static LaneType getLane(int lanes) {
        for (LaneType type : LaneType.values()) {
            if (type.getLanes() == lanes) {
                return type;
            }
        }
        throw new IllegalArgumentException("No LaneType with " + lanes + " lanes.");
    }

    public static String getStyle(LaneType laneType) {
        for (LaneType type : LaneType.values()) {
            if (type == laneType) {
                return type.getStyle();
            }
        }
        throw new IllegalArgumentException("No LaneStyleType with " + laneType + " lanes.");
    }

    public String getStyle() {
        return style;
    }

    public int getLanes() {
        return lanes;
    }
}
