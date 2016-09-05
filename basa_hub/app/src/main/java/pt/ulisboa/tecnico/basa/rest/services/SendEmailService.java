package pt.ulisboa.tecnico.basa.rest.services;


import android.util.Base64;
import android.util.Log;

import com.google.gson.JsonElement;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import pt.ulisboa.tecnico.basa.rest.CallbackMultiple;
import pt.ulisboa.tecnico.basa.rest.EmailServer;
import pt.ulisboa.tecnico.basa.rest.services.ServerCommunicationService;
import retrofit2.Call;

public class SendEmailService extends ServerCommunicationService {

    public static final String BASIC = "Basic";
    private CallbackMultiple callback;
    private String to, subject, msg;
    private byte[] image;
    public SendEmailService(CallbackMultiple callback, String to, String subject, String msg, byte[] image){
        this.callback = callback;
        this.to = to;
        this.subject = subject;
        this.msg = msg;
        this.image = image;

    }




//    public void sendMail(String to, String subject, String msg){
//        String from = "User Name Maybe <mailgun@yourdomain.com>";
//        String clientIdAndSecret = "api" + ":" + "key-AdFEFtggxxxYourApiKey";
//        String authorizationHeader = BASIC + " " + Base64.encodeToString(clientIdAndSecret.getBytes(), Base64.NO_WRAP);
//        sendMailApi.authUser(authorizationHeader,from, to, subject, msg, cb);
//    }
//
    @Override
    public void execute() {

        String from = "User Name Maybe <postmaster@sandboxcd9dd12daa4a40c49f4f9f7bcc1c1a03.mailgun.org>";
        String clientIdAndSecret = "api" + ":" + "key-f17b474c0d3fdd59952b2327f0a79339";
        String authorizationHeader = BASIC + " " + Base64.encodeToString(clientIdAndSecret.getBytes(), Base64.NO_WRAP);

        RequestBody requestFile =
                RequestBody.create(MediaType.parse("multipart/form-data"), image);

        // MultipartBody.Part is used to send also the actual file name
        MultipartBody.Part body =
                MultipartBody.Part.createFormData("picture", "algo.jpeg", requestFile);


        RequestBody fromBody =  RequestBody.create(MediaType.parse("text/plain"), from);
        RequestBody toBody =  RequestBody.create(MediaType.parse("text/plain"), to);
        RequestBody subjectBody =  RequestBody.create(MediaType.parse("text/plain"), subject);
        RequestBody msgBody =  RequestBody.create(MediaType.parse("text/plain"), msg);
        RequestBody imageBody =  RequestBody.create(MediaType.parse("image/*"), image);

        Call<JsonElement> call = EmailServer.getService().send(authorizationHeader,fromBody, toBody, subjectBody, msgBody, imageBody);
        call.enqueue(new retrofit2.Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, retrofit2.Response<JsonElement> response) {

                Log.d("web", "response.isSuccessful():"+ response.isSuccessful());
                if (response.isSuccessful()) {
                    JsonElement json = response.body();
                    if (json != null) {

                    } else {
                        //callback.success(null);
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

