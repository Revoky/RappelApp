package com.example.rappelapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class BellView extends View {
    private final Paint paintBell = new Paint();
    private final Paint paintText = new Paint();
    private String timeText = "07:30 AM";

    public BellView(Context context) {
        super(context);
        init();
    }

    public BellView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BellView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paintBell.setColor(Color.parseColor("#FFA500")); // Orange color
        paintBell.setStyle(Paint.Style.STROKE);
        paintBell.setStrokeWidth(10f);
        paintBell.setAntiAlias(true);

        paintText.setColor(Color.BLACK);
        paintText.setTextSize(48f);
        paintText.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBell(canvas);
        canvas.drawText(timeText, 140f, 80f, paintText);
    }

    private void drawBell(Canvas canvas) {
        RectF rectF = new RectF(40f, 60f, 80f, 100f);
        canvas.drawArc(rectF, 180f, 180f, false, paintBell);
        canvas.drawLine(40f, 80f, 40f, 100f, paintBell);
        canvas.drawLine(80f, 80f, 80f, 100f, paintBell);
        canvas.drawOval(50f, 40f, 70f, 60f, paintBell);
        canvas.drawLine(40f, 100f, 80f, 100f, paintBell);
        canvas.drawCircle(60f, 110f, 6f, paintBell);
    }

    public void setTime(String time) {
        timeText = time;
        invalidate();
    }
}
