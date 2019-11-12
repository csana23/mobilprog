package com.unisopron.stockly;

import java.util.LinkedHashMap;
import java.util.LinkedList;
public class MovingAverage {

    /*int period;
    double sum;
    LinkedList<Double> list;

    public MovingAverage() {
        this.period = 10;
        this.sum = 0;
        list = new LinkedList<>();
    }

    public LinkedList<Double> getData(LinkedHashMap<String, Double> timeSeriesData) {
        LinkedList<Double> extractedData = new LinkedList<>();

        for (String key : timeSeriesData.keySet()) {
            extractedData.add(timeSeriesData.get(key));
        }
        return extractedData;
    }

    public double next(double val) {
        sum += val;
        list.offer(val);

        if(list.size() <= period) {
            return sum/list.size();
        }

        sum -= list.poll();

        return sum/period;
    }*/

}
