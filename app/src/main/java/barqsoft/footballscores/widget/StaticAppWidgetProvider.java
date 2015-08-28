package barqsoft.footballscores.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import barqsoft.footballscores.R;

/**
 * Created by Eamon on 25/08/2015.
 */
public class StaticAppWidgetProvider extends AppWidgetProvider {
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        context.startService(new Intent(context, StaticWidgetIntentService.class));
    }

    public static void updateAppWidgets(Context context, AppWidgetManager appWidgetManager) {
        context.startService(new Intent(context, StaticWidgetIntentService.class));
    }
}
