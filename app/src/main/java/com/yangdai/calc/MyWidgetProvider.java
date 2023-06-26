package com.yangdai.calc;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.yangdai.calc.compass.Compass;
import com.yangdai.calc.converter.UnitActivity;
import com.yangdai.calc.exchange.ExchangeActivity;
import com.yangdai.calc.finance.FinanceActivity;
import com.yangdai.calc.time.DateRangeActivity;

/**
 * @author 30415
 */
public class MyWidgetProvider extends AppWidgetProvider {
    private static final String ACTION_BUTTON_1 = "com.yangdai.calc.MainActivity";
    private static final String ACTION_BUTTON_2 = "com.yangdai.calc.converter.UnitActivity";
    private static final String ACTION_BUTTON_3 = "com.yangdai.calc.exchange.ExchangeActivity";
    private static final String ACTION_BUTTON_4 = "com.yangdai.calc.time.DateRangeActivity";
    private static final String ACTION_BUTTON_5 = "com.yangdai.calc.DiscountActivity";
    private static final String ACTION_BUTTON_6 = "com.yangdai.calc.compass.Compass";
    private static final String ACTION_BUTTON_7 = "com.yangdai.calc.finance.FinanceActivity";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            @SuppressLint("RemoteViewLayout")
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

            // 注册监听
            Intent button1Intent = new Intent(context, MainActivity.class);
            PendingIntent button1PendingIntent =
                    PendingIntent.getActivity(context, 0, button1Intent, PendingIntent.FLAG_IMMUTABLE);
            views.setOnClickPendingIntent(R.id.button1, button1PendingIntent);

            Intent button2Intent = new Intent(context, UnitActivity.class);
            PendingIntent button2PendingIntent =
                    PendingIntent.getActivity(context, 0, button2Intent, PendingIntent.FLAG_IMMUTABLE);
            views.setOnClickPendingIntent(R.id.button2, button2PendingIntent);

            Intent button3Intent = new Intent(context, ExchangeActivity.class);
            PendingIntent button3PendingIntent =
                    PendingIntent.getActivity(context, 0, button3Intent, PendingIntent.FLAG_IMMUTABLE);
            views.setOnClickPendingIntent(R.id.button3, button3PendingIntent);


            Intent button4Intent = new Intent(context, DateRangeActivity.class);
            PendingIntent button4PendingIntent =
                    PendingIntent.getActivity(context, 0, button4Intent, PendingIntent.FLAG_IMMUTABLE);
            views.setOnClickPendingIntent(R.id.button4, button4PendingIntent);

            Intent button5Intent = new Intent(context, DiscountActivity.class);
            PendingIntent button5PendingIntent =
                    PendingIntent.getActivity(context, 0, button5Intent, PendingIntent.FLAG_IMMUTABLE);
            views.setOnClickPendingIntent(R.id.button5, button5PendingIntent);

            Intent button6Intent = new Intent(context, Compass.class);
            PendingIntent button6PendingIntent =
                    PendingIntent.getActivity(context, 0, button6Intent, PendingIntent.FLAG_IMMUTABLE);
            views.setOnClickPendingIntent(R.id.button6, button6PendingIntent);

            Intent button7Intent = new Intent(context, FinanceActivity.class);
            PendingIntent button7PendingIntent =
                    PendingIntent.getActivity(context, 0, button7Intent, PendingIntent.FLAG_IMMUTABLE);
            views.setOnClickPendingIntent(R.id.button7, button7PendingIntent);

            // 更新小组件
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        // 处理按钮点击事件
        if (ACTION_BUTTON_1.equals(intent.getAction())) {
            Intent button1Intent = new Intent(context, MainActivity.class);
            button1Intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(button1Intent);
        } else if (ACTION_BUTTON_2.equals(intent.getAction())) {
            Intent button2Intent = new Intent(context, UnitActivity.class);
            button2Intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(button2Intent);
        } else if (ACTION_BUTTON_3.equals(intent.getAction())) {
            Intent button3Intent = new Intent(context, ExchangeActivity.class);
            button3Intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(button3Intent);
        } else if (ACTION_BUTTON_4.equals(intent.getAction())) {
            Intent button4Intent = new Intent(context, DateRangeActivity.class);
            button4Intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(button4Intent);
        } else if (ACTION_BUTTON_5.equals(intent.getAction())) {
            Intent button5Intent = new Intent(context, DiscountActivity.class);
            button5Intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(button5Intent);
        } else if (ACTION_BUTTON_6.equals(intent.getAction())) {
            Intent button6Intent = new Intent(context, Compass.class);
            button6Intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(button6Intent);
        } else if (ACTION_BUTTON_7.equals(intent.getAction())) {
            Intent button7Intent = new Intent(context, FinanceActivity.class);
            button7Intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(button7Intent);
        }
    }
}
