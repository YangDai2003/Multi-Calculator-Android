package com.yangdai.calc.main.toolbox.functions.ruler;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.NonNull;

import com.google.android.material.R;
import com.google.android.material.color.MaterialColors;

public class RulerViewInch extends View {

    private Paint paintText;
    private Paint paintMain;
    private Paint paintSide;
    private float inchToPx;
    private float inchInterval;
    private int inchTextInterval;
    private float topPadding;

    public RulerViewInch(Context context) {
        this(context, null);
    }

    public RulerViewInch(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RulerViewInch(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        paintText = new Paint();
        paintText.setColor(MaterialColors.getColor(this, R.attr.colorOutline));
        paintText.setStrokeWidth(dpToPx(context, 2f));
        paintText.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 20f, getResources().getDisplayMetrics()));
        paintText.setAntiAlias(true);

        paintMain = new Paint();
        paintMain.setColor(MaterialColors.getColor(this, R.attr.colorOutline));
        paintMain.setStrokeWidth(dpToPx(context, 2f));
        paintMain.setAntiAlias(true);

        paintSide = new Paint();
        paintSide.setColor(MaterialColors.getColor(this, R.attr.colorOutline));
        paintSide.setAlpha(127);
        paintSide.setStrokeWidth(dpToPx(context, 2f));
        paintSide.setAntiAlias(true);

        inchToPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_IN, 1f, getResources().getDisplayMetrics());
        inchInterval = inchToPx / 10f;
        inchTextInterval = 10;
        topPadding = dpToPx(context, 24f);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        float width = getWidth();
        float height = getHeight();

        float numInches = (height - topPadding) / inchToPx;
        int numIntervals = (int) (numInches * 10);

        float longLineLength = width * 0.53f;
        float midLineLength = width * 0.43f;
        float shortLineLength = width * 0.34f;

        for (int i = 0; i <= numIntervals; i++) {
            float y = topPadding + i * inchInterval;
            if (i % inchTextInterval == 0) {
                canvas.drawLine(0f, y, longLineLength, y, paintMain);
                String text = String.valueOf(i / inchTextInterval);
                float textWidth = paintText.measureText(text);
                float textHeight = paintText.descent() - paintText.ascent();
                float textX = (width - longLineLength) / 2 + longLineLength - textWidth / 2;
                float textY = y + textHeight / 3;
                paintText.setColor(MaterialColors.getColor(this, (i / inchTextInterval) % 12 == 0 ? R.attr.colorOnSurface : R.attr.colorOutline));
                canvas.drawText(text, textX, textY, paintText);
            } else if (i % 5 == 0) {
                canvas.drawLine(0f, y, midLineLength, y, paintSide);
            } else {
                canvas.drawLine(0f, y, shortLineLength, y, paintSide);
            }
        }
    }

    private float dpToPx(Context context, float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }
}
