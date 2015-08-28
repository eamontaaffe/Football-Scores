package barqsoft.footballscores.widget;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import barqsoft.footballscores.DatabaseContract;
import barqsoft.footballscores.MainScreenFragment;
import barqsoft.footballscores.R;
import barqsoft.footballscores.service.myFetchService;

/**
 * Created by Eamon on 27/08/2015.
 */
public class StaticAppWidgetConfigure extends Activity {
    private static final int COL_HOME = 3;
    private static final int COL_AWAY = 4;
    private static final int COL_ID = 8;

    public static final int INVALID_MATCH_ID = -1;

    private static final String LOG_TAG = StaticAppWidgetConfigure.class.getSimpleName();

    private Spinner mSpinner;
    private List<String> mMatchList;
    private List<Integer> mIdList;
    private int mAppWidgetId;

    public StaticAppWidgetConfigure() {
        super();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if they press the back button.
        setResult(RESULT_CANCELED);

        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }
        // If they gave us an intent without the widget id, just bail.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }
        // Set the view layout resource to use.
        setContentView(R.layout.static_widget_configure);

        mSpinner = (Spinner) findViewById(R.id.select_match_spinner);

        final Button continueButton = (Button) findViewById(R.id.continue_button);

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Complete configuration activity
                Context context = getApplicationContext();

                // Push widget update to surface with newly set prefix
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                StaticAppWidgetProvider.updateAppWidgets(context,appWidgetManager);

                // Make sure we pass back the original appWidgetId
                Intent resultValue = new Intent();
                resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
                setResult(RESULT_OK, resultValue);
                finish();
            }
        });

        loadMatches();
    }


    private void loadMatches() {
        Uri scoreWithDateUri = DatabaseContract.scores_table.buildScoreWithDate();

        mMatchList = new ArrayList<>();
        mIdList = new ArrayList<>();

        for(int k = 0; k < 5; k ++) {
            Date date = new Date(System.currentTimeMillis()+((k-2)*86400000));
            SimpleDateFormat mformat = new SimpleDateFormat("yyyy-MM-dd");

            String[] dateArray = {mformat.format(date)};
            Cursor data = getContentResolver().query(scoreWithDateUri, null, "ASC",dateArray, null);

            data.moveToFirst();

            if (data == null) {
                continue;
            }
            if (!data.moveToFirst()) {
                data.close();
                continue;
            }

            while (!data.isAfterLast())
            {
                mMatchList.add(data.getString(COL_HOME) + " vs " + data.getString(COL_AWAY));
                mIdList.add(data.getInt(COL_ID));
                data.moveToNext();
            }
            data.close();
        }
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, mMatchList);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        mSpinner.setAdapter(adapter);

        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                int id = mIdList.get(pos);
                saveIdToSharedPrefs(getApplicationContext(), id, mAppWidgetId);
                return;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                int id = mIdList.get(0);
                saveIdToSharedPrefs(getApplicationContext(), id, mAppWidgetId);
                return;
            }
        });

    }

    static void saveIdToSharedPrefs(Context context, int id, int appWidgetId) {
        SharedPreferences.Editor editor =
                context.getSharedPreferences(
                        context.getString(R.string.widget_prefs_name),MODE_PRIVATE).edit();
        editor.putInt(context.getString(R.string.pref_match_id_widget_) + appWidgetId, id);
        editor.apply();
    }

    static int getIdFromSharedPrefs(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(
                context.getString(R.string.widget_prefs_name), MODE_PRIVATE);
        return prefs.getInt(
                context.getString(R.string.pref_match_id_widget_) + appWidgetId, INVALID_MATCH_ID);
    }
}
