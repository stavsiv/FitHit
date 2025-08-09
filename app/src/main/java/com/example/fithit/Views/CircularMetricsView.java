package com.example.fithit.Views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.example.fithit.Models.Metric;
import com.example.fithit.R;

public class CircularMetricsView extends View {
    private Paint centerCirclePaint;
    private Paint iconCirclePaint;
    private Paint textPaint;
    private Paint selectedIconPaint;
    private float centerX;
    private float centerY;
    private float radius;
    private String selectedMetric;
    private float currentValue = 0;
    private OnMetricSelectedListener listener;

    public interface OnMetricSelectedListener {
        void onMetricSelected(String type, float value);
    }

    private final String[] metricTypes = new String[]{
            Metric.WEIGHT,
            Metric.HEART_RATE,
            Metric.STEPS,
            Metric.CALORIES
    };

    public CircularMetricsView(Context context) {
        super(context);
        init();
    }

    public CircularMetricsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        centerCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        centerCirclePaint.setColor(Color.LTGRAY);
        centerCirclePaint.setStyle(Paint.Style.FILL);

        iconCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        iconCirclePaint.setColor(Color.WHITE);
        iconCirclePaint.setStyle(Paint.Style.FILL);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(60f);
        textPaint.setTextAlign(Paint.Align.CENTER);

        selectedIconPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        selectedIconPaint.setColor(Color.parseColor("#E0E0E0"));
        selectedIconPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawCircle(centerX, centerY, radius * 0.6f, centerCirclePaint);

        String valueText = selectedMetric != null ?
                String.format("%.0f%s", currentValue, getUnitForMetric(selectedMetric)) : "0";
        canvas.drawText(valueText, centerX, centerY + textPaint.getTextSize() / 4, textPaint);

        drawMetricIcons(canvas);
    }

    private void drawMetricIcons(Canvas canvas) {
        float iconRadius = radius / 4f;

        for (int i = 0; i < metricTypes.length; i++) {
            double angle = (i * 2 * Math.PI / metricTypes.length) - Math.PI / 2;
            float x = centerX + (float) (Math.cos(angle) * radius);
            float y = centerY + (float) (Math.sin(angle) * radius);

            String type = metricTypes[i];

            Paint paint = (type.equals(selectedMetric)) ? selectedIconPaint : iconCirclePaint;
            canvas.drawCircle(x, y, iconRadius, paint);

            Drawable icon = getIconForMetric(type);
            if (icon != null) {
                int iconSize = (int) (iconRadius * 1.2f);
                icon.setBounds(
                        (int) (x - (float) iconSize / 2),
                        (int) (y - (float) iconSize / 2),
                        (int) (x + (float) iconSize / 2),
                        (int) (y + (float) iconSize / 2)
                );
                icon.setTint(getColorForMetric(type));
                icon.draw(canvas);
            }
        }
    }

    @SuppressLint("ResourceType")
    private Drawable getIconForMetric(String type) {
        switch (type) {
            case Metric.WEIGHT:
                return ContextCompat.getDrawable(getContext(), R.drawable.ic_weight);
            case Metric.HEART_RATE:
                return ContextCompat.getDrawable(getContext(), R.drawable.ic_heart);
            case Metric.STEPS:
                return ContextCompat.getDrawable(getContext(), R.drawable.ic_steps);
            case Metric.CALORIES:
                return ContextCompat.getDrawable(getContext(), R.drawable.ic_kcal);
            default:
                return ContextCompat.getDrawable(getContext(), R.drawable.ic_metric_default);
        }
    }

    private int getColorForMetric(String type) {
        switch (type) {
            case Metric.WEIGHT:
                return Color.parseColor("#2196F3"); // Blue
            case Metric.HEART_RATE:
                return Color.parseColor("#F44336"); // Red
            case Metric.STEPS:
                return Color.parseColor("#FFC107"); // Yellow
            case Metric.CALORIES:
                return Color.parseColor("#4CAF50"); // Green
            default:
                return Color.parseColor("#9E9E9E"); // Gray
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            int touchedMetric = getTouchedMetric(x, y);
            if (touchedMetric != -1) {
                selectedMetric = metricTypes[touchedMetric];
                if (listener != null) {
                    listener.onMetricSelected(selectedMetric, currentValue);
                }
                invalidate();
                return true;
            }
        }
        return super.onTouchEvent(event);
    }

    private int getTouchedMetric(float x, float y) {
        float iconRadius = radius / 4f;

        for (int i = 0; i < metricTypes.length; i++) {
            double angle = (i * 2 * Math.PI / metricTypes.length) - Math.PI / 2;
            float iconX = centerX + (float) (Math.cos(angle) * radius);
            float iconY = centerY + (float) (Math.sin(angle) * radius);

            float dx = x - iconX;
            float dy = y - iconY;
            float distance = (float) Math.sqrt(dx * dx + dy * dy);

            if (distance < iconRadius) {
                return i;
            }
        }
        return -1;
    }

    private String getUnitForMetric(String type) {
        switch (type) {
            case Metric.WEIGHT:
                return " kg";
            case Metric.HEART_RATE:
                return " bpm";
            case Metric.STEPS:
                return " steps";
            case Metric.CALORIES:
                return " kcal";
            default:
                return "";
        }
    }

    public void setValue(float value) {
        currentValue = value;
        invalidate();
    }

    public void setOnMetricSelectedListener(OnMetricSelectedListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        int size = Math.min(width, height);
        setMeasuredDimension(size, size);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        centerX = w / 2f;
        centerY = h / 2f;
        radius = Math.min(w, h)/3f;
    }
}