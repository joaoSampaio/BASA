package pt.ulisboa.tecnico.basa.rest;


import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import pt.ulisboa.tecnico.basa.model.WeatherForecast;
import pt.ulisboa.tecnico.basa.rest.Pojo.Temperature;
import retrofit2.Call;
import retrofit2.Response;

public class GetTemperatureOfficeService extends ServerCommunicationService {

    private CallbackMultiple callback;
    private String url;
    public GetTemperatureOfficeService(String url, CallbackMultiple callback){
        this.callback = callback;
        this.url = url.replace("\"", "");
    }

    @Override
    public void execute() {

        Call<Temperature> call = RestClient.getService().requestTemperatureOffice(url);
            call.enqueue(new retrofit2.Callback<Temperature>() {


                @Override
                public void onResponse(Call<Temperature> call, Response<Temperature> response) {

                    Log.d("web", "response.isSuccessful():"+ response.isSuccessful());
                    if (response.isSuccessful()) {
                        callback.success(response.body());

                    } else {
                        callback.failed(null);
                    }
                }

                @Override
                public void onFailure(Call<Temperature> call, Throwable t) {
                    callback.failed("network problem");
                }
            });
        }
    }

