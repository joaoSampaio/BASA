package pt.ulisboa.tecnico.basa.rest;

import com.google.gson.JsonElement;

import pt.ulisboa.tecnico.basa.rest.Pojo.ServerLocation;
import pt.ulisboa.tecnico.basa.rest.Pojo.Temperature;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Url;


public interface Api {


    @GET("hourly/lang:BR/q/autoip.json")
    Call<JsonElement> requestTemperature();


    @GET
    Call<Temperature> requestTemperatureOffice(@Url String url);

    @POST
    Call<Temperature> giveLocationToArduino(@Url String url, @Body ServerLocation server);

    @GET("http://192.168.0.102/temp")
    Call<JsonElement> requestTemperatureOffice2();


}