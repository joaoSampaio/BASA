package pt.ulisboa.tecnico.basa.rest.services;


import android.util.Log;

import java.io.IOException;

import okhttp3.ResponseBody;
import pt.ulisboa.tecnico.basa.app.AppController;
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





    private void getTemperaturePerOMAS(){


            if (isWifiAvailable()) {
                Call<ResponseBody> call =  RestClient.getService().getStatusPerOMAS(url);
                call.enqueue(new retrofit2.Callback<ResponseBody>() {


                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                        Log.d("web", "response.isSuccessful():"+ response.isSuccessful());
                        if (response.isSuccessful()) {
                            callback.success(response.body());


                            try {
                                Log.d("web", "response.body().string():"+ response.body().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            boolean isLoggded = false;
                            try {
                                String[] lines = response.body().string().split(System.getProperty("line.separator"));
                                String line;
                                    for(int i= 0; i< lines.length; i++){
                                        //sb.append(line);
                                        line = lines[i];
                                        //////////////////////////contains temperature
                                        Log.d("myapp", "..." + line);
                                        if (line.contains("name=\"Light_1\"")) {
                                            isLoggded = true;
                                            Log.d("myapp", "...contains");
                                        }
                                    }

                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            //login
                            if (!isLoggded) {
                                String username = AppController.getInstance().getDeviceConfig().getPeromasUser();
                                String password = AppController.getInstance().getDeviceConfig().getPeromasPass();
                                String ip = AppController.getInstance().getDeviceConfig().getPeromasIP();


                                new LoginPerOMASService(username, password, ip, new CallbackMultiple() {
                                    @Override
                                    public void success(Object response) {
                                        getTemperaturePerOMAS();
                                    }

                                    @Override
                                    public void failed(Object error) {

                                    }
                                }).execute();
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

