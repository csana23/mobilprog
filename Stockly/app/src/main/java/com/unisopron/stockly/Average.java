package com.unisopron.stockly;

import java.util.LinkedHashMap;
import java.util.LinkedList;

public class Average {

    public LinkedList<Double> getData(LinkedHashMap<String, Double> timeSeriesData) {
        LinkedList<Double> extractedData = new LinkedList<>();

        for (String key : timeSeriesData.keySet()) {
            extractedData.add(timeSeriesData.get(key));
        }
        return extractedData;
    }

    public LinkedList<Double> getAverage(LinkedList<Double> values) {
        double sum = 0;
        double avg = 0;
        double n = 100;

        LinkedList<Double> avgs = new LinkedList<>();

       for (double x : values) {
           sum += x;
       }

       avg = sum / n;

       // filling up average dataset
       for (int i = 0; i < 11; i++) {
            avgs.add(avg);
       }

       return avgs;
    }

}
