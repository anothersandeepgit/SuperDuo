package barqsoft.footballscores;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import barqsoft.footballscores.service.myFetchService;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainScreenFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,
        SharedPreferences.OnSharedPreferenceChangeListener
{
    public scoresAdapter mAdapter;
    public static final int SCORES_LOADER = 0;
    private String[] fragmentdate = new String[1];
    private int last_selected_item = -1;

    public MainScreenFragment()
    {
    }

    private void update_scores()
    {
        Intent service_start = new Intent(getActivity(), myFetchService.class);
        getActivity().startService(service_start);
    }
    public void setFragmentDate(String date)
    {
        fragmentdate[0] = date;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        update_scores();
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        final ListView score_list = (ListView) rootView.findViewById(R.id.scores_list);
        View emptyView = rootView.findViewById(R.id.empty_score_list);
        score_list.setEmptyView(emptyView);
        mAdapter = new scoresAdapter(getActivity(),null,0);
        score_list.setAdapter(mAdapter);
        getLoaderManager().initLoader(SCORES_LOADER,null,this);
        mAdapter.detail_match_id = MainActivity.selected_match_id;
        score_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ViewHolder selected = (ViewHolder) view.getTag();
                mAdapter.detail_match_id = selected.match_id;
                MainActivity.selected_match_id = (int) selected.match_id;
                mAdapter.notifyDataSetChanged();
            }
        });
        return rootView;
    }

    @Override
    public void onResume() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sp.registerOnSharedPreferenceChangeListener(this);
        super.onResume();
    }

    @Override
    public void onPause() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sp.unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle)
    {
        return new CursorLoader(getActivity(),DatabaseContract.scores_table.buildScoreWithDate(),
                null,null,fragmentdate,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor)
    {
        //Log.v(FetchScoreTask.LOG_TAG,"loader finished");
        //cursor.moveToFirst();
        /*
        while (!cursor.isAfterLast())
        {
            Log.v(FetchScoreTask.LOG_TAG,cursor.getString(1));
            cursor.moveToNext();
        }
        */

        int i = 0;
        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            i++;
            cursor.moveToNext();
        }
        //Log.v(FetchScoreTask.LOG_TAG,"Loader query: " + String.valueOf(i));
        mAdapter.swapCursor(cursor);
        //mAdapter.notifyDataSetChanged();
        updateEmptyView();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader)
    {
        mAdapter.swapCursor(null);
    }

    /*
        Updates the empty list view with contextually relevant information that the user can
        use to determine why they aren't seeing weather.
     */
    private void updateEmptyView() {
        if (mAdapter.getCount() == 0) {
            TextView tv = (TextView) getView().findViewById(R.id.empty_score_list);
            String contentDescription = "";
            if (null != tv) {
                // if cursor is empty, why? do we have an invalid location
                int message = R.string.empty_score_list;
                @myFetchService.ScoreStatus int scoreStatus = scoresAdapter.getScoreStatus(getActivity());
                switch (scoreStatus) {
                    case myFetchService.SCORE_STATUS_SERVER_DOWN:
                        message = R.string.empty_score_list_server_down;
                        contentDescription = getString(R.string.empty_score_list_server_down);
                        break;
                    case myFetchService.SCORE_STATUS_SERVER_INVALID:
                        message = R.string.empty_score_list_server_error;
                        contentDescription = getString(R.string.empty_score_list_server_error);
                        break;
                    default:
                        if (!Utilies.isNetworkAvailable(getActivity())) {
                            message = R.string.empty_score_list_no_network;
                            contentDescription = getString(R.string.empty_score_list_no_network);
                        }
                }
                tv.setText(message);
                tv.setContentDescription(contentDescription);
            }
        }
    }
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.e("MainScreenFragment", "in onSharedPreferenceChanged");
        if ( key.equals(getString(R.string.pref_score_list_status_key)) ) {
            updateEmptyView();
        }
    }
}
