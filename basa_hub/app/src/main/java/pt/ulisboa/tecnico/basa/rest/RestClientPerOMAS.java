package pt.ulisboa.tecnico.basa.rest;

import android.util.Log;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.concurrent.TimeUnit;

import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import okhttp3.Interceptor;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.framed.Header;
import okhttp3.logging.HttpLoggingInterceptor;
import pt.ulisboa.tecnico.basa.util.ModelCache;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class RestClientPerOMAS {


    private static Api api;
    public static Api getApi() {
        return api;
    }

    public static final Api getService(String url) {
        if (RestClientPerOMAS.getApi() == null) {

            if(!url.endsWith("/"))
                url = url+"/";

            Interceptor COOKIES_REQUEST_INTERCEPTOR = new Interceptor() {

                @Override
                public Response intercept(Chain chain) throws IOException {
//            Response response = chain.proceed(chain.request());
                    Request request = chain.request();
                    Request.Builder newRequest;

                    String cookie = new ModelCache<String>().loadModel(String.class, "Set-Cookie", "");
                    Log.d("intercept", "cookie intercept setCookie:"+ request.headers().get("Set-Cookie"));
                    newRequest = request.newBuilder()
//                            .addHeader("Content-type", "application/json;charset=UTF-8")
//                            .addHeader("Accept", "application/json")
                            .header("Set-Cookie", cookie);
                    Log.d("intercept", "cookie: "+cookie);
                    Response response = chain.proceed(newRequest.build());
                    Log.d("intercept", "response-> cookie intercept setCookie:"+ response.headers().get("Set-Cookie"));
                    return response;
                }
            };

//            CookieJar cookieJar =
//                    new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(AppController.getAppContext()));

//            CookieJar cookieJar = new CookieJar() {
//                private final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();
//
//                @Override
//                public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
//                    cookieStore.put(url.host(), cookies);
//                }
//
//                @Override
//                public List<Cookie> loadForRequest(HttpUrl url) {
//                    List<Cookie> cookies = cookieStore.get(url.host());
//                    return cookies != null ? cookies : new ArrayList<Cookie>();
//                }
//            };


//            JavaNetCookieJar jncj = new JavaNetCookieJar(CookieHandler.getDefault());




            CookieManager cookieManager = new CookieManager();
            cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);

            Dispatcher dispatcher=new Dispatcher();
            dispatcher.setMaxRequests(10);
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient.Builder client = new OkHttpClient.Builder();
//            client.cookieJar(jncj);
//            client.cookieJar(new JavaNetCookieJar(cookieManager));
            client.readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(120, TimeUnit.SECONDS)
                    .connectTimeout(60, TimeUnit.SECONDS);
            client.interceptors().add(interceptor);
            //client.interceptors().add(COOKIES_REQUEST_INTERCEPTOR);


            client.interceptors().add(new AddCookiesInterceptor());
            client.interceptors().add(new ReceivedCookiesInterceptor());

            client.dispatcher(dispatcher);

            client.connectionPool(new ConnectionPool(20, 5 * 60 , TimeUnit.MINUTES));

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .client(client.build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            RestClientPerOMAS.api = retrofit.create(Api.class);
        }


        return RestClientPerOMAS.getApi();
    }


}
