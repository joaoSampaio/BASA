package pt.ulisboa.tecnico.basa.rest;

import android.preference.PreferenceManager;
import android.util.Log;

import java.io.IOException;
import java.util.HashSet;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import pt.ulisboa.tecnico.basa.app.AppController;

/**
 * Created by joao on 20-10-2016.
 */
public class AddCookiesInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder();
        HashSet<String> preferences = (HashSet) PreferenceManager
                .getDefaultSharedPreferences(AppController.getAppContext())
                .getStringSet("cookies", new HashSet<String>());
        builder.header("User-Agent", "basa");
        for (String cookie : preferences) {
            builder.addHeader("Cookie", cookie);
            Log.v("OkHttp", "Adding Header: " + cookie); // This is done so I know which headers are being added; this interceptor is used after the normal logging of OkHttp
        }

        return chain.proceed(builder.build());
    }
}
