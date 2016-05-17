package pt.ulisboa.tecnico.basa.rest;

import com.google.gson.JsonElement;

import retrofit2.Call;
import retrofit2.http.GET;


public interface Api {


    @GET("hourly/lang:BR/q/autoip.json")
    Call<JsonElement> requestTemperature();




}