package com.unisopron.stockly;

import android.service.autofill.Dataset;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Queue;

public class MovingAverage {

    public MovingAverage() {
        this.period = 10;
        this.sum = 0;
    }

    public LinkedList<Double> getData(LinkedHashMap<String, Double> timeSeriesData) {
        LinkedList<Double> extractedData = new LinkedList<>();

        for (String key : timeSeriesData.keySet()) {
            extractedData.add(timeSeriesData.get(key));
        }
        return extractedData;
    }

    public void addData(double num) {
        sum += num;
        dataSet.add(num);

        if (dataSet.size() > period)
        {
            sum -= dataSet.remove();
        }
    }

    public double getMean() {
        return sum / period;
    }

    public LinkedList<Double> getMovingAverages(LinkedList<Double> set) {
        int period;
        double sum;
        double num;
        Queue<Double> dataSet = new LinkedList<>();

        return set;
    }

}
