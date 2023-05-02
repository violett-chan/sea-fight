package com.violet.course;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.AppCompatImageView;


public class DragImageView extends AppCompatImageView implements View.OnTouchListener {

    private float mStartX;
    private float mStartY;
    private float mLastX;
    private float mLastY;
    float ix, iy, step;
    int width, height;
    int life;
    float startX, startY;
    boolean vertical, first;
    long downEvent;

    public DragImageView(Context context) {
        super(context);
        init();
    }

    public DragImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DragImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setOnTouchListener(this);
        life = (getContentDescription().charAt(0) - '0');
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        float x = event.getRawX();
        float y = event.getRawY();
        if (MainActivity.preGridView != null) {
            ix = MainActivity.preGridView.getX();
            iy = MainActivity.preGridView.getY();
            width = MainActivity.preGridView.getWidth();
            height = MainActivity.preGridView.getHeight();
            step = (float) width / 10f;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mStartX = getX();
                mStartY = getY();
                if (!first) {
                    startX = getX();
                    startY = getY();
                    first = true;
                }
                mLastX = x;
                mLastY = y;
                downEvent = event.getDownTime();
                break;
            case MotionEvent.ACTION_MOVE:
                float deltaX = x - mLastX;
                float deltaY = y - mLastY;
                setX(mStartX + deltaX);
                setY(mStartY + deltaY);
                break;
            case MotionEvent.ACTION_UP:
                long duration = event.getEventTime() - event.getDownTime();
                if (duration < 200 && getX() == startX && getY() == startY) {
                    ViewGroup.LayoutParams layoutParams = getLayoutParams();
                    if (vertical) {
                        layoutParams.width = (int) (step * life);
                        layoutParams.height = (int) step;
                    } else {
                        layoutParams.width = (int) step;
                        layoutParams.height = (int) (step * life);
                    }
                    vertical = !vertical;
                    setLayoutParams(layoutParams);
                    break;
                } else {
                    int pos = Integer.parseInt(getResources().getResourceEntryName(getId()).replace("ship", "")) - 1;
                    if (((getX() - ix) > (0 - step / 2)) && ((getY() - iy) > (0 - step / 2))) {
                        int posX = (int) ((getX() - ix + step / 2) / width * 10);
                        int posY = (int) ((getY() - iy + step / 2) / height * 10);
                        if (vertical) {
                            if (MainActivity.russianPlayer.setShip(pos,
                                    posX, posY, posX, posY + life, life, true)) {
                                setX(ix + step * posX);
                                setY(iy + step * posY);
                                break;
                            }
                        } else {
                            if (MainActivity.russianPlayer.setShip(pos,
                                    posX, posY, posX + life, posY, life, false)) {
                                setX(ix + step * posX);
                                setY(iy + step * posY);
                                break;
                            }
                        }
                    }
                    MainActivity.russianPlayer.delShip(pos);
                    setX(startX);
                    setY(startY);
                }
        }
        return true;
    }
}
