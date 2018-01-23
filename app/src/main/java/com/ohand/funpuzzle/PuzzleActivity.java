package com.ohand.funpuzzle;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by ohand on 12/01/2018.
 */

public class PuzzleActivity extends AppCompatActivity {

    public final static int REQUESTCODE_PICK_IMAGE = 25;
    private final static int nbPerLine = 5;
    private Button loadImage = null;

    private enum Free {RIGHT, LEFT, DOWN, UP};

    private Free free = null;
    private ConstraintLayout layout = null;
    private int widthPixels = 0;
    private int heightPixels = 0;
    private MyButton customButton = null;
    private MyButton buttonSelected = null;
    private MyButton freeButton = null;
    private MyButton removedButton = null;
    private Button reOrder = null;
    private List<MyButton> puzzle;
    Map<Integer, MyBitMap> currentPuzzleSort;

    int widthButton = 0;
    int heightButton = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle);
        layout = findViewById(R.id.PuzzleLayout);
        loadImage = findViewById(R.id.loadButton);
        reOrder = findViewById(R.id.ReOrder);
        reOrder.setOnTouchListener(reOrderOnClick);
        loadImage.setOnClickListener(loadOnClick);
        int display_mode = getResources().getConfiguration().orientation;
        if (display_mode == Configuration.ORIENTATION_PORTRAIT) {
            widthPixels = (int) (Resources.getSystem().getDisplayMetrics().widthPixels * 0.95);
            heightPixels = (int) (Resources.getSystem().getDisplayMetrics().heightPixels * 0.9);
        } else {
            heightPixels = (int) (Resources.getSystem().getDisplayMetrics().widthPixels * 0.9);
            widthPixels = (int) (Resources.getSystem().getDisplayMetrics().heightPixels * 0.95);
        }
        widthPixels = (int) (Resources.getSystem().getDisplayMetrics().widthPixels );
        heightPixels = (int) (Resources.getSystem().getDisplayMetrics().heightPixels);

        puzzle = new ArrayList<MyButton>();
        customButton = new MyButton(this);
        Intent mediaIntent = new Intent(Intent.ACTION_GET_CONTENT);
        mediaIntent.setType("image/*"); //set mime type as per requirement
        startActivityForResult(mediaIntent, REQUESTCODE_PICK_IMAGE);
    }

    private View.OnClickListener loadOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            puzzle = new ArrayList<MyButton>();
            //customButton = new MyButton(getApplicationContext());
            layout.removeAllViews();
            layout.addView(reOrder);
            layout.addView(loadImage);
            Intent mediaIntent = new Intent(Intent.ACTION_GET_CONTENT);
            mediaIntent.setType("image/*"); //set mime type as per requirement
            startActivityForResult(mediaIntent, REQUESTCODE_PICK_IMAGE);
        }
    };
    private View.OnTouchListener reOrderOnClick = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == android.view.MotionEvent.ACTION_DOWN)
                reOrder();
            if (event.getAction() == MotionEvent.ACTION_UP)
                shuffleButton();
            return false;
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUESTCODE_PICK_IMAGE
                && resultCode == Activity.RESULT_OK) {
            Uri imageUri = data.getData();
            try {
                doJob(imageUri);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.d("", "Video URI= " + imageUri);


        }
    }

    private void start(List<Bitmap> bmp) {
        int i = 0;

        List<MyBitMap> myBitMapsList = new ArrayList<MyBitMap>();
        for (Bitmap bitmap : bmp) {
            MyBitMap myBitMap = new MyBitMap(bitmap, i);
            myBitMapsList.add(myBitMap);
            i++;
        }
        Collections.shuffle(myBitMapsList);
        i = 0;
        int whole = getWhole(myBitMapsList.size());
        for (MyBitMap myBitMap : myBitMapsList) {
            MyButton myButton = new MyButton(this, myBitMap, i);
            puzzle.add(myButton);
            i++;
        }
        puzzle.get(whole).setFree(true);
        removedButton = puzzle.get(whole);
        setSize();

        Log.d("set Width", "" + widthButton);

        addToLayout(widthButton, heightButton);

    }

    private void shuffleButton() {
        List<MyBitMap> myBitMapsList = new ArrayList<MyBitMap>();
        for (Map.Entry<Integer, MyBitMap> entry : currentPuzzleSort.entrySet()) {
            MyButton button = puzzle.get(entry.getKey());
            button.setMyBitMap(entry.getValue());
            button.invalidate();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private void setSize() {
        customButton.setMinimumWidth(heightButton);
        customButton.setMaxWidth(widthButton);
        customButton.setWidth(widthButton);
        customButton.setHeight(heightButton);
        customButton.setMinimumHeight(heightButton);
        customButton.setMaxHeight(heightButton);
        for (MyButton button : puzzle) {
            button.setMinimumWidth(widthButton);
            button.setMaxWidth(widthButton);
            button.setWidth(widthButton);
            button.setHeight(heightButton);
            button.setMinimumHeight(heightButton);
            button.setMaxHeight(heightButton);
        }
    }

    private int getWhole(int max) {
        Random r = new Random();
        return r.nextInt(max - 0) + 0;
    }

    private void addToLayout(int width, int height) {
        int i = 0, precWidth = 0, precHeight = 0;
        for (MyButton myButton : puzzle) {
            myButton.setX(precWidth);
            myButton.setY(precHeight);
            precWidth += width;
            i++;
            if (i % nbPerLine == 0) {
                precHeight += height;
                precWidth = 0;
            }
            myButton.setOnTouchListener(test);
            layout.addView(myButton);
        }
        layout.addView(customButton);
        customButton.setVisibility(View.GONE);
    }

    private void doJob(Uri imageUri) {
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
        } catch (Exception e) {

        }
        bitmap = scaleBitmap(bitmap);
        List<Bitmap> bitmaps = createBitmaps(bitmap);
        start(bitmaps);

    }

    private Bitmap scaleBitmap(Bitmap bitmap) {
        return scaleBitmap(bitmap, heightPixels, widthPixels);
    }

    private Bitmap scaleBitmap(Bitmap bitmap, int maxHeight, int maxWidth) {
       // if (bitmap.getHeight() < maxHeight && bitmap.getWidth() < maxWidth)
         //   return bitmap;
        float scale = Math.min(((float) maxHeight / bitmap.getWidth()), ((float) maxWidth / bitmap.getHeight()));

        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);

        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return bitmap;
    }

    public List<Bitmap> createBitmaps(Bitmap source) {
        List<Bitmap> bmp = new ArrayList<Bitmap>();
        int j, i;
        int k = 0;
        widthButton = (int) Math.ceil(source.getWidth() / nbPerLine);
        heightButton = (int) Math.ceil(source.getHeight() / nbPerLine);
        for (i = 0; i < nbPerLine; i++) {
            for (j = 0; j < nbPerLine; j++) {
                bmp.add(k++, Bitmap.createBitmap(source, (widthButton * j), (i * heightButton), widthButton, heightButton));
            }
        }
        setPosButton(widthButton, heightButton);
        return bmp;
    }

    private void setPosButton(int widthButton, int heightButton) {
        if (widthPixels > heightPixels) {
            loadImage.setX(widthButton * (nbPerLine + 2));
            loadImage.setY(0);
            reOrder.setX(widthButton * (nbPerLine + 2));
            reOrder.setY(heightButton + 20);
        } else {
            loadImage.setY(heightButton * nbPerLine + 20);
            loadImage.setX(widthButton * (nbPerLine / 2));
            reOrder.setY( (heightButton * nbPerLine) + 150);
            reOrder.setX(widthButton * (nbPerLine / 2));
        }
    }

    private void reOrder() {
        Map<Integer, MyBitMap> tmp = new HashMap<Integer, MyBitMap>();
        currentPuzzleSort = new HashMap<Integer, MyBitMap>();
        for (MyButton button : puzzle) {
            MyBitMap bitMapTmp = button.getMyBitMap();
            button.setFree(false);
            currentPuzzleSort.put(button.getPos(),bitMapTmp);
            if (bitMapTmp != null)
                tmp.put(bitMapTmp.getPos(), bitMapTmp);
            button.invalidate();
        }
        removedButton.setFree(true);
        for (Map.Entry<Integer, MyBitMap> entry : tmp.entrySet()) {
            MyButton button = puzzle.get(entry.getKey());
            button.setMyBitMap(entry.getValue());
            button.invalidate();
        }


    }

    private View.OnTouchListener test = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            Toast.makeText(PuzzleActivity.this, "Wesh T nul", Toast.LENGTH_SHORT).show();

            /* On fist touch */
            if (event.getAction() == android.view.MotionEvent.ACTION_DOWN && v instanceof MyButton) {
                buttonSelected = (MyButton) v;
                int pos = buttonSelected.getPos();

                if (pos + 1 > nbPerLine) {
                    if (puzzle.get(pos - nbPerLine).isFree()) {
                        free = Free.DOWN;
                        freeButton = puzzle.get(pos - nbPerLine);
                    }
                }
                if (puzzle.size() > pos + nbPerLine) {
                    if (puzzle.get(pos + nbPerLine).isFree()) {
                        free = Free.UP;
                        freeButton = puzzle.get(pos + nbPerLine);
                    }
                }
                if ((pos + 1) % nbPerLine != 0 && puzzle.size() >= pos + 1) {
                    if (puzzle.get(pos + 1).isFree()) {
                        free = Free.RIGHT;
                        freeButton = puzzle.get(pos + 1);
                    }
                }
                if (pos > 0 && (pos % nbPerLine) != 0) {
                    if (puzzle.get(pos - 1).isFree()) {
                        free = Free.LEFT;
                        freeButton = puzzle.get(pos - 1);
                    }
                }

                if (free != null) {
                    ((MyButton) v).getMyBitMap();
                    if (customButton.getMyBitMap() == null) {
                        customButton.setMyBitMap(buttonSelected.getMyBitMap());
                    }
                    buttonSelected.setVisibility(View.GONE);
                }
            }

            if (buttonSelected != null && (event.getAction() == MotionEvent.ACTION_MOVE
                    || event.getAction() == android.view.MotionEvent.ACTION_DOWN)) {
                if (free == Free.DOWN || free == Free.UP) {
                    float y = event.getRawY() - buttonSelected.getHeight() / 2;
                    if (free == Free.DOWN) {
                        if (y < freeButton.getY())
                            y = freeButton.getY();
                        if (y > buttonSelected.getY())
                            y = buttonSelected.getY();
                    } else {
                        if (y > freeButton.getY())
                            y = freeButton.getY();
                        if (y < buttonSelected.getY())
                            y = buttonSelected.getY();
                    }
                    customButton.setX(buttonSelected.getX());
                    customButton.setY(y);
                    customButton.setVisibility(View.VISIBLE);

                } else if (free == Free.RIGHT || free == Free.LEFT) {
                    float x = event.getRawX() - buttonSelected.getWidth() / 2;
                    if (free == Free.RIGHT) {
                        if (x > freeButton.getX())
                            x = freeButton.getX();
                        if (x < buttonSelected.getX())
                            x = buttonSelected.getX();
                    } else {
                        if (x < freeButton.getX())
                            x = freeButton.getX();
                        if (x > buttonSelected.getX())
                            x = buttonSelected.getX();
                    }
                    customButton.setY(buttonSelected.getY());
                    customButton.setX(x);
                    customButton.setVisibility(View.VISIBLE);
                }
            } else if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
                boolean find = false;
                if (free == Free.RIGHT) {
                    if (customButton.getX() > (freeButton.getX() - (freeButton.getWidth() / 2))) {
                        find = true;
                    }
                }

                if (free == Free.LEFT) {
                    if (customButton.getX() < (freeButton.getX() + (freeButton.getWidth() / 2))) {
                        find = true;
                    }
                }
                if (free == Free.DOWN) {
                    if (customButton.getY() < (freeButton.getY() + (freeButton.getHeight() / 2))) {
                        find = true;
                    }
                }
                if (free == Free.UP) {
                    if (customButton.getY() > (freeButton.getY() - (freeButton.getHeight() / 2))) {
                        find = true;
                    }
                }
                if (find) {
                    buttonSelected.setFree(true);
                    buttonSelected.setMyBitMap(freeButton.getMyBitMap());
                    freeButton.setMyBitMap(customButton.getMyBitMap());
                    freeButton.setFree(false);
                    freeButton.invalidate();
                }
                buttonSelected.setVisibility(View.VISIBLE);
                free = null;
                customButton.setVisibility(View.GONE);
                customButton.setMyBitMap(null);
                buttonSelected = null;
            }
            return false;
        }
    };


}
