package com.example.yanbraslavski.bitcoingraph.api.models;

/**
 * Created by yan.braslavski on 8/16/16.
 */
public class GraphValueModel {

    private long x;
    private double y;

    public long getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    @Override
    public String toString() {
        return "GraphValueModel{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
