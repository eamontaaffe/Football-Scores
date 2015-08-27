package barqsoft.footballscores.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.widget.RemoteViews;

import barqsoft.footballscores.R;

/**
 * Created by Eamon on 25/08/2015.
 */
public class StaticAppWidgetProvider extends AppWidgetProvider {
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // Perform this loop procedure for each Today widget
         for (int appWidgetId : appWidgetIds) {
             int layoutId = R.layout.static_widget;
             RemoteViews views = new RemoteViews(context.getPackageName(), layoutId);
             appWidgetManager.updateAppWidget(appWidgetId, views);
         }
    }
}
