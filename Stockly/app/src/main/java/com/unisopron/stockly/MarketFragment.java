package com.unisopron.stockly;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.CandleStickChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.gson.JsonObject;


import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MarketFragment extends Fragment {

    private CandleStickChart chart;

    private String responseString;
    private ResponseParser responseParser;
    private LinkedList<LinkedHashMap> timeSeriesData;

    private LinkedHashMap<String, Double> openPrices = new LinkedHashMap<String, Double>();
    private LinkedHashMap<String, Double> highPrices = new LinkedHashMap<String, Double>();
    private LinkedHashMap<String, Double> lowPrices = new LinkedHashMap<String, Double>();
    private LinkedHashMap<String, Double> closePrices = new LinkedHashMap<String, Double>();

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
                } catch (Exception e) {
                    Log.d("responseString", "Something went wrong with responseString");
                }

                // get json text
                responseParser = new ResponseParser();
                JsonObject received = responseParser.responseToJsonObject(responseString);

                // get time series data
                Map<String, Map<String, Double>> ret = responseParser.getTimeSeries(received);

                timeSeriesData = new LinkedList<>();
                timeSeriesData = responseParser.parseTimeSeriesMap(ret);
                Log.d("timeSeriesData", timeSeriesData.toString());

                // chart
                chart = view.findViewById(R.id.chart);

                // old
                /* ArrayList<Entry> values = new ArrayList<>();
                ArrayList<ILineDataSet> dataSets = new ArrayList<>();*/
                List<String> xAxisValues = new ArrayList<>();

                // candle stick chart
                ArrayList<CandleEntry> values = new ArrayList<>();

                // int i = 0;

                /*for (String key : timeSeriesData.keySet()) {
                    // can only pray this works
                    Log.d("keys", key);
                    values.add(new Entry(i, timeSeriesData.get(key).intValue()));
                    xAxisValues.add(key);
                    i++;
                } */

                // load up linkedHashMaps
                for (int i = 0; i < timeSeriesData.size(); i++) {
                    openPrices = timeSeriesData.get(0);
                    highPrices = timeSeriesData.get(1);
                    lowPrices = timeSeriesData.get(2);
                    closePrices = timeSeriesData.get(3);
                }

                int j = 0;

                // load up values with <CandleEntry>
                for (String key : openPrices.keySet()) {
                    values.add(new CandleEntry(
                            j,
                            highPrices.get(key).floatValue(),
                            lowPrices.get(key).floatValue(),
                            openPrices.get(key).floatValue(),
                            closePrices.get(key).floatValue())
                    );
                    xAxisValues.add(key);
                    j++;
                }

                // LineDataSet set1;
                CandleDataSet set1;

                set1 = new CandleDataSet(values, ".DJX points");
                set1.setColor(Color.rgb(216,27,96));
                // set1.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                // set1.setDrawCircles(false);
                set1.setShadowColor(Color.BLUE);
                set1.setShadowWidth(0.8f);
                set1.setDecreasingColor(Color.rgb(255, 0, 0));
                set1.setDecreasingPaintStyle(Paint.Style.FILL);
                set1.setIncreasingColor(Color.rgb(0, 255, 0));
                set1.setIncreasingPaintStyle(Paint.Style.FILL);
                set1.setNeutralColor(Color.LTGRAY);
                set1.setDrawValues(false);
                // dataSets.add(set1);

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
                chart.setHighlightPerDragEnabled(true);
                chart.setDrawBorders(false);

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

                set1.setDrawValues(false);

                chart.getXAxis().setValueFormatter(new com.github.mikephil.charting.formatter.IndexAxisValueFormatter(xAxisValues));

                CandleData data = new CandleData(set1);
                chart.setData(data);
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
