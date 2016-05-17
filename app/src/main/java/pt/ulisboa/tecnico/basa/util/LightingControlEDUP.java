package pt.ulisboa.tecnico.basa.util;

import android.os.AsyncTask;
import android.os.Process;
import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Created by Sampaio on 16/05/2016.
 */
public class LightingControlEDUP implements LightingControl {
    @Override
    public void sendLightCommand(boolean[] lighting) {

        Log.d("light", "sendLightCommand");
        int[] lights = new int[3];
        for (int i = 0; i< lighting.length; i++)
            lights[i] = lighting[i] ? 1 :  0;


//        new ContactEDUP(lights).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        new Thread(new ContactEDUPRunnable(lights)).start();
//        new ContactEDUPRunnable(lights).run();

    }



    private static byte[] combine(String content){

        byte[] start = hexStringToByteArray("7e7e001c0001");
        byte[] data = content.getBytes();
        byte[] end = hexStringToByteArray("7f7f");


        ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
        try {
            outputStream.write( start );
            outputStream.write( data );
            outputStream.write( end );
        } catch (IOException e) {
            e.printStackTrace();
        }

        return outputStream.toByteArray( );



    }

    private static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    private class ContactEDUPRunnable implements Runnable {
        /*
         * Defines the code to run for this task.
         */

        int[] lights;

        public ContactEDUPRunnable(int[] lights) {
            this.lights = lights;
        }

        @Override
        public void run() {
            // Moves the current Thread into the background
            android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO);


            Log.d("light", "doInBackground");
            String content = "ZH037CC7097B7CA91";
            for (int light : lights)
                content+=light;

            Log.d("light", "content:"+content);
            String ipaddress = "47.88.138.88";
//            String ipaddress = "servers.chitco.com.cn";
            int portnumber = 8081;
            String modifiedSentence;
            Socket clientSocket;
            try
            {
                clientSocket = new Socket(ipaddress, portnumber);
                DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
                BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                outToServer.write(combine(content));
                clientSocket.setSoTimeout(10000);
                modifiedSentence = inFromServer.readLine();
                clientSocket.close();
                outToServer.close();
                inFromServer.close();
                Log.d("light", "modifiedSentence:"+modifiedSentence);
            }
            catch (Exception exc)
            {
                exc.printStackTrace();

            }



        }
    }


    private class ContactEDUP extends AsyncTask<Void, Void, Void>{

        int[] lights;

        public ContactEDUP(int[] lights) {
            this.lights = lights;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d("light", "edup onPreExecute");
        }

        @Override
        protected Void doInBackground(Void... params) {

            Log.d("light", "doInBackground");
            String content = "ZH037CC7097B7CA91";
            for (int light : lights)
                content+=light;

            Log.d("light", "content:"+content);
            String ipaddress = "47.88.138.88";
//            String ipaddress = "servers.chitco.com.cn";
            int portnumber = 8081;
            String modifiedSentence;
            Socket clientSocket;
            try
            {
                clientSocket = new Socket(ipaddress, portnumber);
                DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
                BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                outToServer.write(combine(content));
                clientSocket.setSoTimeout(10000);
                modifiedSentence = inFromServer.readLine();
                clientSocket.close();
                outToServer.close();
                inFromServer.close();
                Log.d("light", "modifiedSentence:"+modifiedSentence);
            }
            catch (Exception exc)
            {
                exc.printStackTrace();
            }



            return null;
        }
    }

}
