package pt.ulisboa.tecnico.mybasaclient.camera;

import android.hardware.Camera;

/**
 * Created by Sampaio on 14/05/2016.
 */
public interface CameraInterface {

    void takePhoto(Camera.PictureCallback mPicture, CallbackCameraAction callback);
    void enableQRCode(boolean enable);

}
