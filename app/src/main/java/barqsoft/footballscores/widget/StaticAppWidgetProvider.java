package barqsoft.footballscores.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import barqsoft.footballscores.R;
import barqsoft.footballscores.service.myFetchService;

/**
 * Created by Eamon on 25/08/2015.
 */
public class StaticAppWidgetProvider extends AppWidgetProvider {
    public static final String ACTION_DATA_UPDATED = "ADU";
    private static final String LOG_TAG = StaticAppWidgetProvider.class.getSimpleName();
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        context.startService(new Intent(context, StaticWidgetIntentService.class));
    }

    public static void updateAppWidgets(Context context, AppWidgetManager appWidgetManager) {
        context.startService(new Intent(context, StaticWidgetIntentService.class));
    }



    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v(LOG_TAG,"onRecieve action: " + intent.getAction());
        super.onReceive(context, intent);
        if (myFetchService.ACTION_DATA_UPDATED.equals(intent.getAction())) {
            context.startService(new Intent(context, StaticWidgetIntentService.class));
        }
    }
}
