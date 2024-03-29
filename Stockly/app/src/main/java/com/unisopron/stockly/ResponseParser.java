package com.unisopron.stockly;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

public class ResponseParser {

    Gson gson = new Gson();

    // get whole response
    public JsonObject responseToJsonObject(String responseString) {
        return gson.fromJson(responseString, JsonObject.class);
    }

    public int errorHandler(JsonObject received) {
        try {
            JsonElement errorMessage = received.getAsJsonPrimitive("Error Message");
            Log.d("testError", errorMessage.toString());

        } catch (Exception e) {
            Log.d("errorHandlercatch", "ez van: " + e.getMessage());
            return 0;
        }
        return 1;
    }

    public int errorHandler2(JsonObject received) {
        try {
            JsonElement errorMessage2 = received.getAsJsonPrimitive("Note");
            Log.d("testError", errorMessage2.toString());

        } catch (Exception e) {
            Log.d("errorHandler2catch", "ez van2: " + e.getMessage());
            return 0;
        }
        return 1;
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


            JsonObject timeSeries = received.get("Time Series (Daily)").getAsJsonObject();

            for (Map.Entry<String, JsonElement> e : timeSeries.entrySet()) {
                Map<String, Double> toAdd = new LinkedHashMap<>();
                JsonObject obj = (JsonObject) e.getValue();
                toAdd.put("open", obj.get("1. open").getAsDouble());
                toAdd.put("high", obj.get("2. high").getAsDouble());
                toAdd.put("low", obj.get("3. low").getAsDouble());
                toAdd.put("close", obj.get("4. close").getAsDouble());
                ret.put(e.getKey(), toAdd);
            }

            return ret;

    }

    public  LinkedList<LinkedHashMap> parseTimeSeriesMap(Map<String, Map<String, Double>> timeSeriesData) {
        LinkedHashMap<String, Double> openPrices = new LinkedHashMap<String, Double>();
        LinkedHashMap<String, Double> highPrices = new LinkedHashMap<String, Double>();
        LinkedHashMap<String, Double> lowPrices = new LinkedHashMap<String, Double>();
        LinkedHashMap<String, Double> closePrices = new LinkedHashMap<>();

        ArrayList<String> timeStamps = new ArrayList<>();


            // to return a linkedlist of linkedhashmaps
            LinkedList<LinkedHashMap> prices = new LinkedList<>();

            for (String timeStamp : timeSeriesData.keySet()) {
                timeStamps.add(timeStamp);
            }

            Collections.reverse(timeStamps);
            Log.d("reversedkeys ", timeStamps.toString());

            for (String timeStamp : timeStamps) {
                double openPrice = timeSeriesData.get(timeStamp).get("open");
                double highPrice = timeSeriesData.get(timeStamp).get("high");
                double lowPrice = timeSeriesData.get(timeStamp).get("low");
                double closePrice = timeSeriesData.get(timeStamp).get("close");

                openPrices.put(timeStamp, openPrice);
                highPrices.put(timeStamp, highPrice);
                lowPrices.put(timeStamp, lowPrice);
                closePrices.put(timeStamp, closePrice);
            }
            Log.d("openPrices: ", openPrices.toString());
            Log.d("highPrices: ", highPrices.toString());
            Log.d("lowPrices: ", lowPrices.toString());
            Log.d("closePrices: ", closePrices.toString());

            prices.add(openPrices);
            prices.add(highPrices);
            prices.add(lowPrices);
            prices.add(closePrices);

            return prices;

    }
}
