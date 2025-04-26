package com.example.woof.Utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class DogWeightScaleView extends View {
    private float minWeight = 5;
    private float maxWeight = 30;
    private float currentWeight = 10;
    private float maxScaleWeight = maxWeight + 10;

    private Paint linePaint;
    private Paint rangePaint;
    private Paint currentWeightPaint;
    private Paint textPaint;
    private Paint tickPaint;

    private final int TICK_INTERVAL = 5;

    public DogWeightScaleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    private void init(){
        linePaint = new Paint();
        linePaint.setColor(Color.RED);
        linePaint.setStrokeWidth(8);

        rangePaint = new Paint();
        rangePaint.setColor(Color.GREEN);
        rangePaint.setStrokeWidth(12);

        currentWeightPaint = new Paint();
        currentWeightPaint.setColor(Color.RED);
        currentWeightPaint.setStrokeWidth(8);

        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(38f);
        textPaint.setTextAlign(Paint.Align.CENTER);

        tickPaint = new Paint();
        tickPaint.setColor(Color.DKGRAY);
        tickPaint.setStrokeWidth(4);
    }

    public void setWeights(float min, float max, float current, float scaleMax){
        minWeight = min;
        maxWeight = max;
        currentWeight = current;
        maxScaleWeight = scaleMax;
        invalidate();
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        float width = getWidth();
        float height = getHeight();
        float padding = 60;
        float centerY = height / 2f;

        float scaleStartX = padding;
        float scaleEndX = width - padding;

        // Calculate X positions
        float rangeStartX = getPositionForWeight(minWeight, scaleStartX, scaleEndX);
        float rangeEndX = getPositionForWeight(maxWeight, scaleStartX, scaleEndX);
        float currentX = getPositionForWeight(currentWeight, scaleStartX, scaleEndX);

        // Draw base line
        canvas.drawLine(scaleStartX, centerY, scaleEndX, centerY, linePaint);

        // Draw healthy range
        rangePaint.setColor(Color.GREEN);
        canvas.drawLine(rangeStartX, centerY, rangeEndX, centerY, rangePaint);

        // Background color based on current weight
        if (currentWeight < minWeight) {
            currentWeightPaint.setColor(Color.RED); // Too low
        } else if (currentWeight > maxWeight) {
            currentWeightPaint.setColor(Color.rgb(255, 165, 0)); // Orange for high
        } else {
            currentWeightPaint.setColor(Color.BLUE); // Good
        }

        // Draw current weight marker
        canvas.drawLine(currentX, centerY - 30, currentX, centerY + 30, currentWeightPaint);

        // Draw tick marks and labels
        for (int i = 0; i <= maxScaleWeight; i += TICK_INTERVAL) {
            float x = getPositionForWeight(i, scaleStartX, scaleEndX);
            canvas.drawLine(x, centerY - 15, x, centerY + 15, tickPaint);
            canvas.drawText(String.valueOf(i), x, centerY + 90, textPaint);
        }

        // Optionally: Draw min/max labels
        canvas.drawText("Min: " + minWeight, rangeStartX, centerY - 60, textPaint);
        canvas.drawText("Max: " + maxWeight, rangeEndX, centerY - 60, textPaint);
    }

    private float getPositionForWeight(float weight, float startX, float endX) {
        float percentage = weight / maxScaleWeight;
        return startX + percentage * (endX - startX);
    }
}
