package pt.ulisboa.tecnico.basa.rest;


import android.util.Log;

import pt.ulisboa.tecnico.basa.rest.Pojo.ServerLocation;
import pt.ulisboa.tecnico.basa.rest.Pojo.Temperature;
import retrofit2.Call;
import retrofit2.Response;

public class PostServerLocationService extends ServerCommunicationService {

    private CallbackMultiple callback;
    private String url, server;
    private ServerLocation serverLocation;
    public PostServerLocationService(String url, ServerLocation serverLocation, CallbackMultiple callback){
        this.callback = callback;
        this.url = url;
        this.serverLocation = serverLocation;
    }

    @Override
    public void execute() {

        Call<Temperature> call = RestClient.getService().giveLocationToArduino(url, serverLocation);
            call.enqueue(new retrofit2.Callback<Temperature>() {


                @Override
                public void onResponse(Call<Temperature> call, Response<Temperature> response) {

                    Log.d("web", "response.isSuccessful():"+ response.isSuccessful());
                    if (response.isSuccessful()) {
                        callback.success(null);

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

