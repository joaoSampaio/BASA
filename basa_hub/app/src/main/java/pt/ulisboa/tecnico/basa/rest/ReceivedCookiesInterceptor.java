package pt.ulisboa.tecnico.basa.rest;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.io.IOException;
import java.util.HashSet;
import java.util.prefs.Preferences;

import okhttp3.Interceptor;
import okhttp3.Response;
import pt.ulisboa.tecnico.basa.app.AppController;

/**
 * Created by joao on 20-10-2016.
 */
public class ReceivedCookiesInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Response originalResponse = chain.proceed(chain.request());

        if (!originalResponse.headers("Set-Cookie").isEmpty()) {
            HashSet<String> cookies = new HashSet<>();

            for (String header : originalResponse.headers("Set-Cookie")) {
                cookies.add(header);
            }
            PreferenceManager.getDefaultSharedPreferences(AppController.getAppContext()).edit()
                    .putStringSet("cookies", cookies)
                    .apply();
        }

        return originalResponse;
    }
}