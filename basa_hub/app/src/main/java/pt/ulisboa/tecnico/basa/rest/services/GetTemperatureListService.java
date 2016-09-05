package pt.ulisboa.tecnico.basa.rest.services;


import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;


import pt.ulisboa.tecnico.basa.model.WeatherForecast;

import pt.ulisboa.tecnico.basa.rest.CallbackMultiple;
import pt.ulisboa.tecnico.basa.rest.RestClient;
import retrofit2.Call;

public class GetTemperatureListService extends ServerCommunicationService {

    private CallbackMultiple callback;
    public GetTemperatureListService(CallbackMultiple callback){
        this.callback = callback;
    }

    @Override
    public void execute() {

            Call<JsonElement> call = RestClient.getService().requestTemperature();
            call.enqueue(new retrofit2.Callback<JsonElement>() {
                @Override
                public void onResponse(Call<JsonElement> call, retrofit2.Response<JsonElement> response) {

                    Log.d("web", "response.isSuccessful():"+ response.isSuccessful());
                    if (response.isSuccessful()) {
                        JsonElement json = response.body();
                        if (json != null) {
                            Gson gson = new Gson();
                            JsonObject obj = json.getAsJsonObject();

                            WeatherForecast forecast = gson.fromJson(obj, new TypeToken<WeatherForecast>() {
                            }.getType());





                            callback.success(forecast);
                        } else {
                            callback.success(null);
                        }
                    }
                }

                @Override
                public void onFailure(Call<JsonElement> call, Throwable t) {
                    callback.failed("network problem");
                }
            });
        }
    }

