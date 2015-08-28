package barqsoft.footballscores.widget;

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.text.SimpleDateFormat;
import java.util.Date;

import barqsoft.footballscores.DatabaseContract;
import barqsoft.footballscores.R;

/**
 * Created by Eamon on 28/08/2015.
 */
public class CollectionWidgetRemoteViewsService extends RemoteViewsService {
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
                // position will always range from 0 to getCount() - 1.
                // We construct a remote views item based on our widget item xml file, and set the
                // text based on the position.
                RemoteViews rv = new RemoteViews(getPackageName(), R.layout.widget_collection_list_item);

                rv.setTextViewText(R.id.widget_item, mWidgetItems.get(position).text);
                //TODO finish RemoteViewFactory
            }

            @Override
            public RemoteViews getLoadingView() {
                return null;
            }

            @Override
            public int getViewTypeCount() {
                return 0;
            }

            @Override
            public long getItemId(int i) {
                return 0;
            }

            @Override
            public boolean hasStableIds() {
                return false;
            }
        };
    }
}
