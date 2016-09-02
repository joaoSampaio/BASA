package pt.ulisboa.tecnico.mybasaclient.rest.services;


import android.util.Log;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import pt.ulisboa.tecnico.mybasaclient.Global;
import pt.ulisboa.tecnico.mybasaclient.app.AppController;
import pt.ulisboa.tecnico.mybasaclient.model.BasaDevice;
import pt.ulisboa.tecnico.mybasaclient.model.User;
import pt.ulisboa.tecnico.mybasaclient.rest.RestClient;
import pt.ulisboa.tecnico.mybasaclient.rest.pojo.UserLocation;
import pt.ulisboa.tecnico.mybasaclient.util.FirebaseHelper;
import retrofit2.Call;
import retrofit2.Response;

public class UpdateLocationService extends ServerCommunicationService {

    private CallbackFromService callback;
    private String url, server, deviceId;
    private UserLocation userLocation;
    public UpdateLocationService(BasaDevice device, UserLocation userLocation, CallbackFromService callback){
        this.callback = callback;
        this.deviceId = device.getId();
        this.url = device.getUrl() + Global.HUB_ENDPOINT_LOCATION;
        this.userLocation = userLocation;
    }

    @Override
    public void execute() {

        User user = AppController.getInstance().getLoggedUser();
        if(user.isEnableFirebase()){
            new FirebaseHelper().updateLocation(deviceId, user.getUuid(), userLocation);
        }else{
            Log.d("register", "ip:"+url);
            Call<JsonElement> call = RestClient.getService().updateLocation(url, userLocation);
                call.enqueue(new retrofit2.Callback<JsonElement>() {


                    @Override
                    public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {

                        Log.d("web", "response.isSuccessful():" + response.isSuccessful());
                        if (response.isSuccessful()) {


                            JsonElement json = response.body();
                            if (json != null) {
                                JsonObject obj = json.getAsJsonObject();
                                boolean status = obj.get("status").getAsBoolean();
                                if(status)
                                    callback.success(status);
                                else
                                    callback.failed(null);

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
    }

