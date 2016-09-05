package pt.ulisboa.tecnico.basa.rest.services;


import android.util.Log;

import com.google.gson.JsonElement;

import pt.ulisboa.tecnico.basa.rest.CallbackMultiple;
import pt.ulisboa.tecnico.basa.rest.NotificationServer;
import pt.ulisboa.tecnico.basa.rest.Pojo.FcmNotification;
import pt.ulisboa.tecnico.basa.rest.Pojo.FcmNotificationData;
import retrofit2.Call;
import retrofit2.Response;

public class SendNotificationService extends ServerCommunicationService {

    private CallbackMultiple callback;
    private String topic, message, senderId;
    private int code;
    public SendNotificationService(String deviceId, String message, String senderId, int code, CallbackMultiple callback){
        this.callback = callback;
        this.message = message;
        this.topic = "/topics/"+deviceId;
        this.code = code;
        this.senderId = senderId;

    }

    @Override
    public void execute() {

        Call<JsonElement> call = NotificationServer.getService().sendNotification(new FcmNotification(topic, new FcmNotificationData(message, senderId, code )));
            call.enqueue(new retrofit2.Callback<JsonElement>() {


                @Override
                public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {

                    Log.d("web", "response.isSuccessful():"+ response.isSuccessful());
                    if (response.isSuccessful()) {
                        callback.success(null);

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

