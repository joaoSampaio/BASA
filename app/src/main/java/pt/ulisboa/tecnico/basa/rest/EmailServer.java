package pt.ulisboa.tecnico.basa.rest;

import com.google.gson.JsonElement;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by Sampaio on 08/06/2016.
 */
public class EmailServer {

    private static final String ENDPOINT = "https://api.mailgun.net/v3/sandboxcd9dd12daa4a40c49f4f9f7bcc1c1a03.mailgun.org/";
    public static final String ACCEPT_JSON_HEADER = "Accept: application/json";
    public static final String BASIC = "Basic";


    private static SendMailApi sendMailApi;


    public static SendMailApi getApi() {
        return sendMailApi;
    }

    public interface SendMailApi {

        @Headers({ACCEPT_JSON_HEADER})
//        @FormUrlEncoded
        @Multipart
        @POST("messages")
        Call<JsonElement> send(
                @Header("Authorization") String authorizationHeader,
                @Part("from") RequestBody from,
                @Part("to") RequestBody to,
                @Part("subject") RequestBody subject,
                @Part("html") RequestBody text,
                @Part("inline\"; filename=\"qrcode.jpg") RequestBody attachment
        );
    }


    public static final SendMailApi getService() {
        if (EmailServer.getApi() == null) {

            Interceptor COOKIES_REQUEST_INTERCEPTOR = new Interceptor() {

                @Override
                public Response intercept(Chain chain) throws IOException {
//            Response response = chain.proceed(chain.request());
                    Request request = chain.request();
                    Request.Builder newRequest;

                    newRequest = request.newBuilder()
                            .addHeader("Content-type", "application/json;charset=UTF-8")
                            .addHeader("Accept", "application/json");
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
                    .baseUrl(ENDPOINT)
                    .client(client.build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            EmailServer.sendMailApi = retrofit.create(SendMailApi.class);
        }


        return EmailServer.getApi();
    }


//    public static ClientResponse SendSimpleMessage() {
//        Client client = Client.create();
//        client.addFilter(new HTTPBasicAuthFilter("api",
//                "key-f17b474c0d3fdd59952b2327f0a79339"));
//        WebResource webResource =
//                client.resource("https://api.mailgun.net/v3/sandboxcd9dd12daa4a40c49f4f9f7bcc1c1a03.mailgun.org/messages");
//        MultivaluedMapImpl formData = new MultivaluedMapImpl();
//        formData.add("from", "Mailgun Sandbox <postmaster@sandboxcd9dd12daa4a40c49f4f9f7bcc1c1a03.mailgun.org>");
//        formData.add("to", "joao <joaosampaio30@gmail.com>");
//        formData.add("subject", "Hello joao");
//        formData.add("text", "Congratulations joao, you just sent an email with Mailgun!  You are truly awesome!  You can see a record of this email in your logs: https://mailgun.com/cp/log .  You can send up to 300 emails/day from this sandbox server.  Next, you should add your own domain so you can send 10,000 emails/month for free.");
//        return webResource.type(MediaType.APPLICATION_FORM_URLENCODED).
//                post(ClientResponse.class, formData);
//    }

}
