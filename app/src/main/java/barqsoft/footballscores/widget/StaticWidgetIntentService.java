package barqsoft.footballscores.widget;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import barqsoft.footballscores.DatabaseContract;
import barqsoft.footballscores.R;
import barqsoft.footballscores.Utilies;

/**
 * Created by Eamon on 27/08/2015.
 */
public class StaticWidgetIntentService extends IntentService {
    public static final String LOG_TAG = StaticWidgetIntentService.class.getSimpleName();

    private static final int COL_HOME = 3;
    private static final int COL_AWAY = 4;
    private static final int COL_HOME_GOALS = 6;
    private static final int COL_AWAY_GOALS = 7;
    private static final int COL_DATE = 1;
    private static final int COL_LEAGUE = 5;
    private static final int COL_MATCHDAY = 9;
    private static final int COL_ID = 8;
    private static final int COL_MATCHTIME = 2;

    public StaticWidgetIntentService() {
        super("StaticWidgetIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // Retrieve all of the Today widget ids: these are the widgets we need to update
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this,
                StaticAppWidgetProvider.class));

        // Perform this loop procedure for each Today widget
        for (int appWidgetId : appWidgetIds) {
            int layoutId = R.layout.static_widget;
            String[] matchId = {Integer.toString(
                    StaticAppWidgetConfigure.getIdFromSharedPrefs(this, appWidgetId))};
            Log.v(LOG_TAG,"matchId for "+ appWidgetId + ": " + matchId);

            RemoteViews views = new RemoteViews(getPackageName(), layoutId);

            Uri scoreWithDateUri = DatabaseContract.scores_table.buildScoreWithId();

            Cursor data = getContentResolver().query(scoreWithDateUri, null, "ASC",matchId, null);

            Log.v(LOG_TAG,"data columns: " + data.getColumnCount() );

            if (data == null) {
                Log.v(LOG_TAG, "data is null");
                continue;
            }
            if (!data.moveToFirst()) {
                Log.v(LOG_TAG, "data cannot move to first");
                data.close();
                continue;
            }

            Log.v(LOG_TAG,"home_name: " + data.getString(COL_HOME));
            views.setTextViewText(R.id.home_name, data.getString(COL_HOME));
            views.setTextViewText(R.id.away_name,data.getString(COL_AWAY));
            views.setTextViewText(R.id.data_textview,data.getString(COL_MATCHTIME));
            views.setTextViewText(R.id.score_textview, Utilies.getScores(data.getInt(COL_HOME_GOALS), data.getInt(COL_AWAY_GOALS)));
            views.setImageViewResource(R.id.home_crest, Utilies.getTeamCrestByTeamName(
                    data.getString(COL_HOME)));
            views.setImageViewResource(R.id.away_crest, Utilies.getTeamCrestByTeamName(
                    data.getString(COL_AWAY)));

            data.close();
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}
