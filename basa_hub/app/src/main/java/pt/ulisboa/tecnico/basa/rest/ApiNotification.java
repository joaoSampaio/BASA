package pt.ulisboa.tecnico.basa.rest;

import com.google.gson.JsonElement;

import pt.ulisboa.tecnico.basa.rest.Pojo.FcmNotification;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;


public interface ApiNotification {



    @POST("fcm/send")
    Call<JsonElement> sendNotification(@Body FcmNotification data);




}