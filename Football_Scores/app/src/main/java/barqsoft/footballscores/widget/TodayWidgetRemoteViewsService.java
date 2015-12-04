package barqsoft.footballscores.widget;

import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.os.Build;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.text.SimpleDateFormat;
import java.util.Date;

import barqsoft.footballscores.DatabaseContract;
import barqsoft.footballscores.R;
import barqsoft.footballscores.Utilies;

/**
 * Created by lendevsanadmin on 11/30/2015.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class TodayWidgetRemoteViewsService extends RemoteViewsService {

    public final String LOG_TAG = TodayWidgetRemoteViewsService.class.getSimpleName();
    private static final String[] SCORE_COLUMNS = {
            DatabaseContract.SCORES_TABLE + "." + DatabaseContract.scores_table._ID,
            DatabaseContract.scores_table.DATE_COL,
            DatabaseContract.scores_table.TIME_COL,
            DatabaseContract.scores_table.HOME_COL,
            DatabaseContract.scores_table.AWAY_COL,
            DatabaseContract.scores_table.HOME_GOALS_COL,
            DatabaseContract.scores_table.AWAY_GOALS_COL
    };
    // these indices must match the projection
    static final int INDEX_SCORE_ID = 0;
    static final int INDEX_SCORE_DATE = 1;
    static final int INDEX_SCORE_TIME = 2;
    static final int INDEX_SCORE_HOME = 3;
    static final int INDEX_SCORE_AWAY = 4;
    static final int INDEX_SCORE_HOME_GOALS = 5;
    static final int INDEX_SCORE_AWAY_GOALS = 6;


    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            private Cursor data = null;

            @Override
            public void onCreate() {
                // Nothing to do
            }

            @Override
            public void onDataSetChanged() {
                if (data != null) {
                    data.close();
                }
                // This method is called by the app hosting the widget (e.g., the launcher)
                // However, our ContentProvider is not exported so it doesn't have access to the
                // data. Therefore we need to clear (and finally restore) the calling identity so
                // that calls use our process and permission
                final long identityToken = Binder.clearCallingIdentity();
                Date currentDate = new Date(System.currentTimeMillis());
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String currentDateString = dateFormat.format(currentDate);
                //String selection = DatabaseContract.scores_table.DATE_COL + " = " + currentDateString;
                //String selection = DatabaseContract.scores_table.DATE_COL + " = " + "2015-11-30";
                //data = getContentResolver().query(DatabaseContract.scores_table.buildScoreWithDate(),
                data = getContentResolver().query(DatabaseContract.BASE_CONTENT_URI,
                     SCORE_COLUMNS, null, null,null);
                        //SCORE_COLUMNS, null, new String[] {currentDateString},null);
                        //SCORE_COLUMNS, null, new String[] {"2015-11-30"},null);
                Binder.restoreCallingIdentity(identityToken);
                Log.i(LOG_TAG, "************************data.getCount() = " + data.getCount());
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
            public RemoteViews getViewAt(int position) {
                Log.i(LOG_TAG, "************************position = " + position);
                if (position == AdapterView.INVALID_POSITION ||
                        data == null || !data.moveToPosition(position)) {
                    return null;
                }
                RemoteViews views = new RemoteViews(getPackageName(), R.layout.widget_score_list_item);

                String date = data.getString(INDEX_SCORE_DATE);
                String homeTeamName = data.getString(INDEX_SCORE_HOME);
                String awayTeamName = data.getString(INDEX_SCORE_AWAY);
                String matchTime = data.getString(INDEX_SCORE_TIME);
                String matchScore = Utilies.getScores(data.getInt(INDEX_SCORE_HOME_GOALS), data.getInt(INDEX_SCORE_AWAY_GOALS));
                long matchId = data.getLong(INDEX_SCORE_ID);
                Log.i(LOG_TAG, "************************VIEWS_VALUES = " + homeTeamName + ", " + awayTeamName + ", "  + matchTime + ", " + matchScore);
                views.setTextViewText(R.id.widget_date, date);
                views.setTextViewText(R.id.widget_team_one, homeTeamName);
                views.setTextViewText(R.id.widget_team_two, awayTeamName);
                views.setTextViewText(R.id.widget_time, matchTime);
                views.setTextViewText(R.id.widget_score, matchScore);

                return views;
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.widget_score_list_item);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                if (data.moveToPosition(position))
                    return data.getLong(INDEX_SCORE_ID);
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }

}
