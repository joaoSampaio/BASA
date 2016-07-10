/*******************************************************************************
 * The MIT License (MIT)
 * 
 * Copyright (c) 2013 Triggertrap Ltd
 * Author Neil Davies
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 ******************************************************************************/
package pt.ulisboa.tecnico.mybasaclient.util;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import pt.ulisboa.tecnico.mybasaclient.Global;
import pt.ulisboa.tecnico.mybasaclient.R;


/**
 * 
 * SeekArc.java
 * 
 * This is a class that functions much like a SeekBar but
 * follows a circle path instead of a straight line.
 * 
 * @author Neil Davies
 * 
 */
public class SeekArc extends View {

	private static final String TAG = SeekArc.class.getSimpleName();
	private static int INVALID_PROGRESS_VALUE = -1;
	// The initial rotational offset -90 means we start at 12 o'clock
	private final int mAngleOffset = -90;

	private int colorBackground = Global.COLOR_COLD;

	/**
	 * The Drawable for the seek arc thumbnail
	 */
	private Drawable mThumb;
	
	/**
	 * The Maximum value that this SeekArc can be set to
	 */
	private int mMax = 100;

	/**
	 * The Minimum value that this SeekArc can be set to
	 */
	private int mMin = 0;
	
	/**
	 * The Current value that the SeekArc is set to
	 */
	private int mProgress = 0;
		
	/**
	 * The width of the progress line for this SeekArc
	 */
	private int mProgressWidth = 4;
	
	/**
	 * The Width of the background arc for the SeekArc 
	 */
	private int mArcWidth = 2;
	
	/**
	 * The Angle to start drawing this Arc from
	 */
	private int mStartAngle = 0;
	
	/**
	 * The Angle through which to draw the arc (Max is 360)
	 */
	private int mSweepAngle = 360;
	
	/**
	 * The rotation of the SeekArc- 0 is twelve o'clock
	 */
	private int mRotation = 0;
	
	/**
	 * Give the SeekArc rounded edges
	 */
	private boolean mRoundedEdges = false;
	
	/**
	 * Enable touch inside the SeekArc
	 */
	private boolean mTouchInside = true;
	
	/**
	 * Will the progress increase clockwise or anti-clockwise
	 */
	private boolean mClockwise = true;


	/**
	 * Current temperature
	 */
	private String currentTemperature = "";

	/**
	 * is the control enabled/touchable
 	 */
	private boolean mEnabled = true;

	// Internal variables
	private int mArcRadius = 0;
	private float mProgressSweep = 0;
	private RectF mArcRect = new RectF();
	private Paint mArcPaint;
	private Paint mArcPaintBackground;
	private Paint mProgressPaint;
	private int mTranslateX;
	private int mTranslateY;
	private int mThumbXPos;
	private int mThumbYPos;
	private int mTextXPos;
	private int mTextYPos;
	private double mTouchAngle;
	private Paint paintText;
	private TextPaint paintTextCurrent, paintLabel;
	private int mSelectedTemperature = 0;
	private float mTouchIgnoreRadius;
	private OnSeekArcChangeListener mOnSeekArcChangeListener;
	private float density;
	private Bitmap arrowUp, arrowDown;
	private Context context;
	private int smallest;
//	private

	public interface OnSeekArcChangeListener {

		/**
		 * Notification that the progress level has changed. Clients can use the
		 * fromUser parameter to distinguish user-initiated changes from those
		 * that occurred programmatically.
		 * 
		 * @param seekArc
		 *            The SeekArc whose progress has changed
		 * @param progress
		 *            The current progress level. This will be in the range
		 *            0..max where max was set by
		 *            . (The default value for
		 *            max is 100.)
		 * @param fromUser
		 *            True if the progress change was initiated by the user.
		 */
		void onProgressChanged(SeekArc seekArc, int progress, boolean fromUser);

		/**
		 * Notification that the user has started a touch gesture. Clients may
		 * want to use this to disable advancing the seekbar.
		 * 
		 * @param seekArc
		 *            The SeekArc in which the touch gesture began
		 */
		void onStartTrackingTouch(SeekArc seekArc);

		/**
		 * Notification that the user has finished a touch gesture. Clients may
		 * want to use this to re-enable advancing the seekarc.
		 * 
		 * @param seekArc
		 *            The SeekArc in which the touch gesture began
		 */
		void onStopTrackingTouch(SeekArc seekArc);
	}

	public SeekArc(Context context) {
		super(context);
		init(context, null, 0);
	}

	public SeekArc(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		init(context, attrs, R.attr.seekArcStyle);
	}

