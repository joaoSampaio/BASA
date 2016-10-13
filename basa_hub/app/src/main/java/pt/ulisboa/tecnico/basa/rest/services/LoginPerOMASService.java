package pt.ulisboa.tecnico.basa.rest.services;


import android.util.Log;

import java.io.IOException;

import okhttp3.ResponseBody;
import pt.ulisboa.tecnico.basa.rest.CallbackMultiple;
import pt.ulisboa.tecnico.basa.rest.RestClient;
import retrofit2.Call;
import retrofit2.Response;

public class LoginPerOMASService extends ServerCommunicationService {

    private CallbackMultiple callback;
    String username;
    String password;
    String ip;
    String csrf_token;

    public LoginPerOMASService( String username, String password, String ip, CallbackMultiple callback) {
        this.callback = callback;
        this.username = username;
        this.password = password;
        this.ip = ip;
    }

    @Override
    public void execute() {

            Call<ResponseBody> call = RestClient.getService().loginPerOMASGetToken(ip+"/login");
            call.enqueue(new retrofit2.Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {

                    Log.d("web", "response.isSuccessful():"+ response.isSuccessful());
                    if (response.isSuccessful()) {


                        String[] lines = new String[0];
                        try {
                            lines = response.body().string().split(System.getProperty("line.separator"));
                            String line;

                            for(int i= 0; i< lines.length; i++){
                                line = lines[i];
                                if (line.contains("id=\"csrf_token\"")) {
                                    Log.d("myapp", "encontrou csrf_token");
                                    String[] values = line.split("value=\"");
                                    if (values.length > 0)
                                        csrf_token = values[1].split("\"")[0];
                                }
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        //loginWithToken(username, password, ip, csrf_token.trim());

                        //loginPost(username, password, "y", csrf_token, new Callback<Response>() {
                        Call<ResponseBody> call2 = RestClient.getService().loginPerOMAS(ip+"/login", username, password, "y", csrf_token);
                        call2.enqueue(new retrofit2.Callback<ResponseBody>() {
                                          @Override
                                          public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                              if (response.isSuccessful()) {
                                                  callback.success(null);
                                              }else{
                                                  callback.failed("erro qq 2");
                                              }
                                          }

                                          @Override
                                          public void onFailure(Call<ResponseBody> call, Throwable t) {

                                          }
                                      });


                            //callback.success(forecast);
                        } else {
                            callback.failed("erro qq");
                        }
                    }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    callback.failed("network problem");
                }
            });
        }
    }

