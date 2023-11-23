package com.yangdai.calc.main.calculator;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;


/**
 * @author 30415
 */
public class MyScrollListView extends ListView {
    private float preY = 0;

    public MyScrollListView(Context context) {
        super(context);
    }

    public MyScrollListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyScrollListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MyScrollListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN -> {
                preY = ev.getY();
                getParent().requestDisallowInterceptTouchEvent(true);
            }
            case MotionEvent.ACTION_MOVE ->
                    getParent().requestDisallowInterceptTouchEvent(!slideToTheBottom(ev));
            case MotionEvent.ACTION_UP -> getParent().requestDisallowInterceptTouchEvent(false);
            default -> {}
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 最后一个可见item为全部item的最后一个，且手势向上滑动，且全部露出
     */
    private boolean slideToTheBottom(MotionEvent ev) {
        if (getCount() == 0) {
            return true;
        }
        return getLastVisiblePosition() == getCount() - 1
//                && getChildAt(getChildCount() - 1).getBottom() == getHeight()
                && ev.getY() - preY < 0;
    }
}