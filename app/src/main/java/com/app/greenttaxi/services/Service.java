package com.app.greenttaxi.services;




import com.app.greenttaxi.models.UserModel;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface Service {

    @FormUrlEncoded
    @POST("api/login")
    Call<UserModel> login(@Field("phone") String phone);


    @FormUrlEncoded
    @POST("api/update-location")
    Call<UserModel> updateLocation(
                                   @Field("user_id") String user_id,
                                   @Field("latitude") double latitude,
                                   @Field("longitude") double longitude

    );


}