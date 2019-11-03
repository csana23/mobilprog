package com.unisopron.stockly;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface AlphaVantageApi {

    @GET("query?function=TIME_SERIES_DAILY&symbol=.DJI&apikey=BMOZOE9D5X9ECSP9")
    Call<List<Post>> getPosts();

}
