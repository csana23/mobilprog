package com.unisopron.stockly;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NewsFragment extends Fragment {

    private TextView textViewResult;
    private TextView textViewResult2;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news, container, false);

        textViewResult = view.findViewById(R.id.text_view_result);

        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.alphavantage.co")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        AlphaVantageApi alphaVantageApi = retrofit.create(AlphaVantageApi.class);

        Call<ResponseBody> call = alphaVantageApi.getResponse();

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                String symbol = "";
                Map<String, Map<String, Double>> ret = new HashMap<>();

                if (!response.isSuccessful()) {
                    textViewResult.setText("Code: " + response.code());
                    return;
                }

                String responseString;

                try {

                    responseString = response.body().string();

                    Gson gson = new Gson();

                    // parse responseString
                    JsonObject received = gson.fromJson(responseString, JsonObject.class);
                    JsonObject meta = received.get("Meta Data").getAsJsonObject();

                    // metadata parser
                    String information;

                    String lastRefreshed;
                    String interval;
                    String outputSize;
                    String timeZone;

                    // textViewResult.setText(responseString);

                    information = meta.get("1. Information").getAsString();
                    symbol = meta.get("2. Symbol").getAsString();
                    System.out.println("The symbol is: " + symbol);
                    lastRefreshed = meta.get("3. Last Refreshed").getAsString();
                    interval = meta.get("4. Interval").getAsString();
                    outputSize = meta.get("5. Output Size").getAsString();
                    timeZone = meta.get("6. Time Zone").getAsString();

                    // Time series
                    String time = "Time Series (Daily)";

                    JsonObject timeSeries = received.get(time).getAsJsonObject();

                    for (Map.Entry<String, JsonElement> e : timeSeries.entrySet()) {
                        Map<String, Double> toAdd = new HashMap<>();
                        JsonObject obj = (JsonObject) e.getValue();
                        toAdd.put("open", obj.get("1. open").getAsDouble());
                        toAdd.put("high", obj.get("2. high").getAsDouble());
                        toAdd.put("low", obj.get("3. low").getAsDouble());
                        toAdd.put("close", obj.get("4. close").getAsDouble());
                        toAdd.put("volume", obj.get("5. volume").getAsDouble());
                        ret.put(e.getKey(), toAdd);
                    }

                    // now i have toAdd map containing time series data

                    //textViewResult2.setText(ret.toSt

                } catch (Exception e) {
                    //
                }

                textViewResult.setText(ret.toString());

            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                textViewResult.setText(t.getMessage());
            }
        });

        return view;
    }
}
