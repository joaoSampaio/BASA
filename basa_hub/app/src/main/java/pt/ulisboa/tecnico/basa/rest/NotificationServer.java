package pt.ulisboa.tecnico.basa.rest;

import android.util.Log;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import pt.ulisboa.tecnico.basa.app.AppController;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class NotificationServer {


    private static ApiNotification api;

    public static ApiNotification getApi() {
        return api;
    }

    public static final ApiNotification getService() {
        if (NotificationServer.getApi() == null) {

            Interceptor COOKIES_REQUEST_INTERCEPTOR = new Interceptor() {

                @Override
                public Response intercept(Chain chain) throws IOException {
//            Response response = chain.proceed(chain.request());
                    Request request = chain.request();
                    Request.Builder newRequest;

                    newRequest = request.newBuilder()
                            .addHeader("Content-Type", "application/json")
                            .addHeader("Authorization", "key=" + AppController.getInstance().getServerKey());
                    Log.d("sever", "server key:" + AppController.getInstance().getServerKey());
                    return chain.proceed(newRequest.build());
                }
            };


            Dispatcher dispatcher=new Dispatcher();
            dispatcher.setMaxRequests(10);
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient.Builder client = new OkHttpClient.Builder();
            client.readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(120, TimeUnit.SECONDS)
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .interceptors().add(COOKIES_REQUEST_INTERCEPTOR);
            client.interceptors().add(interceptor);
            client.dispatcher(dispatcher);

            client.connectionPool(new ConnectionPool(20, 5 * 60 , TimeUnit.MINUTES));

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://fcm.googleapis.com/")
                    .client(client.build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            NotificationServer.api = retrofit.create(ApiNotification.class);
        }


        return NotificationServer.getApi();
    }


}
