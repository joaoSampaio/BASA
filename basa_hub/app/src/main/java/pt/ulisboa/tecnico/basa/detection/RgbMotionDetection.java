package pt.ulisboa.tecnico.basa.detection;

import android.graphics.Color;
import android.util.Log;

import pt.ulisboa.tecnico.basa.app.AppController;


//import android.util.Log;

/**
 * This class is used to process integer arrays containing RGB data and detects
 * motion.
 * 
 * @author Justin Wetherell <phishman3579@gmail.com>
 */
public class RgbMotionDetection implements IMotionDetection {


    // Specific settings
    private static final int mPixelThreshold = 50; // Difference in pixel (RGB)
    private static int mThreshold = 150; // Number of different pixels 176*144=25.000
                                                 // (RGB)

    private static int[] mPrevious = null;
    private static int mPreviousWidth = 0;
    private static int mPreviousHeight = 0;

    private int skipTop, skipBottom, skipLeft, skipRight;


    /**
     * {@inheritDoc}
     */
    @Override
    public int[] getPrevious() {
        return ((mPrevious != null) ? mPrevious.clone() : null);
    }

    protected static boolean isDifferent(int[] first, int width, int height) {
        if (first == null) throw new NullPointerException();

        if (mPrevious == null) return false;
        if (first.length != mPrevious.length) return true;
        if (mPreviousWidth != width || mPreviousHeight != height) return true;

        //comeca de cima para baixo
        int totDifferentPixels = 0;
        int i = 0, j=0, ij;
        for (ij = 0; i < height; i++) {
            for (j = 0; j < width; j++, ij++) {

                if(i < AppController.getInstance().skipTop && i > AppController.getInstance().skipBottom
                        && j > AppController.getInstance().skipLeft && j < AppController.getInstance().skipRight){
                    continue;
                }


                int pix = (0xff & (first[ij]));
                int otherPix = (0xff & (mPrevious[ij]));

                // Catch any pixels that are out of range
                if (pix < 0) pix = 0;
                if (pix > 255) pix = 255;
                if (otherPix < 0) otherPix = 0;
                if (otherPix > 255) otherPix = 255;

                if (Math.abs(pix - otherPix) >= mPixelThreshold) {
                    totDifferentPixels++;
                    // Paint different pixel red
                    first[ij] = Color.RED;
                }
            }
        }

        if (totDifferentPixels <= 0) totDifferentPixels = 1;
        boolean different = totDifferentPixels > mThreshold;

        return different;
    }

    /**
     * Detect motion comparing RGB pixel values. {@inheritDoc}
     */
    @Override
    public boolean detect(int[] rgb, int width, int height) {
        if (rgb == null) throw new NullPointerException();

        int[] original = rgb.clone();

        // Create the "mPrevious" picture, the one that will be used to check
        // the next frame against.
        if (mPrevious == null) {
            mPrevious = original;
            mPreviousWidth = width;
            mPreviousHeight = height;
            return false;
        }

        mThreshold = (int)((width*height) * AppController.getInstance().mThreshold);
        boolean motionDetected = isDifferent(rgb, width, height);
         Log.d("motion--", "mThreshold "+ mThreshold);

        // Replace the current image with the previous.
        mPrevious = original;
        mPreviousWidth = width;
        mPreviousHeight = height;

        return motionDetected;
    }
}
