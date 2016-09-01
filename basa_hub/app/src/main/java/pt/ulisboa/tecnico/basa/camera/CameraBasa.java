package pt.ulisboa.tecnico.basa.camera;

import pt.ulisboa.tecnico.basa.util.BitmapMotionTransfer;

/**
 * Created by Sampaio on 27/08/2016.
 */
public interface CameraBasa {
    void destroy();
    void start_camera();
    void addImageListener(BitmapMotionTransfer bitmapMotion );
    void removeImageListener(BitmapMotionTransfer bitmapMotion );
}
