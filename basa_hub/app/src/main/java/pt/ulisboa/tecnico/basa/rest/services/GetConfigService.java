package pt.ulisboa.tecnico.basa.rest.services;


import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import pt.ulisboa.tecnico.basa.model.registration.BasaDeviceLoad;
import pt.ulisboa.tecnico.basa.rest.CallbackMultiple;
import pt.ulisboa.tecnico.basa.rest.RestClient;
import retrofit2.Call;
import retrofit2.Response;

public class GetConfigService extends ServerCommunicationService {

    private CallbackMultiple callback;
    private String url;
    public GetConfigService(String url, CallbackMultiple callback){
        this.callback = callback;
        this.url = url.replace("\"", "");
    }

    @Override
    public void execute() {

        if(url == null || url.isEmpty())
            return;
        Call<JsonElement> call = RestClient.getService().getConfig(url);
            call.enqueue(new retrofit2.Callback<JsonElement>() {


                @Override
                public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {

                    Log.d("web", "response.isSuccessful():"+ response.isSuccessful());
                    if (response.isSuccessful() && response.body() != null) {

                        JsonElement json = response.body();
                        Gson gson = new Gson();
                        JsonObject obj = json.getAsJsonObject();

                        BasaDeviceLoad load = gson.fromJson(obj, new TypeToken<BasaDeviceLoad>() {
                        }.getType());

                        Log.d("web", "response.response.body():"+ response.body());
                        callback.success(load);

                    } else {
                        callback.failed(null);
                    }
                }

                @Override
                public void onFailure(Call<JsonElement> call, Throwable t) {
                    callback.failed("network problem");
                }
            });
        }
    }

