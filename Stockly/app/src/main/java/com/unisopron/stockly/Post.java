package com.unisopron.stockly;

import com.google.gson.annotations.SerializedName;

public class Post {

    private String metaData;

    @SerializedName("body")
    private String timeSeries;

    private String dayData;

    public String getMetaData() {
        return metaData;
    }

    public String getTimeSeries() {
        return timeSeries;
    }

    public String getDayData() {
        return dayData;
    }


}
