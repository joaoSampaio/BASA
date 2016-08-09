package pt.ulisboa.tecnico.basa.util;

import android.content.Context;
import android.support.percent.PercentRelativeLayout;
import android.util.AttributeSet;

/**
 * Created by sampaio on 09-08-2016.
 */
public class SquarePercentRelativeLayout extends PercentRelativeLayout {
    public SquarePercentRelativeLayout(Context context) {
        super(context);
    }

    public SquarePercentRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquarePercentRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // Set a square layout.
        super.onMeasure(heightMeasureSpec, heightMeasureSpec);
    }



}
