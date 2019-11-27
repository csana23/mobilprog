package com.unisopron.stockly;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface AlphaVantageApi {

    @GET
    Call<ResponseBody> getResponse(@Url String url);

}
