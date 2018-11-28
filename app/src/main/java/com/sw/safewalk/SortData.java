package com.sw.safewalk;

import java.io.Serializable;

public class SortData implements Serializable {
    private int distance;
    private boolean recent, danger;
    private Long time;
    private String typeOfCrime;

    public int getDistance() {
        return distance;
    }

    public boolean getDanger() {
        return danger;
    }

    public boolean getRecent() {
        return recent;
    }

    public Long getTime() {
        return time;
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

    public void setTime(Long time) {
        this.time = time;
    }

    public String getTypeOfCrime() {
        return typeOfCrime;
    }

    public void setTypeOfCrime(String typeOfCrime) {
        this.typeOfCrime = typeOfCrime;
    }

    public String toString() {
        return this.danger + " " + this.distance + " " + this.recent + " " + this.typeOfCrime + " " + this.time;
    }
}
