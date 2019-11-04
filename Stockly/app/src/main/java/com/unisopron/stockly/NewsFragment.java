package com.unisopron.stockly;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NewsFragment extends Fragment {

    private TextView textViewResult;
    private String responseString;
    private ResponseParser responseParser;
    private ArrayList<String> metadata;
    private HashMap<String, Double> timeSeriesData;

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

                if (!response.isSuccessful()) {
                    textViewResult.setText("Code: " + response.code());
                    return;
                }

                try {
                    responseString = response.body().string();
                    System.out.println("eyyo" + responseString);
                } catch (Exception e) {
                    //
                }

                // get json text
                responseParser = new ResponseParser();
                JsonObject received = responseParser.responseToJsonObject(responseString);

                // get metadata
                metadata = new ArrayList<String>();
                metadata = responseParser.getMetadata(received);

                // get time series data
                Map<String, Map<String, Double>> ret = responseParser.getTimeSeries(received);

                timeSeriesData = new HashMap<String, Double>();
                timeSeriesData = responseParser.parseTimeSeriesMap(ret);

                textViewResult.setText(timeSeriesData.toString());
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                textViewResult.setText(t.getMessage());
            }
        });

        return view;
    }
}
