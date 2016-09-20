package pt.ulisboa.tecnico.basa.rest.services;


import android.util.Log;

import pt.ulisboa.tecnico.basa.rest.CallbackMultiple;
import pt.ulisboa.tecnico.basa.rest.Pojo.ArduinoChangeTemperature;
import pt.ulisboa.tecnico.basa.rest.Pojo.Temperature;
import pt.ulisboa.tecnico.basa.rest.RestClient;
import retrofit2.Call;
import retrofit2.Response;

public class ChangeTemperatureService extends ServerCommunicationService {

    private CallbackMultiple callback;
    private String url;
    private ArduinoChangeTemperature changeTemperature;
    public ChangeTemperatureService(String url, ArduinoChangeTemperature changeTemperature, CallbackMultiple callback){
        this.callback = callback;
        this.url = url;
        this.changeTemperature = changeTemperature;
    }

    @Override
    public void execute() {

        Call<Temperature> call = RestClient.getService().changeArduinoTemperature(url, changeTemperature);
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
