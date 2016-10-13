package pt.ulisboa.tecnico.basa.camera;

import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import pt.ulisboa.tecnico.basa.app.AppController;

/**
 * Created by Sampaio on 31/08/2016.
 */
public class SavePhotoThread extends Thread {

    private Bitmap bmp;
    private PhotoSaved listener;
    private String path;

    public SavePhotoThread(String path, Bitmap bmp, PhotoSaved listener) {
        this.bmp = bmp;
        this.listener = listener;
        this.path = path;
    }

    @Override
    public void run() {
        File f = new File(path);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(f);
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }
        bmp.compress(Bitmap.CompressFormat.JPEG, 70, fos);
        try {
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        bmp.recycle();
        MediaScannerConnection.scanFile(AppController.getAppContext(), new String[] { f.toString() }, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Log.i("ExternalStorage", "Scanned " + path + ":");
                        Log.i("ExternalStorage", "-> uri=" + uri);
                    }
                });


        if(listener != null)
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    listener.onPhotoBeenSaved(Uri.fromFile(new File(path)));
                }
            });

    }


    public interface PhotoSaved{
        void onPhotoBeenSaved(Uri file);
    }

}
