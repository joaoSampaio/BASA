package pt.ulisboa.tecnico.mybasaclient.rest.services;


import android.util.Log;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import pt.ulisboa.tecnico.mybasaclient.Global;
import pt.ulisboa.tecnico.mybasaclient.rest.RestClient;
import pt.ulisboa.tecnico.mybasaclient.rest.pojo.ChangeTemperatureLights;
import retrofit2.Call;
import retrofit2.Response;

public class ChangeTemperatureLightsService extends ServerCommunicationService {

    private CallbackFromService callback;
    private String url, server;
    private ChangeTemperatureLights changeTemperatureLights;
    public ChangeTemperatureLightsService(String url, ChangeTemperatureLights changeTemperatureLights, CallbackFromService callback){
        this.callback = callback;
        this.url = url + Global.HUB_ENDPOINT_MAKE_CHANGES;
        this.changeTemperatureLights = changeTemperatureLights;
    }

    @Override
    public void execute() {

        Log.d("register", "ip:"+url);
        Call<JsonElement> call = RestClient.getService().changeTemperatureLights(url, changeTemperatureLights);
            call.enqueue(new retrofit2.Callback<JsonElement>() {


                @Override
                public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {

                    Log.d("web", "response.isSuccessful():" + response.isSuccessful());
                    if (response.isSuccessful()) {


                        JsonElement json = response.body();
                        if (json != null) {
                            JsonObject obj = json.getAsJsonObject();
                            boolean status = obj.get("status").getAsBoolean();
                            if (status) {

                                callback.success(true);

                            } else
                                callback.failed("falhou");
                            //callback.failed(obj.get("error").getAsString());

                        } else {
                            callback.failed(null);
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

