package com.pcm.clockview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class DistanceRangeView extends View {

    private static final String TAG = DistanceRangeView.class.getSimpleName();
    private static final int RADIUS_METER = 50; // 50 meter
    private static int BAG_WIDTH = 50;
    private static int BAG_HEIGHT = 50;
    private static float RADIUS = 25;
    private Bitmap bagIcon;
    private Paint paintObject, paintBack;

    public DistanceRangeView(Context context) {
        super(context);
        init();
    }

    public DistanceRangeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DistanceRangeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paintObject = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintBack = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintBack.setColor(Color.WHITE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        int displayCenter = centerX;


        double pos = (displayCenter * 1) / RADIUS_METER;
        float angle = (float) (Math.PI / 45f); // Need to convert to radians first
        float startX = (float) (centerX + pos * Math.sin(angle));
        float startY = (float) (centerY - pos * Math.cos(angle));


        canvas.restore();
    }
}