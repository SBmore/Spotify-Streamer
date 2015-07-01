package app.com.example.android.spotifystreamer;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.IOError;
import java.util.ArrayList;
import java.util.Arrays;
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
public class top_ten_tracksFragment extends Fragment {

    private final String LOG_TAG = FetchTopTenTask.class.getSimpleName();
    private ArrayAdapter<String> mTopTenSearchAdapter;
    private String mArtist;

    public top_ten_tracksFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mArtist = getActivity().getIntent().getStringExtra("artistID");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mTopTenSearchAdapter = new ArrayAdapter<String>(
                getActivity(),
                R.layout.list_item_top_ten,
                R.id.list_item_top_ten_textview,
                new ArrayList<String>());

        View rootView = inflater.inflate(R.layout.fragment_top_ten_tracks, container, false);

        ListView listView = (ListView) rootView.findViewById(R.id.listView_top_ten);
        listView.setAdapter(mTopTenSearchAdapter);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateTracks();
    }

    public void updateTracks() {
        try {
            new FetchTopTenTask().execute(mArtist);
        } catch (IOError e) {
            Log.e(LOG_TAG, "Error ", e);
        }
    }

    public class FetchTopTenTask extends AsyncTask<String, Void, String[]> {

        @Override
        protected String[] doInBackground(String... params) {
            SpotifyApi api = new SpotifyApi();
            SpotifyService spotify = api.getService();
            Tracks topTracks;
            List<Track> items;
            String[] trackList = new String[10];

            if (params[0].length() == 0) {
                topTracks = null;
            } else {
                // https://github.com/spotify/android-sdk/issues/130
                Map<String, Object> options = new HashMap<>();
                options.put("country", "GB");
                topTracks = spotify.getArtistTopTrack(params[0], options);
            }

            if (topTracks == null) {
                for (int i = 0; i < trackList.length; i++) {
                    trackList[i] = "";
                }
            } else {
                items = topTracks.tracks;
                for (int i = 0; i < trackList.length; i++) {
                    if (i < items.size()) {
                        trackList[i] = items.get(i).name + "\n" + items.get(i).album.name;
                    } else {
                        trackList[i] = "";
                    }
                }
            }

            return trackList;
        }

        @Override
        protected void onPostExecute(String[] strings) {
            if (strings != null) {
                List<String> topArtistResults = new ArrayList<>(Arrays.asList(strings));
                mTopTenSearchAdapter.clear();
                mTopTenSearchAdapter.addAll(topArtistResults);
            }
        }
    }
}
