package com.unisopron.stockly;

import com.google.gson.annotations.SerializedName;

public class Post {

    private String metaData;

    @SerializedName("body")
    private String timeSeriesData;

    public String getMetaData() {
        return metaData;
    }

    public String getTimeSeriesData() {
        return timeSeriesData;
    }
}
