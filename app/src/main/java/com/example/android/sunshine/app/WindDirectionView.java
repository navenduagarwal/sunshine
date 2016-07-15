package com.example.android.sunshine.app;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by navendu on 7/15/2016.
 */
public class WindDirectionView extends View {
    private final static String LOG_TAG = WindDirectionView.class.getSimpleName();
    private final static int desiredWidth = 100; //default width of the image
    private final static int desiredHeight = 100; //default height of the image
    private float mWindDirection;
    private Paint mArrowPaint;
    private Bitmap mArrow;
    private Bitmap mArrowScaled;

    public WindDirectionView(Context context) {
        super(context);
        init(context);
    }

    public WindDirectionView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(context);
    }

    public void init(Context context) {
        mArrowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mArrowPaint.setDither(true);
        mArrowPaint.setFilterBitmap(true);
        mArrowPaint.setStyle(Paint.Style.FILL);

        //reading image file from resources
        mArrow = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_arrow_upward_black_24dp);
        //resizing image to desired size
        mArrowScaled = Bitmap.createScaledBitmap(mArrow, desiredWidth, desiredHeight, true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //clear the canvas before redrawing
        canvas.drawColor(Color.TRANSPARENT);
        Log.i(LOG_TAG, "Direction:" + mWindDirection);
        canvas.save(Canvas.MATRIX_SAVE_FLAG);
        canvas.rotate(mWindDirection, canvas.getWidth() / 2, canvas.getHeight() / 2);
        canvas.drawBitmap(mArrowScaled, 0, 0, mArrowPaint);
    }

    public void setWindDirection(float direction) {
        mWindDirection = direction;
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
        mArrowScaled = Bitmap.createScaledBitmap(mArrow, w, h, true);
    }
}
