package com.example.lightappcollege;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName; // Добавлено
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri; // Добавлено
import android.os.AsyncTask; // AsyncTask больше не нужен для парсинга здесь
import android.widget.RemoteViews;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List; // List больше не нужен здесь
import java.util.Locale;

public class ScheduleWidgetProvider extends AppWidgetProvider {

    private static final String PREFS_NAME = "SchedulePrefs";
    private static final String KEY_GROUP_NUMBER = "groupNumber";
    private static final String WIDGET_REFRESH_ACTION = "com.example.lightappcollege.WIDGET_REFRESH_ACTION";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_schedule_layout);

        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String savedGroup = prefs.getString(KEY_GROUP_NUMBER, "");

        views.setTextViewText(R.id.widget_group_title, "Расписание для группы: " + (savedGroup.isEmpty() ? "Не выбрана" : savedGroup));

        // Устанавливаем адаптер для ListView через RemoteViewsService
        Intent serviceIntent = new Intent(context, LessonWidgetService.class);
        serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        serviceIntent.setData(Uri.parse(serviceIntent.toUri(Intent.URI_INTENT_SCHEME))); // Важно для уникальности Intent
        views.setRemoteAdapter(R.id.widget_schedule_list, serviceIntent);

        // Устанавливаем пустой вид для ListView (отображается, если список пуст)
        views.setEmptyView(R.id.widget_schedule_list, R.id.widget_empty_view);


        // Настраиваем кнопку обновления
        Intent refreshIntent = new Intent(context, ScheduleWidgetProvider.class);
        refreshIntent.setAction(WIDGET_REFRESH_ACTION);
        refreshIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, appWidgetId, refreshIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        views.setOnClickPendingIntent(R.id.widget_refresh_button, pendingIntent);

        // Обновляем время последнего обновления
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        String currentTime = sdf.format(new Date());
        views.setTextViewText(R.id.widget_last_updated, "Обновлено: " + currentTime);

        appWidgetManager.updateAppWidget(appWidgetId, views);

        // Уведомляем ListView, что данные могли измениться, чтобы он запросил их у сервиса
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.widget_schedule_list);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (WIDGET_REFRESH_ACTION.equals(intent.getAction())) {
            int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
            if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
                // Обновляем виджет и запрашиваем обновление данных у RemoteViewsService
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.widget_schedule_list);
                Toast.makeText(context, "Обновление расписания...", Toast.LENGTH_SHORT).show();

                // Обновляем текст "Обновлено: " сразу же
                RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_schedule_layout);
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                String currentTime = sdf.format(new Date());
                views.setTextViewText(R.id.widget_last_updated, "Обновлено: " + currentTime);
                appWidgetManager.partiallyUpdateAppWidget(appWidgetId, views); // Используем partiallyUpdate, чтобы не сбрасывать список
            }
        }
    }

    // Если нужна какая-то очистка при удалении виджета
    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // Очистите кэш или другие данные, если они привязаны к конкретному виджету
    }
}