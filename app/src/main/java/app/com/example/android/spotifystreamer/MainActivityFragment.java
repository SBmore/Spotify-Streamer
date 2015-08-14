package app.com.example.android.spotifystreamer;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOError;
import java.util.ArrayList;
import java.util.List;

import app.com.example.android.spotifystreamer.data.SpotifyContract;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    private final String LOG_TAG = FetchArtistsTask.class.getSimpleName();

    private EditText mEditTest;
    private SpotifyListDataAdapter mArtistInfoAdapter;
    private SpotifyListData[] mSpotifyDataArray = new SpotifyListData[10];
    private ArrayList<SpotifyListData> mSpotifyArrayList = new ArrayList<>();

    private final static int FORECAST_LOADER_ID = 0;
    private static final String[] FORECAST_COLUMNS = {
            SpotifyContract.TracksEntry.TABLE_NAME + "." + SpotifyContract.TracksEntry._ID,
            SpotifyContract.TracksEntry.COLUMN_ARTISTS_ID,
            SpotifyContract.TracksEntry.COLUMN_ARTISTS_NAME,
            SpotifyContract.TracksEntry.COLUMN_ARTISTS_IMAGE,
            SpotifyContract.TracksEntry.COLUMN_TRACKS_ID,
            SpotifyContract.TracksEntry.COLUMN_TRACKS_NAME,
            SpotifyContract.TracksEntry.COLUMN_ALBUM_NAME,
            SpotifyContract.TracksEntry.COLUMN_ALBUM_IMAGE,
    };

    // These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
    // must change.
    static final int COL_ROW_ID = 0;
    static final int COLUMN_ARTISTS_ID = 1;
    static final int COLUMN_ARTISTS_NAME = 2;
    static final int COLUMN_ARTISTS_IMAGE = 3;
    static final int COLUMN_TRACKS_ID = 4;
    static final int COLUMN_TRACKS_NAME = 5;
    static final int COLUMN_ALBUM_NAME = 6;
    static final int COLUMN_ALBUM_IMAGE = 7;


    public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.v(LOG_TAG, "onCreate");

        if (savedInstanceState != null && savedInstanceState.containsKey("artists")) {
            mSpotifyArrayList = savedInstanceState.getParcelableArrayList("artists");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("artists", mSpotifyArrayList);
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mArtistInfoAdapter = new SpotifyListDataAdapter(getActivity(), mSpotifyArrayList);

        ListView listView = (ListView) rootView.findViewById(R.id.listview_artists);
        listView.setAdapter(mArtistInfoAdapter);

        // Update the data when the user submits what they typed in the editText
        mEditTest = (EditText) rootView.findViewById(R.id.editText_artist_search);
        mEditTest.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                // after coming back from Top Ten Tracks the actionId changes from 6 to 5 for some peculiar reason
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
                    updateArtists();
                }
                return false;
            }
        });

        // Put data into an Intent and send it over to the Top Ten Tracks when
        // the user clicks on an item in the listView
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(i);
                if (cursor != null) {
                    String artistID = mSpotifyArrayList.get(i).spotifyID;
                    if (artistID != null && artistID != "") {
                        Intent intent = new Intent(getActivity(), TopTenTracksFragment.class)
                                .setData(SpotifyContract.TracksEntry.buildTopTenTracksFromArtistID(
                                                artistID)
                                );

                        startActivity(intent);
                    }
                }
            }
        });

        return rootView;
    }

    public void updateArtists() {
        try {
            String query = mEditTest.getText().toString();
            new FetchArtistsTask().execute(query);
        } catch (IOError e) {
            Log.e(LOG_TAG, "Error ", e);
        }
    }

    public class FetchArtistsTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            SpotifyApi api = new SpotifyApi();
            SpotifyService spotify = api.getService();
            ArtistsPager artistsGet = new ArtistsPager();
            List<Artist> items;

            // Check if there is an internet connection to avoid a Fatal Exception
            if (isNetworkAvailable()) {
                artistsGet = spotify.searchArtists("artist:" + params[0] + "**");
            }

            // Three possible outcomes: No connection | No results | Results Found
            if (artistsGet.artists != null) {
                items = artistsGet.artists.items;

                if (items.size() == 0) {
                    return "empty";
                } else {
                    for (int i = 0; i < mSpotifyDataArray.length; i++) {
                        if (i < items.size()) {
                            String name = items.get(i).name;
                            FindImageClosestSize finder = new FindImageClosestSize();
                            String image = finder.findImageUrl(items.get(i).images, 200, 200);
                            String artistID = items.get(i).id;

                            mSpotifyDataArray[i] = new SpotifyListData(name, "", image, "artist", artistID);
                        } else {
                            mSpotifyDataArray[i] = new SpotifyListData("", "", "", "artist", "");
                        }
                    }
                }
                return "success";
            } else {
                return "no connection";
            }
        }


        @Override
        protected void onPostExecute(String data) {
            if (data == "success") { 
                mSpotifyArrayList.clear();
                for (int i = 0; i < mSpotifyDataArray.length; i++) {
                    mSpotifyArrayList.add(mSpotifyDataArray[i]);
                }
                mArtistInfoAdapter.notifyDataSetChanged();
            } else {
                // No Artist Data, prepare a toast to explain the problem to the user
                int duration = Toast.LENGTH_SHORT;
                Context context = getActivity().getApplication().getApplicationContext();
                CharSequence text = "There was an unknown problem.";

                if (data == "empty"){
                    text = "No results found.\nPlease refine your search!";
                } else if (data == "no connection") {
                    text = "No connection found.\nPlease connect to the internet!";
                }

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
        }

        // Source:  http://stackoverflow.com/questions/4238921/detect-whether-there-is-an-internet-connection-available-on-android
        // User:    Alexandre Jasmin
        // Changes: getActivity().getApplicationContext() to access getSystemService
        private boolean isNetworkAvailable() {
            ConnectivityManager connectivityManager
                    = (ConnectivityManager) getActivity().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.v(LOG_TAG,"onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v(LOG_TAG,"onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.v(LOG_TAG,"onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.v(LOG_TAG,"onStop");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(LOG_TAG,"onDestroy");
    }
}