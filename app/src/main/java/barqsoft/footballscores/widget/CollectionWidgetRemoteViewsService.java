package barqsoft.footballscores.widget;

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.text.SimpleDateFormat;
import java.util.Date;

import barqsoft.footballscores.DatabaseContract;
import barqsoft.footballscores.R;
import barqsoft.footballscores.Utilies;

/**
 * Created by Eamon on 28/08/2015.
 */
public class CollectionWidgetRemoteViewsService extends RemoteViewsService {

    private static final int COL_HOME = 3;
    private static final int COL_AWAY = 4;
    private static final int COL_HOME_GOALS = 6;
    private static final int COL_AWAY_GOALS = 7;
    private static final int COL_DATE = 1;
    private static final int COL_LEAGUE = 5;
    private static final int COL_MATCHDAY = 9;
    private static final int COL_ID = 8;
    private static final int COL_MATCHTIME = 2;

    private final String LOG_TAG = CollectionWidgetRemoteViewsService.class.getSimpleName();


    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            Cursor data;
            @Override
            public void onCreate() {

            }

            @Override
            public void onDataSetChanged() {
                if (data != null) {
                    data.close();
                }

                // Get matches that are on today
                Uri scoreWithDateUri = DatabaseContract.scores_table.buildScoreWithDate();

                Date date = new Date(System.currentTimeMillis()+((0)*86400000));
                SimpleDateFormat mformat = new SimpleDateFormat("yyyy-MM-dd");

                String[] dateArray = {mformat.format(date)};

                data = getContentResolver().query(scoreWithDateUri, null, "ASC",dateArray, null);
            }

            @Override
            public void onDestroy() {
                if (data != null) {
                    data.close();
                    data = null;
                }
            }

            @Override
            public int getCount() {
                return data == null ? 0 : data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int i) {
                Log.v(LOG_TAG,"getViewAt: " + i);
                // position will always range from 0 to getCount() - 1.
                // We construct a remote views item based on our widget item xml file, and set the
                // text based on the position.
                RemoteViews rv = new RemoteViews(getPackageName(), R.layout.widget_collection_list_item);

                if (data == null) {
                    Log.v(LOG_TAG, "data is null");
                    return null;
                }
                if (!data.moveToPosition(i)) {
                    Log.v(LOG_TAG, "data cannot move to position: " + i);
                    data.close();
                    return null;
                }

                rv.setTextViewText(R.id.home_name, data.getString(COL_HOME));
                rv.setTextViewText(R.id.away_name, data.getString(COL_AWAY));
                rv.setTextViewText(R.id.data_textview, data.getString(COL_MATCHTIME));
                rv.setTextViewText(R.id.score_textview, Utilies.getScores(data.getInt(COL_HOME_GOALS), data.getInt(COL_AWAY_GOALS)));
                rv.setImageViewResource(R.id.home_crest, Utilies.getTeamCrestByTeamName(
                        data.getString(COL_HOME)));
                rv.setImageViewResource(R.id.away_crest, Utilies.getTeamCrestByTeamName(
                        data.getString(COL_AWAY)));

                return rv;
            }

            @Override
            public RemoteViews getLoadingView() {
                return null;
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int i) {
                return i;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}
