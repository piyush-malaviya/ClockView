package com.pcm.clockviewlib;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.util.Calendar;

public class ClockView extends View {
    private static final int START_ANGLE = -90;
    private Paint mPaint;
    private int mSecond;
    private int mMinute;
    private int mHour;
    private int mSecondHandColor = Color.parseColor("#2E7D32");
    private int mMinuteHandColor = Color.parseColor("#EF6C00");
    private int mHourHandColor = Color.parseColor("#F44336");
    private int mClockFaceColor = Color.parseColor("#1565C0");
    private int mClockFaceBackgroundId = -1;
    private Bitmap mClockFaceBackgroundBitmap;
    private boolean isDigitalTimeShow;
    private int mClockFace = 1;
    private Xfermode mClockBackGroundMask;
    private Xfermode mResetMask;

    public ClockView(Context context) {
        this(context, null);
    }

    public ClockView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ClockView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ClockView);

        mSecondHandColor = typedArray.getColor(R.styleable.ClockView_second_hand_color, mSecondHandColor);
        mMinuteHandColor = typedArray.getColor(R.styleable.ClockView_minute_hand_color, mMinuteHandColor);
        mHourHandColor = typedArray.getColor(R.styleable.ClockView_hour_hand_color, mHourHandColor);
        mClockFaceColor = typedArray.getColor(R.styleable.ClockView_clock_face_color, mClockFaceColor);
        mClockFace = typedArray.getInt(R.styleable.ClockView_clock_face, 1);
        mClockFaceBackgroundId = typedArray.getResourceId(R.styleable.ClockView_clock_face_background, -1);

        int showDigitalTime = typedArray.getInt(R.styleable.ClockView_show_digital_time, 1);
        if (showDigitalTime <= 0) {
            isDigitalTimeShow = false;
        } else {
            isDigitalTimeShow = true;
        }
        mClockBackGroundMask = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);
        typedArray.recycle();
        init();
        setClockTime(Calendar.getInstance().getTimeInMillis());
    }

    public void setClockTime(long timeInMillis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeInMillis);
        mSecond = calendar.get(Calendar.SECOND) * 6;
        mMinute = calendar.get(Calendar.MINUTE) * 6;
        mHour = calendar.get(Calendar.HOUR) * 30 + (mMinute / 24);
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.RED);
        mPaint.setStrokeWidth(2);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);

        if (mClockFaceBackgroundId != -1) {
            mClockFaceBackgroundBitmap = BitmapFactory.decodeResource(getResources(), mClockFaceBackgroundId);
        }

        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                // one min have 60 sec so 360/60=6
                //mSecond = (mSecond + 6) % 360;
                mSecond += 6;
                if (mSecond >= 360) {
                    mMinute += 6;
                    mHour += 1;
                    mSecond = 0;
                }
                if (mMinute >= 360) {
                    mMinute = 0;
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

    private Bitmap getCircleBitmap(Bitmap bitmap) {
        final Bitmap result = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(result);

        final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        canvas.drawOval(rectF, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int center = getWidth() / 2;
        float radius = center / 1.25f;
        mPaint.setColor(mClockFaceColor);

        if (mClockFaceBackgroundBitmap != null) {
            int left = (getWidth() - mClockFaceBackgroundBitmap.getWidth()) / 2;
            int top = (getHeight() - mClockFaceBackgroundBitmap.getHeight()) / 2;
            canvas.drawBitmap(getCircleBitmap(mClockFaceBackgroundBitmap), left, top, mPaint);
        }
        canvas.drawCircle(center, center, radius, mPaint);
        canvas.drawCircle(center, center, 6, mPaint);

        drawClockFace(canvas);
        drawTimeOnCanvas(canvas);
        drawHoursLine(canvas);
        drawMinuteLine(canvas);
        drawSecondLine(canvas);
    }

    private void drawTimeOnCanvas(Canvas canvas) {
        int center = getWidth() / 2;
        String hour = String.valueOf(Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
        String minute = String.valueOf(Calendar.getInstance().get(Calendar.MINUTE));
        String sec = String.valueOf(Calendar.getInstance().get(Calendar.SECOND));

        if (Integer.parseInt(hour) < 10) {
            hour = "0" + hour;
        }
        if (Integer.parseInt(minute) < 10) {
            minute = "0" + minute;
        }
        if (Integer.parseInt(sec) < 10) {
            sec = "0" + sec;
        }

        if (isDigitalTimeShow) {
            mPaint.setTextAlign(Paint.Align.CENTER);
            mPaint.setTextSize(36);
            canvas.drawText(hour + ":" +
                    minute + ":" +
                    sec, center, center + 60, mPaint);
        }
    }

    private void drawClockFace(Canvas canvas) {

        if (mClockFace == 1) {
            drawDigitalFace(canvas);
        } else if (mClockFace == 2) {
            drawRomanClockFace(canvas);
        } else {
            drawNormalFace(canvas);
        }
    }

    private void drawSecondLine(Canvas canvas) {
        int center = getWidth() / 2;
        float angle = (float) (START_ANGLE + mSecond + (Math.PI / 180)); // Need to convert to radians first
        double radians = Math.toRadians(angle);
        float radius = center / 1.5f;
        float startX = (float) (center + radius * Math.cos(radians));
        float startY = (float) (center + radius * Math.sin(radians));
        mPaint.setColor(mSecondHandColor);
        mPaint.setStrokeWidth(2);
        canvas.drawLine(startX, startY, center, center, mPaint);
    }

    private void drawMinuteLine(Canvas canvas) {
        int center = getWidth() / 2;
        float angle = (float) (START_ANGLE + mMinute + (Math.PI / 180)); // Need to convert to radians first
        double radians = Math.toRadians(angle);
        float radius = center / 1.75f;
        float startX = (float) (center + radius * Math.cos(radians));
        float startY = (float) (center + radius * Math.sin(radians));
        mPaint.setColor(mMinuteHandColor);
        mPaint.setStrokeWidth(4);
        canvas.drawLine(startX, startY, center, center, mPaint);
        canvas.drawCircle(startX, startY, 4, mPaint);
    }

    private void drawHoursLine(Canvas canvas) {
        int center = getWidth() / 2;
        float angle = (float) (START_ANGLE + mHour + (Math.PI / 180)); // Need to convert to radians first
        double radians = Math.toRadians(angle);
        float radius = center / 2f;
        float startX = (float) (center + radius * Math.cos(radians));
        float startY = (float) (center + radius * Math.sin(radians));
        mPaint.setColor(mHourHandColor);
        mPaint.setStrokeWidth(6);
        canvas.drawLine(startX, startY, center, center, mPaint);
        canvas.drawCircle(startX, startY, 4, mPaint);
    }

    private void drawDigitalFace(Canvas canvas) {
        int center = getWidth() / 2;

        for (int index = 1; index <= 12; index++) {
            float angle = (float) (START_ANGLE + (index * 30) + (Math.PI / 180)); // Need to convert to radians first
            double radians = Math.toRadians(angle);
            float radius = center / 1.5f;
            float startX = (float) (center + radius * Math.cos(radians));
            float startY = (float) (center + radius * Math.sin(radians));
            mPaint.setColor(mClockFaceColor);
            mPaint.setTextSize(36);
            mPaint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(String.valueOf(index), startX, startY, mPaint);
        }
    }

    /**
     * If user wants roman clock
     *
     * @param canvas : view can canvas
     */
    private void drawRomanClockFace(Canvas canvas) {
        int center = getWidth() / 2;
        String[] strings = new String[]{"XII", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X", "XI"};
        for (int index = 0; index < 12; index++) {
            float angle = (float) (START_ANGLE + (index * 30) + (Math.PI / 180)); // Need to convert to radians first
            double radians = Math.toRadians(angle);
            float radius = center / 1.5f;
            float startX = (float) (center + radius * Math.cos(radians));
            float startY = (float) (center + radius * Math.sin(radians));
            mPaint.setColor(mClockFaceColor);
            mPaint.setTextSize(36);
            mPaint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(strings[index], startX, startY, mPaint);
        }
    }

    private void drawNormalFace(Canvas canvas) {
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
            mPaint.setColor(mClockFaceColor);
            mPaint.setStrokeWidth(2);
            if (index % 5 == 0) {
                canvas.drawCircle(startX, startY, 4, mPaint);
                canvas.drawCircle(startX2, startY2, 4, mPaint);
                canvas.drawCircle(startX1, startY1, 4, mPaint);
            } else {
                canvas.drawCircle(startX1, startY1, 2, mPaint);
            }
        }
    }
}
