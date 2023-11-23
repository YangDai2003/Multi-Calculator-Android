package com.yangdai.calc.features;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.yangdai.calc.R;
import com.yangdai.calc.main.toolbox.functions.algebra.StatisticsActivity;
import com.yangdai.calc.main.toolbox.functions.compass.Compass;
import com.yangdai.calc.main.toolbox.functions.converter.UnitActivity;
import com.yangdai.calc.main.toolbox.functions.currency.CurrencyActivity;
import com.yangdai.calc.main.toolbox.functions.finance.FinanceActivity;
import com.yangdai.calc.main.MainActivity;
import com.yangdai.calc.main.toolbox.functions.shopping.ShoppingActivity;
import com.yangdai.calc.main.toolbox.functions.time.DateRangeActivity;

/**
 * @author 30415
 */
public class MyWidgetProvider extends AppWidgetProvider {
    private static final String ACTION_BUTTON_0 = "com.yangdai.calc.main.MainActivity";
    private static final String ACTION_BUTTON_2 = "com.yangdai.calc.main.toolbox.functions.converter.UnitActivity";
    private static final String ACTION_BUTTON_3 = "com.yangdai.calc.exchange.CurrencyActivity";
    private static final String ACTION_BUTTON_4 = "com.yangdai.calc.main.toolbox.functions.time.DateRangeActivity";
    private static final String ACTION_BUTTON_5 = "com.yangdai.calc.main.toolbox.functions.shopping.ShoppingActivity";
    private static final String ACTION_BUTTON_6 = "com.yangdai.calc.main.toolbox.functions.compass.Compass";
    private static final String ACTION_BUTTON_7 = "com.yangdai.calc.main.toolbox.functions.finance.FinanceActivity";
    private static final String ACTION_BUTTON_1 = "com.yangdai.calc.main.toolbox.functions.algebra.StatisticsActivity";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

            setOnClickPendingIntent(context, views, R.id.calculator, MainActivity.class, ACTION_BUTTON_0);
            setOnClickPendingIntent(context, views, R.id.button1, StatisticsActivity.class, ACTION_BUTTON_1);
            setOnClickPendingIntent(context, views, R.id.button2, UnitActivity.class, ACTION_BUTTON_2);
            setOnClickPendingIntent(context, views, R.id.button3, CurrencyActivity.class, ACTION_BUTTON_3);
            setOnClickPendingIntent(context, views, R.id.button4, DateRangeActivity.class, ACTION_BUTTON_4);
            setOnClickPendingIntent(context, views, R.id.button5, ShoppingActivity.class, ACTION_BUTTON_5);
            setOnClickPendingIntent(context, views, R.id.button6, Compass.class, ACTION_BUTTON_6);
            setOnClickPendingIntent(context, views, R.id.button7, FinanceActivity.class, ACTION_BUTTON_7);

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if (ACTION_BUTTON_0.equals(intent.getAction())) {
            startNewTaskActivity(context, MainActivity.class);
        } else if (ACTION_BUTTON_1.equals(intent.getAction())) {
            startNewTaskActivity(context, StatisticsActivity.class);
        } else if (ACTION_BUTTON_2.equals(intent.getAction())) {
            startNewTaskActivity(context, UnitActivity.class);
        } else if (ACTION_BUTTON_3.equals(intent.getAction())) {
            startNewTaskActivity(context, CurrencyActivity.class);
        } else if (ACTION_BUTTON_4.equals(intent.getAction())) {
            startNewTaskActivity(context, DateRangeActivity.class);
        } else if (ACTION_BUTTON_5.equals(intent.getAction())) {
            startNewTaskActivity(context, ShoppingActivity.class);
        } else if (ACTION_BUTTON_6.equals(intent.getAction())) {
            startNewTaskActivity(context, Compass.class);
        } else if (ACTION_BUTTON_7.equals(intent.getAction())) {
            startNewTaskActivity(context, FinanceActivity.class);
        }
    }

    private void setOnClickPendingIntent(Context context, RemoteViews views, int viewId, Class<?> activityClass, String action) {
        Intent intent = new Intent(context, activityClass);
        intent.setAction(action);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        views.setOnClickPendingIntent(viewId, pendingIntent);
    }

    private void startNewTaskActivity(Context context, Class<?> activityClass) {
        Intent intent = new Intent(context, activityClass);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}