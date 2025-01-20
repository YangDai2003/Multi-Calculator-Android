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

public class RulerView extends View {

    private Paint paintText;
    private Paint paintMain;
    private Paint paintSide;
    private float mmToPx;
    private float topPadding;

    public RulerView(Context context) {
        this(context, null);
    }

    public RulerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RulerView(Context context, AttributeSet attrs, int defStyleAttr) {
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

        mmToPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_MM, 1f, getResources().getDisplayMetrics());
        topPadding = dpToPx(context, 24f);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        float width = getWidth();
        float height = getHeight();

        int numDivisions = (int) ((height - topPadding) / mmToPx);
        float longLineLength = width * 0.53f;
        float midLineLength = width * 0.43f;
        float shortLineLength = width * 0.34f;

        for (int i = 0; i <= numDivisions; i++) {
            float y = topPadding + i * mmToPx;
            if (i % 10 == 0) {
                canvas.drawLine(width - longLineLength, y, width, y, paintMain);
                String text = String.valueOf(i / 10);
                float textWidth = paintText.measureText(text);
                float textHeight = paintText.descent() - paintText.ascent();
                float textX = (width - longLineLength) / 2 - textWidth / 2;
                float textY = y + textHeight / 3;
                paintText.setColor(MaterialColors.getColor(this, (i / 10) % 5 == 0 ? R.attr.colorOnSurface : R.attr.colorOutline));
                canvas.drawText(text, textX, textY, paintText);
            } else if (i % 5 == 0) {
                canvas.drawLine(width - midLineLength, y, width, y, paintSide);
            } else {
                canvas.drawLine(width - shortLineLength, y, width, y, paintSide);
            }
        }
    }

    private float dpToPx(Context context, float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }
}