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
import androidx.core.content.ContextCompat;

import com.example.fithit.Enums.MetricType;
import com.example.fithit.R;

public class CircularMetricsView extends View {
    private Paint centerCirclePaint;
    private Paint iconCirclePaint;
    private Paint textPaint;
    private Paint selectedIconPaint;
    private float centerX;
    private float centerY;
    private float radius;
    private MetricType selectedMetric;
    private float currentValue = 0;
    private OnMetricSelectedListener listener;

    public interface OnMetricSelectedListener {
        void onMetricSelected(MetricType type, float value);
    }

    public CircularMetricsView(Context context) {
        super(context);
        init();
    }

    public CircularMetricsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        // Initialize paints
        centerCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        centerCirclePaint.setColor(Color.LTGRAY);
        centerCirclePaint.setStyle(Paint.Style.FILL);

        iconCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        iconCirclePaint.setColor(Color.WHITE);
        iconCirclePaint.setStyle(Paint.Style.FILL);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(40f);
        textPaint.setTextAlign(Paint.Align.CENTER);

        selectedIconPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        selectedIconPaint.setColor(Color.parseColor("#E0E0E0")); // Light gray when selected
        selectedIconPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        centerX = w / 2f;
        centerY = h / 2f;
        radius = Math.min(w, h) / 3f;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Draw center circle
        canvas.drawCircle(centerX, centerY, radius / 2, centerCirclePaint);

        // Draw value text
        String valueText = selectedMetric != null ?
                String.format("%.0f%s", currentValue, getUnitForMetric(selectedMetric)) : "0%";
        canvas.drawText(valueText, centerX, centerY + textPaint.getTextSize() / 3, textPaint);

        // Draw metric icons
        drawMetricIcons(canvas);
    }

    private void drawMetricIcons(Canvas canvas) {
        int totalMetrics = MetricType.values().length;
        float iconRadius = radius / 4f;

        for (int i = 0; i < totalMetrics; i++) {
            double angle = (i * 2 * Math.PI / totalMetrics) - Math.PI / 2;
            float x = centerX + (float) (Math.cos(angle) * radius);
            float y = centerY + (float) (Math.sin(angle) * radius);

            MetricType type = MetricType.values()[i];

            // Draw circle background
            Paint paint = (selectedMetric == type) ? selectedIconPaint : iconCirclePaint;
            canvas.drawCircle(x, y, iconRadius, paint);

            // Draw icon
            Drawable icon = getIconForMetric(type);
            if (icon != null) {
                int iconSize = (int) (iconRadius * 1.2f);
                icon.setBounds(
                        (int) (x - iconSize/2),
                        (int) (y - iconSize/2),
                        (int) (x + iconSize/2),
                        (int) (y + iconSize/2)
                );
                icon.setTint(getColorForMetric(type));
                icon.draw(canvas);
            }
        }
    }

    @SuppressLint("ResourceType")
    private Drawable getIconForMetric(MetricType type) {
        switch (type) {
            case WEIGHT:
                return ContextCompat.getDrawable(getContext(), R.drawable.ic_weight);
            case HEART_RATE:
                return ContextCompat.getDrawable(getContext(), R.drawable.ic_heart);
            case BODY_FAT:
                return ContextCompat.getDrawable(getContext(), R.drawable.ic_body_fat);
            case MUSCLE_MASS:
                return ContextCompat.getDrawable(getContext(), R.drawable.ic_muscle);
            case ENDURANCE_TIME:
                return ContextCompat.getDrawable(getContext(), R.drawable.ic_endurance);
            default:
                return ContextCompat.getDrawable(getContext(), R.drawable.ic_metric_default);
        }
    }

    private int getColorForMetric(MetricType type) {
        switch (type) {
            case WEIGHT:
                return Color.parseColor("#2196F3"); // Blue
            case HEART_RATE:
                return Color.parseColor("#F44336"); // Red
            case BODY_FAT:
                return Color.parseColor("#00BCD4"); // Cyan
            case MUSCLE_MASS:
                return Color.parseColor("#4CAF50"); // Green
            case ENDURANCE_TIME:
                return Color.parseColor("#FFC107"); // Yellow
            default:
                return Color.parseColor("#9E9E9E"); // Gray
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                int touchedMetric = getTouchedMetric(x, y);
                if (touchedMetric != -1) {
                    selectedMetric = MetricType.values()[touchedMetric];
                    if (listener != null) {
                        listener.onMetricSelected(selectedMetric, currentValue);
                    }
                    invalidate();
                    return true;
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    private int getTouchedMetric(float x, float y) {
        int totalMetrics = MetricType.values().length;
        float iconRadius = radius / 4f;

        for (int i = 0; i < totalMetrics; i++) {
            double angle = (i * 2 * Math.PI / totalMetrics) - Math.PI / 2;
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

    public void setValue(float value) {
        currentValue = value;
        invalidate();
    }

    public void setOnMetricSelectedListener(OnMetricSelectedListener listener) {
        this.listener = listener;
    }

    private String getUnitForMetric(MetricType type) {
        switch (type) {
            case WEIGHT:
                return "kg";
            case HEART_RATE:
                return "bpm";
            case BODY_FAT:
            case MUSCLE_MASS:
                return "%";
            case ENDURANCE_TIME:
                return "min";
            default:
                return "";
        }
    }
}