package com.example.yanbraslavski.bitcoingraph.api.models;

import java.io.Serializable;
import java.util.List;

public class GraphModel implements Serializable {

    private String status;
    private String name;
    private String unit;
    private String period;
    private List<GraphValueModel> values;

    public String getPeriod() {
        return period;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }

    public String getUnit() {
        return unit;
    }

    public List<GraphValueModel> getValues() {
        return values;
    }

    @Override
    public String toString() {
        return "GraphModel{" +
                "name='" + name + '\'' +
                ", status='" + status + '\'' +
                ", unit='" + unit + '\'' +
                ", period='" + period + '\'' +
                ", values=" + values +
                '}';
    }
}
