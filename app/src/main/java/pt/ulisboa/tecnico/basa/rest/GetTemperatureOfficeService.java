package pt.ulisboa.tecnico.basa.rest;


import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import pt.ulisboa.tecnico.basa.model.WeatherForecast;
import retrofit2.Call;

public class GetTemperatureOfficeService extends ServerCommunicationService {

    private CallbackMultiple callback;
    public GetTemperatureOfficeService(CallbackMultiple callback){
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

                            Log.d("temperature", ""+obj.getAsString());
                            double temperature = 0;
                            if(obj.get("temperature") != null)
                                temperature = obj.get("temperature").getAsDouble();






                            callback.success(temperature);
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

