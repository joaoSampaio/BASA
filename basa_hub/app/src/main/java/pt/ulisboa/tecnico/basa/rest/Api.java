package pt.ulisboa.tecnico.basa.rest;

import com.google.gson.JsonElement;

import okhttp3.ResponseBody;
import pt.ulisboa.tecnico.basa.rest.Pojo.ArduinoChangeTemperature;
import pt.ulisboa.tecnico.basa.rest.Pojo.ServerLocation;
import pt.ulisboa.tecnico.basa.rest.Pojo.Temperature;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Url;


public interface Api {


    @GET("hourly/lang:BR/q/autoip.json")
    Call<JsonElement> requestTemperature();


    @GET
    Call<Temperature> requestTemperatureOffice(@Url String url);

    @POST
    Call<Temperature> changeArduinoTemperature(@Url String url, @Body ArduinoChangeTemperature arduino);

    @POST
    Call<Temperature> giveLocationToArduino(@Url String url, @Body ServerLocation server);

    @GET("http://192.168.0.102/temp")
    Call<JsonElement> requestTemperatureOffice2();


    @GET
    Call<JsonElement> getConfig(@Url String url);

    @GET
    Call<ResponseBody> getStatusPerOMAS(@Url String url);

    @GET
    Call<ResponseBody> loginPerOMASGetToken(@Url String url);

    @Multipart
    @POST
    Call<ResponseBody> loginPerOMAS(@Url String url, @Part("username") String username, @Part("password") String password, @Part("remember_me") String remember_me, @Part("csrf_token") String csrf_token);

}