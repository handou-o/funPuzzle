package com.ohand.funpuzzle;

import android.graphics.Bitmap;

/**
 * Created by ohand on 12/01/2018.
 */

public class MyBitMap {

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    private Bitmap bitmap;
    private int pos;

    MyBitMap(Bitmap bitmap, int pos){
        this.bitmap = bitmap;
        this.pos = pos;
    }
}
