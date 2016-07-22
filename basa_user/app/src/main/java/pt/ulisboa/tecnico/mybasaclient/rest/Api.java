package pt.ulisboa.tecnico.mybasaclient.rest;

import com.google.gson.JsonElement;

import pt.ulisboa.tecnico.mybasaclient.model.UserRegistration;
import pt.ulisboa.tecnico.mybasaclient.rest.pojo.ChangeTemperatureLights;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Url;


public interface Api {


    @GET("hourly/lang:BR/q/autoip.json")
    Call<JsonElement> requestTemperature();


    @GET
    Call<JsonElement> requestTemperatureOffice(@Url String url);

    @GET
    Call<JsonElement> isAlive(@Url String url);

    @GET
    Call<JsonElement> deviceStatus(@Url String url);

    @GET
    Call<JsonElement> registerOnDevice(@Url String url);

    @POST
    Call<JsonElement> registerOnDevice(@Url String url,@Body UserRegistration registration);

    @POST
    Call<JsonElement> changeTemperatureLights(@Url String url,@Body ChangeTemperatureLights change);

}