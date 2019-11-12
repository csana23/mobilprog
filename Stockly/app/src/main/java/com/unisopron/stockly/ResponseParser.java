package com.unisopron.stockly;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class ResponseParser {

    Gson gson = new Gson();

    // get whole response
    public JsonObject responseToJsonObject(String responseString) {
        return gson.fromJson(responseString, JsonObject.class);
    }

    // get metadata from responseString
    public ArrayList<String> getMetadata(JsonObject received) {
        JsonObject metaJson = received.get("Meta Data").getAsJsonObject();

        // parse info
        String information;
        String symbol;
        String lastRefreshed;
        String outputSize;
        String timeZone;

        information = metaJson.get("1. Information").getAsString();
        symbol = metaJson.get("2. Symbol").getAsString();
        lastRefreshed = metaJson.get("3. Last Refreshed").getAsString();
        outputSize = metaJson.get("4. Output Size").getAsString();
        timeZone = metaJson.get("5. Time Zone").getAsString();

        // add to ArrayList
        ArrayList<String> metadata = new ArrayList<String>();
        metadata.add(information);
        metadata.add(symbol);
        metadata.add(lastRefreshed);
        metadata.add(outputSize);
        metadata.add(timeZone);

        return metadata;
    }

    public Map<String, Map<String, Double>> getTimeSeries(JsonObject received) {
        Map<String, Map<String, Double>> ret = new LinkedHashMap<>();

        try {
            JsonObject timeSeries = received.get("Time Series (Daily)").getAsJsonObject();

            for (Map.Entry<String, JsonElement> e : timeSeries.entrySet()) {
                Map<String, Double> toAdd = new LinkedHashMap<>();
                JsonObject obj = (JsonObject) e.getValue();
                toAdd.put("close", obj.get("4. close").getAsDouble());
                toAdd.put("volume", obj.get("5. volume").getAsDouble());
                ret.put(e.getKey(), toAdd);
            }
        } catch (Exception e) {
            Log.d("Gson", "Gson problem");
        }

        return ret;
    }

    public  LinkedHashMap<String, Double> parseTimeSeriesMap(Map<String, Map<String, Double>> timeSeriesData) {
        LinkedHashMap<String, Double> closePrices = new LinkedHashMap<String, Double>();

        ArrayList<String> timeStamps = new ArrayList<>();

        for (String timeStamp : timeSeriesData.keySet()) {
           /* double closePrice = timeSeriesData.get(timeStamp).get("close");

            closePrices.put(timeStamp, closePrice);*/
           timeStamps.add(timeStamp);
        }

        Collections.reverse(timeStamps);
        Log.d("reversedkeys ", timeStamps.toString());

        for (String timeStamp : timeStamps) {
            double closePrice = timeSeriesData.get(timeStamp).get("close");

            closePrices.put(timeStamp, closePrice);
        }

        Log.d("closePrices: ", closePrices.toString());

        return closePrices;
    }
}
