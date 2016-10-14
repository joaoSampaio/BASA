package pt.ulisboa.tecnico.basa.rest.services;


import android.util.Log;

import java.io.IOException;

import okhttp3.ResponseBody;
import pt.ulisboa.tecnico.basa.app.AppController;
import pt.ulisboa.tecnico.basa.model.BasaDeviceConfig;
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




        int choice = AppController.getInstance().getDeviceConfig().getTemperatureChoice();
        if(choice == BasaDeviceConfig.TEMPERATURE_TYPE_MONITOR_CONTROL_ARDUINO){
            if(url == null || url.isEmpty())
                return;
            getTemperatureArduino();

        } else if(choice == BasaDeviceConfig.TEMPERATURE_TYPE_MONITOR_CONTROL_PEROMAS){
            getTemperaturePerOMAS();
        }

    }


    private void getTemperatureArduino(){
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



    private void getTemperaturePerOMAS(){


            if (isWifiAvailable()) {
                url = AppController.getInstance().getDeviceConfig().getPeromasIP()+"/index";

                Call<ResponseBody> call =  RestClient.getService().getStatusPerOMAS(url);
                call.enqueue(new retrofit2.Callback<ResponseBody>() {


                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                        if (response.isSuccessful()) {



                            boolean isLoggded = false;
                            try {
                                String body =  response.body().string();
                                String[] lines = body.split("\\r\\n|\\n|\\r");
                                String line;
                                    for(int i= 0; i< lines.length; i++){
                                        //sb.append(line);
                                        line = lines[i];
                                        //////////////////////////contains temperature
                                        if (line.contains("ºC")) {
                                            Log.d("peromas", "found joao" + line);
                                            String[] result = line.split("ºC");
                                            if(result.length > 0){
                                                result = result[0].split("<b>");
                                                Log.d("peromas", "found joao tmp" + result[1].trim());
                                                callback.success(new Temperature(Double.parseDouble(result[1].trim()), 50));
                                                break;

                                            }
                                            isLoggded = true;
                                            Log.d("myapp", "...contains");
                                        }
                                    }

                            } catch (IOException e) {
                                e.printStackTrace();
                            }


                        } else {
                            callback.failed(null);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        callback.failed("network problem");
                    }
                });

            }
        }



    }

