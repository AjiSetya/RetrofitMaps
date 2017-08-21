package com.blogspot.blogsetyaaji.retrofitmaps;

import com.blogspot.blogsetyaaji.retrofitmaps.Model.MProperties;
import com.blogspot.blogsetyaaji.retrofitmaps.Model.Propertus;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by AJISETYA on 8/20/2017.
 */

public interface BaseApi {
    @GET("getproperti.php")
    Call<MProperties> getProperties();

    @FormUrlEncoded
    @POST("hapusproperti.php")
    Call<MProperties> hapusProperti(@Field("txtid") String id);

    @FormUrlEncoded
    @POST("editproperti.php")
    Call<MProperties> editProperti(@Field("txtid") String id
            , @Field("txtnama") String nama
            , @Field("txtjenis") String jenis
            , @Field("txtlatitude") double latitude
            , @Field("txtlongitude") double longitude);

    @FormUrlEncoded
    @POST("inputproperti.php")
    Call<MProperties> insertProperti(@Field("txtnama") String nama
            , @Field("txtjenis") String jenis
            , @Field("txtlatitude") double latitude
            , @Field("txtlongitude") double longitude);

    class BaseApiUtama {
        public static BaseApi buat() {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://192.168.95.120/app_masterproperti/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            return retrofit.create(BaseApi.class);
        }
    }
}
