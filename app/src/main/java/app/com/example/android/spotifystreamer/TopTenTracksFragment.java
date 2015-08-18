package app.com.example.android.spotifystreamer;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOError;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;


/**
 * A placeholder fragment containing a simple view.
 */
public class TopTenTracksFragment extends Fragment {
    private final String LOG_TAG = FetchTopTenTask.class.getSimpleName();
    static final String TOP_TEN_DATA_KEY = "ARTIST";  // key for intent and parcelable
    static final String TOP_TEN_SAVE_KEY = "TRACKS";  // key for savedInstanceState

    private SpotifyListDataAdapter mTopTenAdapter;
    private ArrayList<SpotifyListData> mSpotifyArrayList = new ArrayList<>();
    private SpotifyListData[] mSpotifyDataArray = new SpotifyListData[10];
    private String mArtist;
    private int mStackLevel = 0;

    public TopTenTracksFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (savedInstanceState != null && savedInstanceState.containsKey(TOP_TEN_SAVE_KEY)) {
            mSpotifyArrayList = savedInstanceState.getParcelableArrayList(TOP_TEN_SAVE_KEY);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(TOP_TEN_SAVE_KEY, mSpotifyArrayList);
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null) {
            mArtist = arguments.getString(TOP_TEN_DATA_KEY);
        } else {
            mArtist = getActivity().getIntent().getStringExtra(TOP_TEN_DATA_KEY);
        }

        mTopTenAdapter = new SpotifyListDataAdapter(getActivity(), mSpotifyArrayList);

        View rootView = inflater.inflate(R.layout.fragment_top_ten_tracks, container, false);

        ListView tracksListView = (ListView) rootView.findViewById(R.id.listView_top_ten);
        tracksListView.setAdapter(mTopTenAdapter);

        // Put data into an Intent and send it over to the Top Ten Tracks when
        // the user clicks on an item in the listView
        tracksListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                showDialog();
            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        // only update if it's empty, otherwise let the parcelable handle it
        if (mSpotifyArrayList != null && mArtist != null) {
            updateTracks();
        }
    }

    public void updateTracks() {
        try {
            new FetchTopTenTask().execute(mArtist);
        } catch (IOError e) {
            Log.e(LOG_TAG, "Error ", e);
        }
    }

    public class FetchTopTenTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            SpotifyApi api = new SpotifyApi();
            SpotifyService spotify = api.getService();
            Tracks topTracks = new Tracks();
            List<Track> items;

            // getArtistTopTrack requires geo data
            Map<String, Object> options = new HashMap<>();
            options.put("country", "GB");

            // Check if there is an internet connection to avoid a Fatal Exception
            if (isNetworkAvailable()) {
                topTracks = spotify.getArtistTopTrack(params[0], options);
            }

            // Three possible outcomes: No connection | No results | Results Found
            if (topTracks.tracks != null) {
                items = topTracks.tracks;

                if (items.size() == 0) {
                    return "empty";
                } else {
                    for (int i = 0; i < mSpotifyDataArray.length; i++) {
                        if (i < items.size()) {
                            String name = items.get(i).name;
                            FindImageClosestSize finder = new FindImageClosestSize();
                            String image = finder.findImageUrl(items.get(i).album.images, 200, 200);
                            String detail = items.get(i).album.name;

                            mSpotifyDataArray[i] = new SpotifyListData(name, detail, image, "tracks", "");
                        } else {
                            mSpotifyDataArray[i] = new SpotifyListData("", "", "", "tracks", "");
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
                // Track data has been found. Update the list and tell the adaptor things have changed
                mSpotifyArrayList.clear();
                for (int i = 0; i < mSpotifyDataArray.length; i++) {
                    mSpotifyArrayList.add(mSpotifyDataArray[i]);
                }
                mTopTenAdapter.notifyDataSetChanged();
            } else {
                // No track Data, prepare a toast to explain the problem to the user
                int duration = Toast.LENGTH_SHORT;
                Context context = getActivity().getApplication().getApplicationContext();
                CharSequence text = "There was an unknown problem.";

                if (data == "empty") {
                    text = "No tracks found for this artist.";
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

    void showDialog() {
        mStackLevel++;

        // DialogFragment.show() will take care of adding the fragment
        // in a transaction.  We also want to remove any currently showing
        // dialog, so make our own transaction and take care of that here.
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        NowPlayingDialogFragment nowPlaying = NowPlayingDialogFragment.newInstance(mStackLevel);
        nowPlaying.show(ft, "dialog");
    }
}
