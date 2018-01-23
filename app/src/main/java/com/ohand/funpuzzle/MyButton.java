package com.ohand.funpuzzle;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * Created by ohand on 12/01/2018.
 */

public class MyButton extends android.support.v7.widget.AppCompatButton {



    private MyBitMap myBitMap;

    private boolean free;
    public int getPos() {
        return pos;
    }

    public boolean isFree(){
        return free;
    }
    public void setFree(boolean f){
        this.free = f;
    }
    public void setPos(int pos) {
        this.pos = pos;
    }

    private int pos;
    public MyButton(Context context) {
        super(context);
        init(null,-1);
    }
    public MyButton(Context context,MyBitMap myBitMap,int pos) {
            super(context);
        init(myBitMap,pos);
    }

    public MyButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init(MyBitMap myBitMap,int pos){
    this.myBitMap = myBitMap;
    this.pos = pos;
    this.paint = new Paint();
    this.free = false;
    }
    Paint paint = null;
    @Override
    protected void onDraw(Canvas canvas) {
        if (myBitMap != null && free == false) {
            //Drawable d = new BitmapDrawable(getResources(), myBitMap.getBitmap());
            //setBackground(d);
            canvas.drawBitmap( myBitMap.getBitmap(),0.0f,0.0f, null);

            paint.setColor( Color.GREEN );
            if (pos != myBitMap.getPos()){
                paint.setColor( Color.RED );
            }
            paint.setStrokeWidth( 1.5f );
            paint.setStyle( Paint.Style.STROKE );
            canvas.drawRect(0, 0, getWidth(), getHeight(), paint);
        }
        else {
            setBackgroundColor(Color.WHITE);
        }

        super.onDraw(canvas);


    }


    public MyBitMap getMyBitMap() {
        return myBitMap;
    }

    public void setMyBitMap(MyBitMap myBitMap) {
        this.myBitMap = myBitMap;
    }
}
