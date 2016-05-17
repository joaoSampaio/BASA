package pt.ulisboa.tecnico.basa.rest;

import com.google.gson.JsonElement;

import retrofit2.Call;
import retrofit2.http.GET;


public interface ApiTemperature {


    @GET("action")
    Call<JsonElement> requestTemperature();




}