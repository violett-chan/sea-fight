package com.violet.course;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

public class GridView extends View {
    private int width, height;
    private int x = 10, y = 10;
    Paint paint;


    public GridView(Context context) {
        super(context);
        init();
    }

    public GridView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GridView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public GridView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(1);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        width = getWidth();
        height = getHeight();

        for (int i = 0; i <= y; i++) {
            float x = i * (float) (width - 1) / y;
            canvas.drawLine(x, 0, x, height, paint);
        }

        for (int i = 0; i <= x; i++) {
            float y = i * (float) (height - 1) / x;
            canvas.drawLine(0, y, width, y, paint);
        }
    }

}
