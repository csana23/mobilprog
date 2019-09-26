package com.unisopron.stockly;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface JsonPlaceHolderApi {

    @GET("query?function=TIME_SERIES_DAILY&symbol=AAPL&apikey=BMOZOE9D5X9ECSP9")
    Call<List<Post>> getPosts();

}
