package com.pcm.clockview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.util.Calendar;

public class ClockView extends View {
    private static final int START_ANGLE = -90;
    private Paint paint;
    private int second;
    private int minute;
    private int hour;

    public ClockView(Context context) {
        this(context, null);
    }

    public ClockView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ClockView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.RED);
        paint.setStrokeWidth(2);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);

        second = Calendar.getInstance().get(Calendar.SECOND) * 6;
        minute = Calendar.getInstance().get(Calendar.MINUTE) * 6;
        hour = Calendar.getInstance().get(Calendar.HOUR) * 30 + (minute / 24);

        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                // one min have 60 sec so 360/60=6
                //second = (second + 6) % 360;
                second += 6;
                if (second >= 360) {
                    minute += 6;
                    hour += 1;
                    second = 0;
                }
                if (minute >= 360) {
                    minute = 0;
                }
                invalidate();
                handler.postDelayed(this, 1000);
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int squareSize = width > height ? height : width;
        super.onMeasure(MeasureSpec.makeMeasureSpec(squareSize, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(squareSize, MeasureSpec.EXACTLY));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int center = getWidth() / 2;
        float radius = center / 1.25f;
        paint.setColor(Color.parseColor("#1565C0"));
        canvas.drawCircle(center, center, radius, paint);
        canvas.drawCircle(center, center, 6, paint);

        //drawNumber(canvas);
        drawDialer(canvas);
        drawHoursLine(canvas);
        drawMinuteLine(canvas);
        drawSecondLine(canvas);
    }

    private void drawSecondLine(Canvas canvas) {
        int center = getWidth() / 2;
        float angle = (float) (START_ANGLE + second + (Math.PI / 180)); // Need to convert to radians first
        double radians = Math.toRadians(angle);
        float radius = center / 1.5f;
        float startX = (float) (center + radius * Math.cos(radians));
        float startY = (float) (center + radius * Math.sin(radians));
        paint.setColor(Color.parseColor("#2E7D32"));
        paint.setStrokeWidth(2);
        canvas.drawLine(startX, startY, center, center, paint);
    }

    private void drawMinuteLine(Canvas canvas) {
        int center = getWidth() / 2;
        float angle = (float) (START_ANGLE + minute + (Math.PI / 180)); // Need to convert to radians first
        double radians = Math.toRadians(angle);
        float radius = center / 1.75f;
        float startX = (float) (center + radius * Math.cos(radians));
        float startY = (float) (center + radius * Math.sin(radians));
        paint.setColor(Color.parseColor("#EF6C00"));
        paint.setStrokeWidth(4);
        canvas.drawLine(startX, startY, center, center, paint);
        canvas.drawCircle(startX, startY, 4, paint);
    }

    private void drawHoursLine(Canvas canvas) {
        int center = getWidth() / 2;
        float angle = (float) (START_ANGLE + hour + (Math.PI / 180)); // Need to convert to radians first
        double radians = Math.toRadians(angle);
        float radius = center / 2f;
        float startX = (float) (center + radius * Math.cos(radians));
        float startY = (float) (center + radius * Math.sin(radians));
        paint.setColor(Color.parseColor("#F44336"));
        paint.setStrokeWidth(6);
        canvas.drawLine(startX, startY, center, center, paint);
        canvas.drawCircle(startX, startY, 4, paint);
    }

    private void drawNumber(Canvas canvas) {
        int center = getWidth() / 2;

        for (int index = 1; index <= 12; index++) {

            float angle = (float) (START_ANGLE + (index * 30) + (Math.PI / 180)); // Need to convert to radians first
            double radians = Math.toRadians(angle);
            float radius = center / 1.5f;
            float startX = (float) (center + radius * Math.cos(radians));
            float startY = (float) (center + radius * Math.sin(radians));
            paint.setColor(Color.parseColor("#2E7D32"));
            //paint.setStrokeWidth(2);
            //canvas.drawLine(startX, startY, center, center, paint);
            paint.setTextSize(36);
            paint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(String.valueOf(index), startX, startY, paint);
        }
    }

    private void drawDialer(Canvas canvas) {
        int center = getWidth() / 2;

        for (int index = 1; index <= 60; index++) {
            float angle = (float) ((index * 6) + (Math.PI / 180)); // Need to convert to radians first
            double radians = Math.toRadians(angle);
            float radius = center / 1.4f;
            float startX = (float) (center + radius * Math.cos(radians));
            float startY = (float) (center + radius * Math.sin(radians));
            float startX1 = (float) (center + center / 1.35f * Math.cos(radians));
            float startY1 = (float) (center + center / 1.35f * Math.sin(radians));
            float startX2 = (float) (center + center / 1.3f * Math.cos(radians));
            float startY2 = (float) (center + center / 1.3f * Math.sin(radians));
            paint.setColor(Color.parseColor("#2E7D32"));
            paint.setStrokeWidth(2);
            if (index % 5 == 0) {
                canvas.drawCircle(startX, startY, 4, paint);
                canvas.drawCircle(startX2, startY2, 4, paint);
                canvas.drawCircle(startX1, startY1, 4, paint);
            } else {
                canvas.drawCircle(startX1, startY1, 2, paint);
            }
            //canvas.drawLine(startX, startY, startX + 10, startY + 10, paint);
        }
    }
}
