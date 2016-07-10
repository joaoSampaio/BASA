package pt.ulisboa.tecnico.mybasaclient.rest.services;


import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import pt.ulisboa.tecnico.mybasaclient.Global;
import pt.ulisboa.tecnico.mybasaclient.model.DeviceStatus;
import pt.ulisboa.tecnico.mybasaclient.model.UserRegistration;
import pt.ulisboa.tecnico.mybasaclient.rest.RestClient;
import retrofit2.Call;
import retrofit2.Response;

public class GetDeviceStatusService extends ServerCommunicationService {

    private CallbackFromService callback;
    private String url;
    private UserRegistration registration;
    public GetDeviceStatusService(String url, CallbackFromService callback){
        this.callback = callback;
        this.url = url + Global.HUB_ENDPOINT_STATUS;
    }

    @Override
    public void execute() {

        Call<JsonElement> call = RestClient.getService().deviceStatus(url);
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

                                Gson gson = new Gson();
                                DeviceStatus answer = gson.fromJson(obj.get("data"), new TypeToken<DeviceStatus>() {
                                }.getType());

                                callback.success(answer);

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

