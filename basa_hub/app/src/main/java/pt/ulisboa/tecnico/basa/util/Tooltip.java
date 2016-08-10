package pt.ulisboa.tecnico.basa.util;

import android.graphics.Rect;
import android.util.Log;
import android.view.View;

/**
 * Created by joao on 10-08-2016.
 */
public class Tooltip {

    public static void applyToolTipPosition(final View mView, final View tooltip) {
        final int[] masterViewScreenPosition = new int[2];
        mView.getLocationOnScreen(masterViewScreenPosition);

        final Rect viewDisplayFrame = new Rect();
        mView.getWindowVisibleDisplayFrame(viewDisplayFrame);

        final int[] parentViewScreenPosition = new int[2];
        (tooltip).getLocationOnScreen(parentViewScreenPosition);

        final int masterViewWidth = mView.getWidth();
        final int masterViewHeight = mView.getHeight();

        int mRelativeMasterViewX = masterViewScreenPosition[0] - parentViewScreenPosition[0];
        int mRelativeMasterViewY = masterViewScreenPosition[1] - parentViewScreenPosition[1];
        final int relativeMasterViewCenterX = mRelativeMasterViewX + masterViewWidth / 2;

        int toolTipViewAboveY = mRelativeMasterViewY - tooltip.getHeight();
        int toolTipViewBelowY = Math.max(0, mRelativeMasterViewY + masterViewHeight);


        Log.d("tool", "mView.masterViewScreenPosition[0]():"+masterViewScreenPosition[0]);
        Log.d("tool", "tooltip.getX():"+tooltip.getX());
        Log.d("tool", "mView.getHeight():"+mView.getHeight());
        Log.d("tool", "tooltip.getHeight():"+tooltip.getHeight());
        Log.d("tool", "tooltip.getWidth():"+tooltip.getWidth());
        tooltip.setX(masterViewScreenPosition[0] + mView.getWidth()/2 - tooltip.getWidth()/2);
        tooltip.setY(masterViewScreenPosition[1] - mView.getHeight()/2 - tooltip.getHeight()/2);
        tooltip.setVisibility(View.VISIBLE);
        Log.d("tool", "tooltip.getX() setX:"+tooltip.getX());
        Log.d("tool", "tooltip.getY() setY:"+tooltip.getY());

    }
}
