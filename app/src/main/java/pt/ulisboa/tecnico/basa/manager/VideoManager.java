package pt.ulisboa.tecnico.basa.manager;

import android.app.Activity;
import android.graphics.ImageFormat;
import android.graphics.YuvImage;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.text.format.Formatter;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import pt.ulisboa.tecnico.basa.ui.MainActivity;

/**
 * Created by Sampaio on 16/04/2016.
 */
public class VideoManager {

    private MainActivity activity;
    private Socket socket;
    private String TAG = "steam";
    private DataOutputStream stream;
    private boolean prepared, streaming, running;
    private String boundary = "separador";
    private ByteArrayOutputStream buffer;





    public static String SERVERIP="";
    public static final int SERVERPORT = 8080;
    private Handler handler = new Handler();
    private ServerSocket serverSocket;


    public VideoManager(MainActivity activity){
        this.activity = activity;
    }



    public void start(){
        buffer = new ByteArrayOutputStream();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(!running)
                    new ServerASYNC().execute();
            }
        },500);

    }


    public class ServerASYNC extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... params) {
            try
            {
                running = true;
                WifiManager wm = (WifiManager) getActivity().getSystemService(Activity.WIFI_SERVICE);
                String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
                Log.i(TAG, "The server is running in -> " + ip + ":5001");
                ServerSocket server = new ServerSocket(5001);


                socket = server.accept();

                server.close();

                Log.i(TAG, "New connection to :" + socket.getInetAddress());

                stream = new DataOutputStream(socket.getOutputStream());
                prepared = true;
            }
            catch (IOException e)
            {
                Log.e(TAG, e.getMessage());
                running = false;
            }


            if (stream != null)
            {
                try
                {
                    // send the header

                stream.write(("HTTP/1.0 200 OK\r\n" +
                        "Server: iRecon\r\n" +
                        "Connection: close\r\n" +
                        "Max-Age: 0\r\n" +
                        "Expires: 0\r\n" +
                        "Cache-Control: no-store, no-cache, must-revalidate, pre-check=0, post-check=0, max-age=0\r\n" +
                        "Pragma: no-cache\r\n" +
                        "Content-Type: multipart/x-mixed-replace; " +
                        "boundary=" + boundary + "\r\n" +
                        "\r\n" +
                        "--" + boundary + "\r\n").getBytes());

                    stream.flush();

                    streaming = true;
                }
                catch (IOException e)
                {
                    Log.i(TAG, "error :" + e.getMessage());
                    running = false;
                }
            }


            return null;
        }
    }





    public void sendImagePacket(byte[] frame){
        if (stream == null) {
            Log.i(TAG, "stream == null :");
            return;

        }
        try
        {

            Log.i(TAG, "sending stream image :");
            // buffer is a ByteArrayOutputStream
            buffer.reset();

            buffer.write(frame);



            buffer.flush();

            // write the content header
            stream.write(("--" + boundary + "\r\n" +
                    "Content-type: image/jpg\r\n" +
                    "Content-Length: " + buffer.size() +
                    "\r\n\r\n").getBytes());

            Long tsLong = System.currentTimeMillis()/1000;
            String timestamp = tsLong.toString();


            stream.write(("Content-type: image/jpeg\r\n" +
                    "Content-Length: " + buffer.size() + "\r\n" +
                    "X-Timestamp:" + timestamp + "\r\n" +
                    "\r\n").getBytes());

            buffer.writeTo(stream);
            stream.write(("\r\n--" + boundary + "\r\n").getBytes());


            stream.flush();
        }
        catch (IOException e)
        {
            Log.i(TAG, "error :" + e.getMessage());
        }
    }












//    public class SendVideoThread implements Runnable{
//        public void run(){
//            // From Server.java
//            try {
//                if(SERVERIP!=null){
//
//                    serverSocket = new ServerSocket(SERVERPORT);
//                    while(true) {
//                        //listen for incoming clients
//                        Socket client = serverSocket.accept();
//
//                        try{
//                            // Begin video communication
//                            final ParcelFileDescriptor pfd = ParcelFileDescriptor.fromSocket(client);
//                            handler.post(new Runnable(){
//                                @Override
//                                public void run(){
//                                    recorder = new MediaRecorder();
//                                    recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
//                                    recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
//                                    recorder.setOutputFile(pfd.getFileDescriptor());
//                                    recorder.setVideoFrameRate(20);
//                                    recorder.setVideoSize(176,144);
//                                    recorder.setVideoEncoder(MediaRecorder.VideoEncoder.H263);
//                                    recorder.setPreviewDisplay(mHolder.getSurface());
//                                    try {
//                                        recorder.prepare();
//                                    } catch (IllegalStateException e) {
//                                        // TODO Auto-generated catch block
//                                        e.printStackTrace();
//                                    } catch (IOException e) {
//                                        // TODO Auto-generated catch block
//                                        e.printStackTrace();
//                                    }
//                                    recorder.start();
//                                }
//                            });
//                        } catch (Exception e) {
//                            handler.post(new Runnable(){
//                                @Override
//                                public void run(){
//                                    connectionStatus.setText("Oops.Connection interrupted. Please reconnect your phones.");
//                                }
//                            });
//                            e.printStackTrace();
//                        }
//                    }
//                } else {
//                    handler.post(new Runnable() {
//                        @Override
//                        public void run(){
//                            connectionStatus.setText("Couldn't detect internet connection.");
//                        }
//                    });
//                }
//            } catch (Exception e){
//                handler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        connectionStatus.setText("Error");
//                    }
//                });
//                e.printStackTrace();
//            }
//            // End from server.java
//        }


    public MainActivity getActivity() {
        return activity;
    }
}
