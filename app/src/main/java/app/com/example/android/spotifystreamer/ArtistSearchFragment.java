package app.com.example.android.spotifystreamer;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOError;
import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;


/**
 * A placeholder fragment containing a simple view.
 */
public class ArtistSearchFragment extends Fragment {
    private final String LOG_TAG = FetchArtistsTask.class.getSimpleName();

    private EditText mEditTest;
    private SpotifyListDataAdapter mArtistInfoAdapter;
    private SpotifyListData[] mSpotifyDataArray = new SpotifyListData[10];
    private ArrayList<SpotifyListData> mSpotifyArrayList = new ArrayList<>();

    public ArtistSearchFragment() {
    }

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        void onItemSelected(String artistName, String artistID);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        boolean isTablet = getResources().getBoolean(R.bool.tablet);
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mArtistInfoAdapter = new SpotifyListDataAdapter(getActivity(), mSpotifyArrayList);

        ListView artistListView = (ListView) rootView.findViewById(R.id.listview_artists);
        artistListView.setAdapter(mArtistInfoAdapter);

        if (isTablet) {
            artistListView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        } else {
            artistListView.setChoiceMode(AbsListView.CHOICE_MODE_NONE);
        }

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
        artistListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String artistID = mSpotifyArrayList.get(i).spotifyID;
                String artistName = mSpotifyArrayList.get(i).spotifyDataName;
                if (artistID != null && artistID != "") {
                    ((Callback) getActivity()).onItemSelected(artistName, artistID);
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
            if (Utility.isNetworkAvailable(getActivity())) {
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
                            String image = Utility.findImageUrl(items.get(i).images, 200, 200);
                            String artistID = items.get(i).id;

                            mSpotifyDataArray[i] = new SpotifyListData(name, "", image, "artist", artistID, "");
                        } else {
                            mSpotifyDataArray[i] = new SpotifyListData("", "", "", "artist", "", "");
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

                if (data == "empty") {
                    text = "No results found.\nPlease refine your search!";
                } else if (data == "no connection") {
                    text = "No connection found.\nPlease connect to the internet!";
                }

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
        }
    }
}