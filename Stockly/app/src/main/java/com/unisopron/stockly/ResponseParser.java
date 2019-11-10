package com.unisopron.stockly;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
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
        Map<String, Map<String, Double>> ret = new HashMap<>();

        try {
            JsonObject timeSeries = received.get("Time Series (Daily)").getAsJsonObject();

            for (Map.Entry<String, JsonElement> e : timeSeries.entrySet()) {
                Map<String, Double> toAdd = new HashMap<>();
                JsonObject obj = (JsonObject) e.getValue();
                toAdd.put("close", obj.get("4. close").getAsDouble());
                toAdd.put("volume", obj.get("5. volume").getAsDouble());
                ret.put(e.getKey(), toAdd);
            }
        } catch (Exception e) {
            //
        }

        return ret;
    }

    public  HashMap<String, Double> parseTimeSeriesMap(Map<String, Map<String, Double>> timeSeriesData) {
        LinkedHashMap<String, Double> closePrices = new LinkedHashMap<String, Double>();

        for (String timeStamp : timeSeriesData.keySet()) {
            double closePrice = timeSeriesData.get(timeStamp).get("close");

            closePrices.put(timeStamp, closePrice);
        }

        return closePrices;
    }
}
