package com.unisopron.stockly;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.gson.JsonObject;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MarketFragment extends Fragment {

    private LineChart chart;

    private String responseString;
    private ResponseParser responseParser;
    private ArrayList<String> metadata;
    private HashMap<String, Double> timeSeriesData;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view =inflater.inflate(R.layout.fragment_market, container, false);

        // get data for chart
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
                    Toast.makeText(view.getContext(), "@string/error_message", Toast.LENGTH_SHORT).show();
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
                metadata = new ArrayList<>();
                metadata = responseParser.getMetadata(received);

                // get time series data
                Map<String, Map<String, Double>> ret = responseParser.getTimeSeries(received);

                timeSeriesData = new LinkedHashMap<>();
                timeSeriesData = responseParser.parseTimeSeriesMap(ret);
                Log.d("timeSeriesData", timeSeriesData.toString());

                // chart
                chart = view.findViewById(R.id.chart);

                ArrayList<Entry> values = new ArrayList<>();
                ArrayList<ILineDataSet> dataSets = new ArrayList<>();
                List<String> xAxisValues = new ArrayList<>();

                int i = 0;

                for (String key : timeSeriesData.keySet()) {
                    // can only pray this works
                    Log.d("boi keys", key);
                    values.add(new Entry(i, timeSeriesData.get(key).intValue()));
                    xAxisValues.add(key);
                    i++;
                }

                LineDataSet set1;

                set1 = new LineDataSet(values, "DJX point at market closure");
                set1.setColor(Color.rgb(216,27,96));
                set1.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                dataSets.add(set1);

                // customization
                chart.setTouchEnabled(true);
                chart.setDragEnabled(true);
                chart.setScaleEnabled(true);
                chart.setPinchZoom(true);
                chart.setDrawGridBackground(false);
                chart.setExtraLeftOffset(15);
                chart.setExtraRightOffset(15);
                chart.getXAxis().setDrawGridLines(true);
                chart.getAxisLeft().setDrawGridLines(true);
                chart.getAxisRight().setDrawGridLines(true);

                YAxis rightYAxis = chart.getAxisRight();
                rightYAxis.setEnabled(false);
                YAxis leftYAxis = chart.getAxisLeft();
                leftYAxis.setEnabled(true);
                XAxis topXAxis = chart.getXAxis();
                topXAxis.setEnabled(false);

                // axes
                XAxis xAxis = chart.getXAxis();
                xAxis.setGranularity(5f);
                xAxis.setCenterAxisLabels(true);
                xAxis.setEnabled(true);
                xAxis.setDrawGridLines(false);
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

                set1.setLineWidth(2f);
                //set1.setCircleRadius(2f);
                set1.setDrawValues(false);

                chart.getXAxis().setValueFormatter(new com.github.mikephil.charting.formatter.IndexAxisValueFormatter(xAxisValues));

                LineData data = new LineData(dataSets);
                chart.setData(data);
                //chart.animateX(2000);
                chart.invalidate();

                // legend
                Legend legend = chart.getLegend();
                legend.setEnabled(true);
                legend.setForm(Legend.LegendForm.LINE);
                chart.getDescription().setEnabled(false);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(view.getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}
