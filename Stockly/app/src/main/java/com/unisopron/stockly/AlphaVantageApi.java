package com.unisopron.stockly;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;

public interface AlphaVantageApi {

    @GET("query?function=TIME_SERIES_DAILY&symbol=.DJI&outputsize=compact&apikey=BMOZOE9D5X9ECSP9")
    Call<ResponseBody> getResponse();

}
