package pt.ulisboa.tecnico.mybasaclient.broadcastReceiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import pt.ulisboa.tecnico.mybasaclient.R;
import pt.ulisboa.tecnico.mybasaclient.app.AppController;

public class WifiBroadcastReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        Log.d("wifi", "WifiBroadcastReceiver:" + action);



        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi1 = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        boolean isConnected = wifi1 != null && wifi1.isConnectedOrConnecting() ||
                mobile != null && mobile.isConnectedOrConnecting();
        if (isConnected) {
            Log.d("Network Available ", "YES");
        } else {
            Log.d("Network Available ", "NO");
        }




        if(intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE") && isConnected){


            if(AppController.getInstance().getLoggedUser() != null &&
                    AppController.getInstance().getLoggedUser().isEnableTracking() && isConnected) {

                Alarm alarm = new Alarm();
                alarm.cancelAlarm(context);
                alarm.setAlarm(context);

                //context.startService(new Intent(context, WifiLocationService.class));
            }

        }


        if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
            Log.d("wifi", "SCAN_RESULTS_AVAILABLE_ACTION:" );

        }


        if (WifiManager.NETWORK_STATE_CHANGED_ACTION .equals(action)) {
            Log.d("wifi", "******************NETWORK_STATE_CHANGED_ACTION");
        }

        if (action.equals(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION)) {
            Log.d("wifi", "******************SUPPLICANT_CONNECTION_CHANGE_ACTION");

            if (intent.getBooleanExtra(WifiManager.EXTRA_SUPPLICANT_CONNECTED, false)) {
                WifiManager wifiManager =
                        (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

                WifiInfo wifi = wifiManager.getConnectionInfo();
                Log.d("wifi3", "wifi != null:" + (wifi != null));
                if (wifi != null) {
                    // get current router Mac address
                    Log.d("wifi3", "conected to SSID:" + wifi.getSSID());

                }
            } else {
                // wifi connection was lost
            }
        }

        if (WifiManager.WIFI_STATE_CHANGED_ACTION .equals(action)) {

            Log.d("wifi", "******************WIFI_STATE_CHANGED_ACTION");


            SupplicantState state = intent.getParcelableExtra(WifiManager.EXTRA_NEW_STATE);
            if (SupplicantState.isValidState(state)
                    && state == SupplicantState.COMPLETED) {

                WifiManager wifiManager =
                        (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

                WifiInfo wifi = wifiManager.getConnectionInfo();
                Log.d("wifi", "wifi != null:" + (wifi != null));
                if (wifi != null) {
                    // get current router Mac address
                    Log.d("wifi", "conected to SSID:" + wifi.getSSID());

                }
            }
        }
    }


    /**
     * Creates a notification displaying the SSID & MAC addr
     */
    private void createNotification(String ssid, String mac, Context ctx) {
        Log.d("wifi", "createNotification");
        Notification n = new NotificationCompat.Builder(ctx)
                .setContentTitle("Wifi Connection")
                .setContentText("visible network: " + ssid)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("You're connected to " + ssid + " at " + mac))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000})
                .build();
        ((NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE))
                .notify(0, n);
    }


}
