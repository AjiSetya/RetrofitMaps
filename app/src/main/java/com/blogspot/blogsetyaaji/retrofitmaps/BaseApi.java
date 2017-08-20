package com.blogspot.blogsetyaaji.retrofitmaps;

import com.blogspot.blogsetyaaji.retrofitmaps.Model.MProperties;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

/**
 * Created by AJISETYA on 8/20/2017.
 */

public interface BaseApi {
    @GET("getproperti.php")
    Call<MProperties> getProperties();

    class BaseApiUtama{
        public static BaseApi buat(){
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://192.168.20.64/app_masterproperti/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            return retrofit.create(BaseApi.class);
        }
    }
}
