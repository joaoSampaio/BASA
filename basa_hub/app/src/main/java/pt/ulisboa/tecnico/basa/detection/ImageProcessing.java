package pt.ulisboa.tecnico.basa.detection;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.util.Log;

import java.io.ByteArrayOutputStream;


/**
 * This abstract class is used to process images.
 * 
 * @author Justin Wetherell <phishman3579@gmail.com>
 */
public abstract class ImageProcessing {

    public static final int A = 0;
    public static final int R = 1;
    public static final int G = 2;
    public static final int B = 3;

    public static final int H = 0;
    public static final int S = 1;
    public static final int L = 2;

    private ImageProcessing() {
    }



    public static void encodeYUV420SP(byte[] yuv420sp, int[] argb, int width, int height) {
        final int frameSize = width * height;

        int yIndex = 0;
        int uvIndex = frameSize;

        int a, R, G, B, Y, U, V;
        int index = 0;
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {

                a = (argb[index] & 0xff000000) >> 24; // a is not used obviously
                R = (argb[index] & 0xff0000) >> 16;
                G = (argb[index] & 0xff00) >> 8;
                B = (argb[index] & 0xff) >> 0;

                // well known RGB to YUV algorithm
                Y = ( (  66 * R + 129 * G +  25 * B + 128) >> 8) +  16;
                U = ( ( -38 * R -  74 * G + 112 * B + 128) >> 8) + 128;
                V = ( ( 112 * R -  94 * G -  18 * B + 128) >> 8) + 128;

                // NV21 has a plane of Y and interleaved planes of VU each sampled by a factor of 2
                //    meaning for every 4 Y pixels there are 1 V and 1 U.  Note the sampling is every other
                //    pixel AND every other scanline.
                yuv420sp[yIndex++] = (byte) ((Y < 0) ? 0 : ((Y > 255) ? 255 : Y));
                if (j % 2 == 0 && index % 2 == 0) {
                    yuv420sp[uvIndex++] = (byte)((V<0) ? 0 : ((V > 255) ? 255 : V));
                    yuv420sp[uvIndex++] = (byte)((U<0) ? 0 : ((U > 255) ? 255 : U));
                }

                index ++;
            }
        }
    }


    public static int[] bitmapToRGB(Bitmap bmp){
        int[] rgbValues = new int[bmp.getWidth() * bmp.getHeight()];
        long t1 = System.currentTimeMillis();
        Log.d("camera", "bitmapToRGB ");


        bmp.getPixels(rgbValues, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());


//        int i = 0, j=0, ij;
//        for (ij = 0; i < bmp.getHeight(); i++) {
//            Log.d("camera", "bitmapToRGB height " + i + " ->took:  " + (System.currentTimeMillis() - t1));
//            for (j = 0; j < bmp.getWidth(); j++, ij++) {
//                rgbValues[ij] = bmp.getPixel(j, i);
//            }
//        }
        Log.d("camera", "bitmapToRGB took:  " + (System.currentTimeMillis() - t1));
        return rgbValues;
    }



    /**
     * Get RGB values from pixel.
     * 
     * @param pixel
     *            Integer representation of a pixel.
     * @return float array of a,r,g,b values.
     */
    public static float[] getARGB(int pixel) {
        int a = (pixel >> 24) & 0xff;
        int r = (pixel >> 16) & 0xff;
        int g = (pixel >> 8) & 0xff;
        int b = (pixel) & 0xff;
        return (new float[] { a, r, g, b });
    }

    /**
     * Get HSL (Hue, Saturation, Luma) from RGB. Note1: H is 0-360 (degrees)
     * Note2: S and L are 0-100 (percent)
     * 
     * @param r
     *            Red value.
     * @param g
     *            Green value.
     * @param b
     *            Blue value.
     * @return Integer array representing an HSL pixel.
     */
    public static int[] convertToHSL(int r, int g, int b) {
        float red = r / 255;
        float green = g / 255;
        float blue = b / 255;

        float minComponent = Math.min(red, Math.min(green, blue));
        float maxComponent = Math.max(red, Math.max(green, blue));
        float range = maxComponent - minComponent;
        float h = 0, s = 0, l = 0;

        l = (maxComponent + minComponent) / 2;

        if (range == 0) { // Monochrome image
            h = s = 0;
        } else {
            s = (l > 0.5) ? range / (2 - range) : range / (maxComponent + minComponent);

            if (red == maxComponent) {
                h = (blue - green) / range;
            } else if (green == maxComponent) {
                h = 2 + (blue - red) / range;
            } else if (blue == maxComponent) {
                h = 4 + (red - green) / range;
            }
        }

        // convert to 0-360 (degrees)
        h *= 60;
        if (h < 0) h += 360;

        // convert to 0-100 (percent)
        s *= 100;
        l *= 100;

        // Since they were converted from float to int
        return (new int[] { (int) h, (int) s, (int) l });
    }

    /**
     * Decode a YUV420SP image to Luma.
     * 
     * @param yuv420sp
     *            Byte array representing a YUV420SP image.
     * @param width
     *            Width of the image.
     * @param height
     *            Height of the image.
     * @return Integer array representing the Luma image.
     * @throws NullPointerException
     *             if yuv420sp byte array is NULL.
     */
    public static int[] decodeYUV420SPtoLuma(byte[] yuv420sp, int width, int height) {
        if (yuv420sp == null) throw new NullPointerException();

        final int frameSize = width * height;
        int[] hsl = new int[frameSize];

        for (int j = 0, yp = 0; j < height; j++) {
            for (int i = 0; i < width; i++, yp++) {
                int y = (0xff & (yuv420sp[yp])) - 16;
                if (y < 0) y = 0;
                hsl[yp] = y;
            }
        }
        return hsl;
    }

    /**
     * Decode a YUV420SP image to RGB.
     * 
     * @param yuv420sp
     *            Byte array representing a YUV420SP image.
     * @param width
     *            Width of the image.
     * @param height
     *            Height of the image.
     * @return Integer array representing the RGB image.
     * @throws NullPointerException
     *             if yuv420sp byte array is NULL.
     */
    public static int[] decodeYUV420SPtoRGB(byte[] yuv420sp, int width, int height) {
        if (yuv420sp == null) throw new NullPointerException();

        final int frameSize = width * height;
        int[] rgb = new int[frameSize];

        for (int j = 0, yp = 0; j < height; j++) {
            int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;
            for (int i = 0; i < width; i++, yp++) {
                int y = (0xff & (yuv420sp[yp])) - 16;
                if (y < 0) y = 0;
                if ((i & 1) == 0) {
                    v = (0xff & yuv420sp[uvp++]) - 128;
                    u = (0xff & yuv420sp[uvp++]) - 128;
                }
                int y1192 = 1192 * y;
                int r = (y1192 + 1634 * v);
                int g = (y1192 - 833 * v - 400 * u);
                int b = (y1192 + 2066 * u);

                if (r < 0) r = 0;
                else if (r > 262143) r = 262143;
                if (g < 0) g = 0;
                else if (g > 262143) g = 262143;
                if (b < 0) b = 0;
                else if (b > 262143) b = 262143;

                rgb[yp] = 0xff000000 | ((r << 6) & 0xff0000) | ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);
            }
        }
        return rgb;
    }

    /**
     * Convert an RGB image into a Bitmap.
     * 
     * @param rgb
     *            Integer array representing an RGB image.
     * @param width
     *            Width of the image.
     * @param height
     *            Height of the image.
     * @return Bitmap of the RGB image.
     * @throws NullPointerException
     *             if RGB integer array is NULL.
     */
    public static Bitmap rgbToBitmap(int[] rgb, int width, int height) {
        if (rgb == null) throw new NullPointerException();

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        bitmap.setPixels(rgb, 0, width, 0, 0, width, height);
        return bitmap;
    }

    /**
     * Convert an Luma image into Greyscale.
     * 
     * @param lum
     *            Integer array representing an Luma image.
     * @param width
     *            Width of the image.
     * @param height
     *            Height of the image.
     * @return Bitmap of the Luma image.
     * @throws NullPointerException
     *             if RGB integer array is NULL.
     */
    public static Bitmap lumaToGreyscale(int[] lum, int width, int height) {
        if (lum == null) throw new NullPointerException();

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        for (int y = 0, xy = 0; y < bitmap.getHeight(); y++) {
            for (int x = 0; x < bitmap.getWidth(); x++, xy++) {
                int luma = lum[xy];
                bitmap.setPixel(x, y, Color.argb(1, luma, luma, luma));
            }
        }
        return bitmap;
    }

    /**
     * Rotate the given Bitmap by the given degrees.
     * 
     * @param bmp
     *            Bitmap to rotate.
     * @param degrees
     *            Degrees to rotate.
     * @return Bitmap which was rotated.
     */
    public static Bitmap rotate(Bitmap bmp, int degrees) {
        if (bmp == null) throw new NullPointerException();

        // getting scales of the image
        int width = bmp.getWidth();
        int height = bmp.getHeight();

        // Creating a Matrix and rotating it to 90 degrees
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);

        // Getting the rotated Bitmap
        Bitmap rotatedBmp = Bitmap.createBitmap(bmp, 0, 0, width, height, matrix, true);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        rotatedBmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return rotatedBmp;
    }

    /**
     * Rotate the given image in byte array format by the given degrees.
     * 
     * @param data
     *            Bitmap to rotate in byte array form.
     * @param degrees
     *            Degrees to rotate.
     * @return Byte array format of an image which was rotated.
     */
    public static byte[] rotate(byte[] data, int degrees) {
        if (data == null) throw new NullPointerException();

        // Convert the byte data into a Bitmap
        Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);

        // Getting the rotated Bitmap
        Bitmap rotatedBmp = rotate(bmp, degrees);

        // Get the byte array from the Bitmap
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        rotatedBmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }
}
