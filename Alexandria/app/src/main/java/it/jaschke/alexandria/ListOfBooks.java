package it.jaschke.alexandria;

import android.app.Activity;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import it.jaschke.alexandria.api.BookListAdapter;
import it.jaschke.alexandria.api.Callback;
import it.jaschke.alexandria.data.AlexandriaContract;
import it.jaschke.alexandria.services.BookService;


public class ListOfBooks extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,
        SharedPreferences.OnSharedPreferenceChangeListener{

    private BookListAdapter bookListAdapter;
    private ListView bookList;
    private int position = ListView.INVALID_POSITION;
    private EditText searchText;

    private final int LOADER_ID = 10;

    public ListOfBooks() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Cursor cursor = getActivity().getContentResolver().query(
                AlexandriaContract.BookEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );


        bookListAdapter = new BookListAdapter(getActivity(), cursor, 0);
        View rootView = inflater.inflate(R.layout.fragment_list_of_books, container, false);
        searchText = (EditText) rootView.findViewById(R.id.searchText);
        rootView.findViewById(R.id.searchButton).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ListOfBooks.this.restartLoader();
                    }
                }
        );

        bookList = (ListView) rootView.findViewById(R.id.listOfBooks);

        View emptyView = rootView.findViewById(R.id.empty_book_list);
        bookList.setEmptyView(emptyView);

        bookList.setAdapter(bookListAdapter);

        bookList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Cursor cursor = bookListAdapter.getCursor();
                if (cursor != null && cursor.moveToPosition(position)) {
                    ((Callback)getActivity())
                            .onItemSelected(cursor.getString(cursor.getColumnIndex(AlexandriaContract.BookEntry._ID)));
                }
            }
        });

        return rootView;
    }

    private void restartLoader(){
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        final String selection = AlexandriaContract.BookEntry.TITLE +" LIKE ? OR " + AlexandriaContract.BookEntry.SUBTITLE + " LIKE ? ";
        String searchString =searchText.getText().toString();

        if(searchString.length()>0){
            searchString = "%"+searchString+"%";
            return new CursorLoader(
                    getActivity(),
                    AlexandriaContract.BookEntry.CONTENT_URI,
                    null,
                    selection,
                    new String[]{searchString,searchString},
                    null
            );
        }

        return new CursorLoader(
                getActivity(),
                AlexandriaContract.BookEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        bookListAdapter.swapCursor(data);
        if (position != ListView.INVALID_POSITION) {
            bookList.smoothScrollToPosition(position);
        }
        updateEmptyView();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        bookListAdapter.swapCursor(null);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        activity.setTitle(R.string.books);
    }

    /*
    Updates the empty list view with contextually relevant information that the user can
    use to determine why they aren't seeing weather.
 */
    private void updateEmptyView() {
        if (bookListAdapter.getCount() == 0) {
            TextView tv = (TextView) getView().findViewById(R.id.empty_book_list);
            String contentDescription = "";
            if (null != tv) {
                // if cursor is empty, why? do we have an invalid location
                int message = R.string.empty_book_list;
                @BookService.BookStatus int bookStatus = bookListAdapter.getBookStatus(getActivity());
                switch (bookStatus) {
                    case BookService.BOOK_STATUS_SERVER_DOWN:
                        message = R.string.empty_book_list_server_down;
                        contentDescription = getString(R.string.empty_book_list_server_down);
                        break;
                    case BookService.BOOK_STATUS_SERVER_INVALID:
                        message = R.string.empty_book_list_server_error;
                        contentDescription = getString(R.string.empty_book_list_server_error);
                        break;
                    default:
                        if (!Utilities.isNetworkAvailable(getActivity())) {
                            message = R.string.empty_book_list_no_network;
                            contentDescription = getString(R.string.empty_book_list_no_network);
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
        if ( key.equals(getString(R.string.pref_book_list_status_key)) ) {
            updateEmptyView();
        }
    }
}
