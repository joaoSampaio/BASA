package pt.ulisboa.tecnico.basa.util;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Build;
import android.util.Log;
import android.view.View;

/**
 * Created by Sampaio on 07/08/2016.
 */
public class ColorHelper {

    public static void changeBackgroundColor(View v, int color){
        Drawable background = v.getBackground();
        if (background instanceof ShapeDrawable) {
            ((ShapeDrawable)background).getPaint().setColor(color);
        } else if (background instanceof GradientDrawable) {
            ((GradientDrawable)background).setColor(color);
        } else if (background instanceof ColorDrawable) {
            ((ColorDrawable)background).setColor(color);
        } else if (background instanceof RippleDrawable) {
            Log.d("color", "RippleDrawable:"+ ((RippleDrawable)background).getNumberOfLayers());

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ((RippleDrawable)background).getDrawable(0).setTint(color);
            }


        }
    }

}
