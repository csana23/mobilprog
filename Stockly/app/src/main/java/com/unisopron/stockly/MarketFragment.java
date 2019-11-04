package com.unisopron.stockly;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.components.YAxis.AxisDependency;
import com.google.gson.JsonObject;


import java.util.ArrayList;
import java.util.HashMap;
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
                metadata = new ArrayList<String>();
                metadata = responseParser.getMetadata(received);

                // get time series data
                Map<String, Map<String, Double>> ret = responseParser.getTimeSeries(received);

                timeSeriesData = new HashMap<String, Double>();
                timeSeriesData = responseParser.parseTimeSeriesMap(ret);


            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(view.getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        chart = view.findViewById(R.id.chart);

        ArrayList<Entry> values = new ArrayList<>();
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        List<String> xAxisValues = new ArrayList<>();

        int i = 1;

        for (String key : timeSeriesData.keySet()) {
            // can only pray this works
            values.add(new Entry(i, timeSeriesData.get(key).floatValue()));
            xAxisValues.add(key);
            i++;
        }

        LineDataSet set1;

        set1 = new LineDataSet(values, "Closing price");
        set1.setColor(Color.rgb(216,27,96));
        set1.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSets.add(set1);

        // customization
        chart.setTouchEnabled(true);
        chart.setDragEnabled(true);
        chart.setScaleEnabled(false);
        chart.setPinchZoom(false);
        chart.setDrawGridBackground(false);
        chart.setExtraLeftOffset(15);
        chart.setExtraRightOffset(15);
        chart.getXAxis().setDrawGridLines(false);
        chart.getAxisLeft().setDrawGridLines(false);
        chart.getAxisRight().setDrawGridLines(false);

        YAxis rightYAxis = chart.getAxisRight();
        rightYAxis.setEnabled(false);
        YAxis leftYAxis = chart.getAxisLeft();
        leftYAxis.setEnabled(false);
        XAxis topXAxis = chart.getXAxis();
        topXAxis.setEnabled(false);

        // axes
        XAxis xAxis = chart.getXAxis();
        xAxis.setTextSize(11f);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setTextColor(ColorTemplate.getHoloBlue());
        leftAxis.setAxisMaximum(200f);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setDrawGridLines(true);
        leftAxis.setGranularityEnabled(true);

        if (chart.getData() != null && chart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) chart.getData().getDataSetByIndex(0);
        } else {
            set1 = new LineDataSet(values, "bitch boi get rekt");

            set1.setAxisDependency(AxisDependency.LEFT);
            set1.setColor(ColorTemplate.getHoloBlue());
            set1.setCircleColor(Color.WHITE);
            set1.setLineWidth(2f);
            set1.setCircleRadius(3f);
            set1.setFillAlpha(65);
            set1.setFillColor(ColorTemplate.getHoloBlue());
            set1.setHighLightColor(Color.rgb(244, 117, 117));
            set1.setDrawCircleHole(false);

            LineData data = new LineData(set1);

            data.setValueTextColor(Color.WHITE);
            data.setValueTextSize(9f);

            chart.setData(data);
        }

        return view;
    }
}
