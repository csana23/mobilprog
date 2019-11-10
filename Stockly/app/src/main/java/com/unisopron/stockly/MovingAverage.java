package com.unisopron.stockly;

import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MovingAverage {

    int period;
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

        if(list.size()<=size){
            return sum/list.size();
        }

        sum -= list.poll();

        return sum/size;
    }

    public LinkedList<Double> getMovingAverages(LinkedList<Double> set) {
        int period = 10;
        double sum = 0;
        LinkedList<Double> list = new LinkedList<>();

        for (double x : set) {
            sum += x;
            list.offer(x);

            if (list.size() <= period) {
                set.add(sum/list.size());
            }
            sum -= list.poll();

            set.add(sum/period);
        }

        return set;
    }

}
