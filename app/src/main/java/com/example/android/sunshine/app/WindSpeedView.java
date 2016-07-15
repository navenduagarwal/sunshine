package com.example.android.sunshine.app;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by navendu on 7/15/2016.
 */
public class WindSpeedView extends View {
    private final static String LOG_TAG = WindSpeedView.class.getSimpleName();
    private final static int desiredWidth = 100; //default width of the image
    private final static int desiredHeight = 100; //default height of the image

    private Paint mWindmillPaint;
    private float mSpeed;

    //Images
    private Bitmap mRotor;
    private Bitmap mScaledRotor;
    private float mRotation;

    public WindSpeedView(Context context) {
        super(context);
        init(context);
    }

    public WindSpeedView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(context);
    }

    /**
     * initiating variables in separate method avoid instantiating anything in onDraw or onMeasure methods
     *
     * @param context
     */
    public void init(Context context) {
        mWindmillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mWindmillPaint.setDither(true);
        mWindmillPaint.setFilterBitmap(true);
        mWindmillPaint.setStyle(Paint.Style.FILL);

        //reading image file from resources
        mRotor = BitmapFactory.decodeResource(context.getResources(), R.drawable.rotor);
        //resizing image to desired size
        mScaledRotor = Bitmap.createScaledBitmap(mRotor, desiredWidth, desiredHeight, true);
        mRotation = 359f;
        mSpeed = 0f;
    }

    /**
     * controls bitmap rotation
     *
     * @param bitmap image file to be rotated
     * @param x
     * @param y
     * @return matrix
     */
    public Matrix rotate(Bitmap bitmap, int x, int y) {
        Matrix matrix = new Matrix();
        matrix.postRotate(mRotation, bitmap.getWidth() / 2, bitmap.getHeight() / 2);
        matrix.postTranslate(x, y); //The coordinates where we want to put our bitmap
        mRotation -= mSpeed;
        return matrix;
    }

    /**
     * defining canvas to be drawn
     *
     * @param canvas draw space
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //clear the canvas before redrawing
        canvas.drawColor(Color.TRANSPARENT);
        //Draw rotor
        int xCoord = 0;
        int yCoord = 0;
        canvas.drawBitmap(mScaledRotor, rotate(mScaledRotor, xCoord, yCoord), mWindmillPaint);
        invalidate();
    }

    /**
     * updating speed
     *
     * @param speed of rotation
     */
    public void setSpeed(float speed) {
        mSpeed = speed;
    }

    /**
     * Resizing Measure
     *
     * @param widthMeasureSpec  defined width spec in layout
     * @param heightMeasureSpec defined height spec in layout
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        //Get size requested and size mode
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(widthMeasureSpec);

        int mWidth;
        int mHeight;

        //Set Width Measure
        if (widthMode == MeasureSpec.EXACTLY) {
            //must be this size
            mWidth = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            //Can't be bigger then this size
            mWidth = Math.min(desiredWidth, widthSize);
        } else {
            //Be whatever you want
            mWidth = desiredWidth;
        }

        //Set Height Measure
        if (heightMode == MeasureSpec.EXACTLY) {
            //must be this size
            mHeight = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            //Can't be bigger then this size
            mHeight = Math.min(desiredHeight, heightSize);
        } else {
            //Be whatever you want
            mHeight = desiredHeight;
        }
        setMeasuredDimension(mWidth, mHeight);
    }

    /**
     * Updating view size based on passed parameters in layout and resized as per onMeasure
     *
     * @param w    updated width
     * @param h    updated height
     * @param oldw old width
     * @param oldh old height
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mScaledRotor = Bitmap.createScaledBitmap(mRotor, w, h, true);
    }
}
