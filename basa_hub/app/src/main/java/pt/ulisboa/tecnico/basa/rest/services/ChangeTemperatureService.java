package pt.ulisboa.tecnico.basa.rest.services;


import android.support.annotation.NonNull;
import android.util.Log;

import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import pt.ulisboa.tecnico.basa.app.AppController;
import pt.ulisboa.tecnico.basa.model.BasaDeviceConfig;
import pt.ulisboa.tecnico.basa.rest.CallbackMultiple;
import pt.ulisboa.tecnico.basa.rest.Pojo.ArduinoChangeTemperature;
import pt.ulisboa.tecnico.basa.rest.Pojo.Temperature;
import pt.ulisboa.tecnico.basa.rest.RestClient;
import pt.ulisboa.tecnico.basa.rest.RestClientPerOMAS;
import pt.ulisboa.tecnico.basa.util.ModelCache;
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


        int choice = AppController.getInstance().getDeviceConfig().getTemperatureChoice();
        if(choice == BasaDeviceConfig.TEMPERATURE_TYPE_MONITOR_CONTROL_ARDUINO){
            if(url == null || url.isEmpty())
                return;
            changeArduinoTemperature();

            } else if(choice == BasaDeviceConfig.TEMPERATURE_TYPE_MONITOR_CONTROL_PEROMAS){
                changeTempPerOMAS();
            }

        }


    private void changeArduinoTemperature() {

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


        public static final String MULTIPART_FORM_DATA = "multipart/form-data";

        @NonNull
        private RequestBody createPartFromString(String descriptionString) {
            return RequestBody.create(
                    MediaType.parse(MULTIPART_FORM_DATA), descriptionString);
        }


        private void changeTempPerOMAS(){
            //login
            String username = AppController.getInstance().getDeviceConfig().getPeromasUser();
            String password = AppController.getInstance().getDeviceConfig().getPeromasPass();
            final String ip = AppController.getInstance().getDeviceConfig().getPeromasIP();





            new LoginPerOMASService(username, password, ip, new CallbackMultiple() {
                @Override
                public void success(Object response) {
                    //getTemperaturePerOMAS();



                    RequestBody AC_OFF = createPartFromString("AC_OFF");
                    RequestBody AC_3 = createPartFromString("AC_3");

//                    HashMap<String, RequestBody> map = new HashMap<>();
                    HashMap<String, String> map = new HashMap<>();

                    if(changeTemperature.isOff()){
//                        map.put("AC_OFF", AC_OFF);
                        map.put("AC_OFF", "AC_OFF");
                    }else{
//                        map.put("AC_3", AC_3);
                        map.put("AC_3", "AC_3");
                    }

//                    String cookie = new ModelCache<String>().loadModel(String.class, "Set-Cookie", "");
//                    url = AppController.getInstance().getDeviceConfig().getPeromasIP()+"/index";
                    Call<ResponseBody> call = RestClientPerOMAS.getService(ip).setTemperaturePerOMAS(map);
                    call.enqueue(new retrofit2.Callback<ResponseBody>() {


                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                            Log.d("peromas", "response.isSuccessful() 222:"+ response.isSuccessful());
                            if (response.isSuccessful()) {
                                callback.success(null);

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

                @Override
                public void failed(Object error) {

                }
            }).execute();




        }


    }

