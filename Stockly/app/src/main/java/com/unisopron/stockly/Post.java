package com.unisopron.stockly;

import com.google.gson.annotations.SerializedName;

public class Post {

    @SerializedName("body")
    private String message;

    public String getMessage() {
        return message;
    }
}
