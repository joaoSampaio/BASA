package pt.ulisboa.tecnico.basa.rest.services;


import android.util.Log;

import pt.ulisboa.tecnico.basa.rest.CallbackMultiple;
import pt.ulisboa.tecnico.basa.rest.Pojo.Temperature;
import pt.ulisboa.tecnico.basa.rest.RestClient;
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

        if(url == null || url.isEmpty())
            return;
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

