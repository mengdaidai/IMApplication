package com.daidai.im.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by songs on 2016/1/29.
 */
public class VolumnViewer extends View {
    private int mVolumeValue;
    private Paint mPaint;
    private boolean isFresh = true;
    public VolumnViewer(Context context) {
        super(context);
        init();
    }

    public VolumnViewer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public VolumnViewer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void init(){
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.WHITE);
        mVolumeValue = 0;
    }

    public void setmVolumeValue(int volume){
        this.mVolumeValue = volume;
    }

    public void stopRefresh(){
        isFresh = false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        final int height = getHeight();
        for(int i = 0;i<mVolumeValue;i++){
            int top = height-i*20;
            canvas.drawRect(0,top,10+5*i,top+12,mPaint);
        }

        if(isFresh){
            postInvalidateDelayed(10);
        }

    }


}