	public SeekArc(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs, defStyle);
	}

	private void init(Context context, AttributeSet attrs, int defStyle) {

		Log.d(TAG, "Initialising SeekArc");
		final Resources res = getResources();
		density = context.getResources().getDisplayMetrics().density;

		// Defaults, may need to link this into theme settings
		int arcColor = res.getColor(R.color.progress_gray);
		int progressColor = res.getColor(R.color.default_blue_light);
		int thumbHalfheight = 0;
		int thumbHalfWidth = 0;
		mThumb = res.getDrawable(R.drawable.seek_arc_control_selector);
		// Convert progress width to pixels for current density
		mProgressWidth = (int) (mProgressWidth * density);



		if (attrs != null) {
			// Attribute initialization
			final TypedArray a = context.obtainStyledAttributes(attrs,
					R.styleable.SeekArc, defStyle, 0);

			Drawable thumb = a.getDrawable(R.styleable.SeekArc_thumb);
			if (thumb != null) {
				mThumb = thumb;
			}

			paintText = new Paint(Paint.ANTI_ALIAS_FLAG);
			// text color - #3D3D3D
			paintText.setColor(Color.parseColor("#FF383838"));
			// text size in pixels
			paintText.setTextSize((int) (22 * density));

			paintText.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

			paintText.setShadowLayer(1f, 0f, 1f, Color.WHITE);

			mMax = a.getInteger(R.styleable.SeekArc_max, mMax);
			mMin= a.getInteger(R.styleable.SeekArc_min, mMin);
			mMax = mMax-mMin;
			mProgress = a.getInteger(R.styleable.SeekArc_progress, mProgress);
			mProgressWidth = (int) a.getDimension(
					R.styleable.SeekArc_progressWidth, mProgressWidth);
			mArcWidth = (int) a.getDimension(R.styleable.SeekArc_arcWidth,
					mArcWidth);
			mStartAngle = a.getInt(R.styleable.SeekArc_startAngle, mStartAngle);
			mSweepAngle = a.getInt(R.styleable.SeekArc_sweepAngle, mSweepAngle);
			mRotation = a.getInt(R.styleable.SeekArc_rotation, mRotation);
			mRoundedEdges = a.getBoolean(R.styleable.SeekArc_roundEdges,
					mRoundedEdges);
			mTouchInside = a.getBoolean(R.styleable.SeekArc_touchInside,
					mTouchInside);
			mClockwise = a.getBoolean(R.styleable.SeekArc_clockwise,
					mClockwise);

			arcColor = a.getColor(R.styleable.SeekArc_arcColor, arcColor);
			progressColor = a.getColor(R.styleable.SeekArc_progressColor,
					progressColor);

			a.recycle();
		}

		mProgress = (mProgress > mMax) ? mMax : mProgress;
		mProgress = (mProgress < 0) ? 0 : mProgress;

		mSweepAngle = (mSweepAngle > 360) ? 360 : mSweepAngle;
		mSweepAngle = (mSweepAngle < 0) ? 0 : mSweepAngle;

		mProgressSweep = (float) mProgress / mMax * mSweepAngle;

		mStartAngle = (mStartAngle > 360) ? 0 : mStartAngle;
		mStartAngle = (mStartAngle < 0) ? 0 : mStartAngle;

		mArcPaintBackground = new Paint();
		mArcPaintBackground.setColor(colorBackground);
		mArcPaintBackground.setAntiAlias(true);
		mArcPaintBackground.setStyle(Paint.Style.FILL);

		mArcPaint = new Paint();
		mArcPaint.setColor(arcColor);
		mArcPaint.setAntiAlias(true);
		mArcPaint.setStyle(Paint.Style.STROKE);
		mArcPaint.setStrokeWidth(mArcWidth);

		mProgressPaint = new Paint();
		mProgressPaint.setColor(progressColor);
		mProgressPaint.setAntiAlias(true);
		mProgressPaint.setStyle(Paint.Style.STROKE);
		mProgressPaint.setStrokeWidth(mProgressWidth);

		if (mRoundedEdges) {
			mArcPaint.setStrokeCap(Paint.Cap.ROUND);
			mProgressPaint.setStrokeCap(Paint.Cap.ROUND);
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {		
		if(!mClockwise) {
			canvas.scale(-1, 1, mArcRect.centerX(), mArcRect.centerY() );
		}

		smallest = (getWidth() > getHeight())? getHeight() : getWidth();

		if(paintTextCurrent == null){
			int thumbHalfheight = (int) mThumb.getIntrinsicHeight() / 2;
			int thumbHalfWidth = (int) mThumb.getIntrinsicWidth() / 2;

			thumbHalfheight = (int)(thumbHalfheight * ((smallest/density)/500));
			thumbHalfWidth = (int)(thumbHalfWidth * ((smallest/density)/500));
			mThumb.setBounds(-thumbHalfWidth, -thumbHalfheight, thumbHalfWidth,
					thumbHalfheight);
		}


		if(paintTextCurrent == null){
			paintTextCurrent = new TextPaint();
			paintTextCurrent.setColor(Color.WHITE);
			paintTextCurrent.setTextAlign(Paint.Align.CENTER);
			paintTextCurrent.setTextSize(getScaledSize(110));


		}
		if(paintLabel == null) {
			paintLabel = new TextPaint();
			paintLabel.setColor(context.getResources().getColor(R.color.grey));
			paintLabel.setTextAlign(Paint.Align.CENTER);
			paintLabel.setTextSize(getScaledSize(25));

		}

		// Draw the arcs
		final int arcStart = mStartAngle + mAngleOffset + mRotation;
		final int arcSweep = mSweepAngle;

		if (mProgress == 0) {

			canvas.drawArc(mArcRect, 0, 360, false, mArcPaintBackground);

			canvas.drawArc(mArcRect, arcStart, arcSweep, false, mArcPaint);

		} else {

			canvas.drawArc(mArcRect, 0, 360, false, mArcPaintBackground);
			canvas.drawArc(mArcRect, arcStart, arcSweep, false, mArcPaint);

			float bias = (float) mProgressSweep / (float) (arcSweep - 1);
			int mColor1 =  Color.parseColor("#ff33b5e5");
			int mColor2 =  Color.parseColor("#ffe2231a");
			int color = interpolateColor(mColor1, mColor2, bias);
			mProgressPaint.setColor(color);


			canvas.drawArc(mArcRect, arcStart, mProgressSweep, false, mProgressPaint);

		}
		canvas.drawText(""+(mSelectedTemperature+mMin), mTranslateX - mTextXPos  ,  mTranslateY - mTextYPos  , paintText);


		RectF bounds = new RectF(0, 0, getWidth(), getHeight());
		float textHeight = paintTextCurrent.descent() - paintTextCurrent.ascent();
		float textOffset = (textHeight / 2) - paintTextCurrent.descent();
		canvas.drawText(""+currentTemperature, bounds.centerX(), bounds.centerY() + textOffset, paintTextCurrent);



		float textHeightLabel = paintLabel.descent() - paintLabel.ascent();
		float textOffsetLabel = (textHeightLabel / 2) - paintLabel.descent();
		canvas.drawText("Currrent", bounds.centerX(), bounds.centerY()  - textHeightLabel - getScaledSize(50), paintLabel);


//		int centreX = ((int)(bounds.width()  - arrowUp.getWidth()) /2);
//		Paint paint = new Paint();
//		paint.setAntiAlias(true);
//		paint.setFilterBitmap(true);
//		paint.setDither(true);
//		canvas.drawBitmap(arrowUp , centreX, 0 + getScaledSize(20), paint);
//		centreX = ((int)(bounds.width()  - arrowDown.getWidth()) /2);
//		canvas.drawBitmap(arrowDown , centreX, bounds.centerY() - (getScaledSize(20)), paint);


		canvas.translate(mTranslateX - mThumbXPos, mTranslateY - mThumbYPos);


		canvas.save(Canvas.MATRIX_SAVE_FLAG); //Saving the canvas and later restoring it so only this image will be rotated.
		canvas.rotate(mProgressSweep+mStartAngle);
		mThumb.draw(canvas);
		canvas.restore();

	}

	private int getScaledSize(int value){
		return (int)(density * value * ((smallest/density)/500));
	}

	private int interpolateColor(int colorA, int colorB, float bias) {
		float[] hsvColorA = new float[3];
		Color.colorToHSV(colorA, hsvColorA);

		float[] hsvColorB = new float[3];
		Color.colorToHSV(colorB, hsvColorB);

		hsvColorB[0] = interpolate(hsvColorA[0], hsvColorB[0], bias);
		hsvColorB[1] = interpolate(hsvColorA[1], hsvColorB[1], bias);
		hsvColorB[2] = interpolate(hsvColorA[2], hsvColorB[2], bias);

		// NOTE For some reason the method HSVToColor fail in edit mode. Just use the start color for now
		if (isInEditMode())
			return colorA;

		return Color.HSVToColor(hsvColorB);
	}

	private float interpolate(float a, float b, float bias) {
		return (a + ((b - a) * bias));
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		final int height = getDefaultSize(getSuggestedMinimumHeight(),
				heightMeasureSpec);
		final int width = getDefaultSize(getSuggestedMinimumWidth(),
				widthMeasureSpec);
		final int min = Math.min(width, height);
		float top = 0;
		float left = 0;
		int arcDiameter = 0;

		mTranslateX = (int) (width * 0.5f);
		mTranslateY = (int) (height * 0.5f);
		
		arcDiameter = min - getPaddingLeft();
		mArcRadius = arcDiameter / 2;
		top = height / 2 - (arcDiameter / 2);
		left = width / 2 - (arcDiameter / 2);
		mArcRect.set(left, top, left + arcDiameter, top + arcDiameter);
	
		int arcStart = (int)mProgressSweep + mStartAngle  + mRotation + 90;
		mThumbXPos = (int) (mArcRadius * Math.cos(Math.toRadians(arcStart)));
		mThumbYPos = (int) (mArcRadius * Math.sin(Math.toRadians(arcStart)));

		mTextXPos = (int) (mArcRadius * Math.cos(Math.toRadians(arcStart+10)));
		mTextYPos = (int) (mArcRadius * Math.sin(Math.toRadians(arcStart+10)));

		
		setTouchInSide(mTouchInside);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (mEnabled) {
			this.getParent().requestDisallowInterceptTouchEvent(true);

			switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					onStartTrackingTouch();
					updateOnTouch(event);
					break;
				case MotionEvent.ACTION_MOVE:
					updateOnTouch(event);
					break;
				case MotionEvent.ACTION_UP:
					onStopTrackingTouch();
					setPressed(false);
					this.getParent().requestDisallowInterceptTouchEvent(false);
					break;
				case MotionEvent.ACTION_CANCEL:
					onStopTrackingTouch();
					setPressed(false);
					this.getParent().requestDisallowInterceptTouchEvent(false);
					break;
			}
			return true;
		}
		return false;
	}

	@Override
	protected void drawableStateChanged() {
		super.drawableStateChanged();
		if (mThumb != null && mThumb.isStateful()) {
			int[] state = getDrawableState();
			mThumb.setState(state);
		}
		invalidate();
	}

	private void onStartTrackingTouch() {
		if (mOnSeekArcChangeListener != null) {
			mOnSeekArcChangeListener.onStartTrackingTouch(this);
		}
	}

	private void onStopTrackingTouch() {
		if (mOnSeekArcChangeListener != null) {
			mOnSeekArcChangeListener.onStopTrackingTouch(this);
		}
	}

	private void updateOnTouch(MotionEvent event) {
		boolean ignoreTouch = ignoreTouch(event.getX(), event.getY());
		if (ignoreTouch) {
			return;
		}
		setPressed(true);
		mTouchAngle = getTouchDegrees(event.getX(), event.getY());
		int progress = getProgressForAngle(mTouchAngle);
		onProgressRefresh(progress, true);
	}

	private boolean ignoreTouch(float xPos, float yPos) {
		boolean ignore = false;
		float x = xPos - mTranslateX;
		float y = yPos - mTranslateY;

		float touchRadius = (float) Math.sqrt(((x * x) + (y * y)));
		if (touchRadius < mTouchIgnoreRadius) {
			ignore = true;
		}
		return ignore;
	}

	private double getTouchDegrees(float xPos, float yPos) {
		float x = xPos - mTranslateX;
		float y = yPos - mTranslateY;
		//invert the x-coord if we are rotating anti-clockwise
		x= (mClockwise) ? x:-x;
		// convert to arc Angle
		double angle = Math.toDegrees(Math.atan2(y, x) + (Math.PI / 2)
				- Math.toRadians(mRotation));
		if (angle < 0) {
			angle = 360 + angle;
		}
		angle -= mStartAngle;
		return angle;
	}

	private int getProgressForAngle(double angle) {
		int touchProgress = (int) Math.round(valuePerDegree() * angle);

		touchProgress = (touchProgress < 0) ? INVALID_PROGRESS_VALUE
				: touchProgress;
		touchProgress = (touchProgress > mMax) ? INVALID_PROGRESS_VALUE
				: touchProgress;
		return touchProgress;
	}

	private float valuePerDegree() {
		return (float) mMax / mSweepAngle;
	}

	private void onProgressRefresh(int progress, boolean fromUser) {
		updateProgress(progress, fromUser);
	}

	private void updateThumbPosition() {
		int thumbAngle = (int) (mStartAngle + mProgressSweep + mRotation + 90);
		mThumbXPos = (int) (mArcRadius * Math.cos(Math.toRadians(thumbAngle)));
		mThumbYPos = (int) (mArcRadius * Math.sin(Math.toRadians(thumbAngle)));
		mTextXPos = (int) (mArcRadius * Math.cos(Math.toRadians(thumbAngle+10)));
		mTextYPos = (int) (mArcRadius * Math.sin(Math.toRadians(thumbAngle+10)));
	}
	
	private void updateProgress(int progress, boolean fromUser) {

		if (progress == INVALID_PROGRESS_VALUE) {
			return;
		}

		progress = (progress > mMax) ? mMax : progress;
		progress = (progress < 0) ? 0 : progress;
		mProgress = progress;

		if (mOnSeekArcChangeListener != null) {
			mOnSeekArcChangeListener
					.onProgressChanged(this, progress+mMin, fromUser);
		}

		mProgressSweep = (float) progress / mMax * mSweepAngle;

		updateThumbPosition();
		mSelectedTemperature = progress;
		invalidate();
	}

	public void setBackgroundColor(int color){
		colorBackground = color;
		mArcPaintBackground.setColor(color);
		invalidate();
	}

	public void setCurrentTemperature(String temperature){

		currentTemperature = temperature;

		invalidate();
	}



	/**
	 * Sets a listener to receive notifications of changes to the SeekArc's
	 * progress level. Also provides notifications of when the user starts and
	 * stops a touch gesture within the SeekArc.
	 * 
	 * @param l
	 *            The seek bar notification listener
	 * 
	 */
	public void setOnSeekArcChangeListener(OnSeekArcChangeListener l) {
		mOnSeekArcChangeListener = l;
	}

	public void setProgress(int progress) {
		updateProgress(progress-mMin, false);
	}

	public int getProgress() {
		return mProgress+ mMin;
	}

	public int getProgressWidth() {
		return mProgressWidth;
	}

	public void setProgressWidth(int mProgressWidth) {
		this.mProgressWidth = mProgressWidth;
		mProgressPaint.setStrokeWidth(mProgressWidth);
	}
	
	public int getArcWidth() {
		return mArcWidth;
	}

	public void setArcWidth(int mArcWidth) {
		this.mArcWidth = mArcWidth;
		mArcPaint.setStrokeWidth(mArcWidth);
	}
	public int getArcRotation() {
		return mRotation;
	}

	public void setArcRotation(int mRotation) {
		this.mRotation = mRotation;
		updateThumbPosition();
	}

	public int getStartAngle() {
		return mStartAngle;
	}

	public void setStartAngle(int mStartAngle) {
		this.mStartAngle = mStartAngle;
		updateThumbPosition();
	}

	public int getSweepAngle() {
		return mSweepAngle;
	}

	public void setSweepAngle(int mSweepAngle) {
		this.mSweepAngle = mSweepAngle;
		updateThumbPosition();
	}
	
	public void setRoundedEdges(boolean isEnabled) {
		mRoundedEdges = isEnabled;
		if (mRoundedEdges) {
			mArcPaint.setStrokeCap(Paint.Cap.ROUND);
			mProgressPaint.setStrokeCap(Paint.Cap.ROUND);
		} else {
			mArcPaint.setStrokeCap(Paint.Cap.SQUARE);
			mProgressPaint.setStrokeCap(Paint.Cap.SQUARE);
		}
	}
	
	public void setTouchInSide(boolean isEnabled) {
		int thumbHalfheight = (int) mThumb.getIntrinsicHeight() / 2;
		int thumbHalfWidth = (int) mThumb.getIntrinsicWidth() / 2;
		mTouchInside = isEnabled;
		if (mTouchInside) {
			mTouchIgnoreRadius = (float) mArcRadius / 4;
		} else {
			// Don't use the exact radius makes interaction too tricky
			mTouchIgnoreRadius = mArcRadius
					- Math.min(thumbHalfWidth, thumbHalfheight);
		}
	}
	
	public void setClockwise(boolean isClockwise) {
		mClockwise = isClockwise;
	}

	public boolean isClockwise() {
		return mClockwise;
	}

	public boolean isEnabled() {
		return mEnabled;
	}

	public void setEnabled(boolean enabled) {
		this.mEnabled = enabled;
	}

	public int getProgressColor() {
		return mProgressPaint.getColor();
	}

	public void setProgressColor(int color) {
		mProgressPaint.setColor(color);
		invalidate();
	}

	public int getArcColor() {
		return mArcPaint.getColor();
	}

	public void setArcColor(int color) {
		mArcPaint.setColor(color);
		invalidate();
	}

	public int getMax() {
		return mMax+mMin;
	}

	public void setMax(int mMax) {
		this.mMax = mMax-mMin;
	}

	public int getMin() {
		return mMin;
	}

	public void setMin(int mMin) {
		this.mMin = mMin;
	}
}
