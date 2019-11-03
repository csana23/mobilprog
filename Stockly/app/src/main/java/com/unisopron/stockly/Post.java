package com.unisopron.stockly;

import com.google.gson.annotations.SerializedName;

public class Post {

    private String symbol;
    private String date;
    private int close;
    private int volume;

    public String getSymbol() {
        return symbol;
    }

    public String getDate() {
        return date;
    }

    public int getClose() {
        return close;
    }

    public int getVolume() {
        return volume;
    }
}
