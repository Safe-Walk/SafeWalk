package com.sw.safewalk;

import java.io.Serializable;

public class SortData implements Serializable {
    private int distance;
    private boolean recent, danger;

    public int getDistance() {
        return distance;
    }

    public boolean getDanger() {
        return danger;
    }

    public boolean getRecent() {
        return recent;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public void setDanger(boolean danger) {
        this.danger = danger;
    }

    public void setRecent(boolean recent) {
        this.recent = recent;
    }

    public String toString() {
        return this.danger + " " + this.distance + " " + this.recent;
    }
}
