package widget.android.upir.pl.widgetapp;

import android.app.ActivityManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.net.Uri;
import android.widget.RemoteViews;

import java.lang.reflect.Field;
import java.util.Random;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link AppTestWidgetConfigureActivity AppTestWidgetConfigureActivity}
 */
public class AppTestWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        CharSequence widgetText = AppTestWidgetConfigureActivity.loadTitlePref(context, appWidgetId);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.app_test_widget);
        views.setTextViewText(R.id.appwidget_text, widgetText);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setData(Uri.parse("http://www.google.com"));
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.button1, pendingIntent);
        views.setImageViewResource(R.id.image_view,R.mipmap.ic_launcher);

        //ComponentName watchWidget = new ComponentName(context, AppTestWidget.class);

        //views.setOnClickPendingIntent(R.id.button_image, getPendingSelfIntent(context, "image_change"));
        //views.setOnClickPendingIntent(R.id.button_audio, getPendingSelfIntent(context, "music_play"));
        //appWidgetManager.updateAppWidget(watchWidget, views);



        Intent intent1 = new Intent(context, AppTestWidget.class);
        intent1.setAction("image_change");
        pendingIntent = PendingIntent.getBroadcast(context, 0, intent1, 0);
        views.setOnClickPendingIntent(R.id.button_image, pendingIntent);
        views.setImageViewResource(R.id.image_view, R.mipmap.ic_launcher);
        intent1.setAction("music_play");
        pendingIntent = PendingIntent.getBroadcast(context, 0, intent1, 0);
        views.setOnClickPendingIntent(R.id.button_audio, pendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        // Проверяем, что это intent от нажатия на третью зону
        if (intent.getAction().equalsIgnoreCase("image_change")) {

            // извлекаем ID экземпляра
            int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

            //RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.app_test_widget);

            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

            RemoteViews remoteViews;
            ComponentName watchWidget;

            remoteViews = new RemoteViews(context.getPackageName(), R.layout.app_test_widget);
            watchWidget = new ComponentName(context, AppTestWidget.class);

            //all from drawable
            /*final Class drawableClass = R.drawable.class;
            final Field[] fields = drawableClass.getFields();

            final Random rand = new Random();
            int rndInt = rand.nextInt(fields.length);
            try {
                int resID = fields[rndInt].getInt(drawableClass);
                remoteViews.setImageViewResource(R.id.image_view, resID);
            } catch (Exception e) {
                e.printStackTrace();
            }*/

            TypedArray images = context.getResources().obtainTypedArray(R.array.loading_images);
            int choice = (int) (Math.random() * images.length());
            remoteViews.setImageViewResource(R.id.image_view, images.getResourceId(choice, R.drawable.example_appwidget_preview));
            images.recycle();


            // Обновляем виджет
            appWidgetManager.updateAppWidget(watchWidget, remoteViews);
                /*updateAppWidget(context, AppWidgetManager.getInstance(context),
                        mAppWidgetId);*/
        } else if (intent.getAction().equalsIgnoreCase("music_play")) {
            if (isMusicServiceRunning(context))
                context.stopService(new Intent(context, MusicService.class));
            else
                context.startService(new Intent(context, MusicService.class));
        }
    }

    protected PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, context.getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    private boolean isMusicServiceRunning(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (MusicService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
        //CharSequence widgetText = AppTestWidgetConfigureActivity.loadTitlePref(context, appWidgetIds[0]);
        // Construct the RemoteViews object
        /*RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.app_test_widget);
        views.setTextViewText(R.id.appwidget_text, widgetText);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setData(Uri.parse("http://www.google.com"));
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.button1, pendingIntent);
        views.setImageViewResource(R.id.image_view, R.mipmap.ic_launcher);
        ComponentName watchWidget;

        watchWidget = new ComponentName(context, AppTestWidget.class);

        views.setOnClickPendingIntent(R.id.button_image, getPendingSelfIntent(context, "image_change"));
        views.setOnClickPendingIntent(R.id.button_audio, getPendingSelfIntent(context, "music_play"));
        appWidgetManager.updateAppWidget(watchWidget, views);*/
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        for (int appWidgetId : appWidgetIds) {
            AppTestWidgetConfigureActivity.deleteTitlePref(context, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

