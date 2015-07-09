package app.com.example.android.spotifystreamer;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

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
    private SpotifyListDataAdapter mTopTenAdapter;
    private ArrayList<SpotifyListData> mSpotifyArrayList = new ArrayList<>();
    private String mArtist;

    public TopTenTracksFragment() {
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

        mTopTenAdapter = new SpotifyListDataAdapter(getActivity(), mSpotifyArrayList);

        View rootView = inflater.inflate(R.layout.fragment_top_ten_tracks, container, false);

        ListView listView = (ListView) rootView.findViewById(R.id.listView_top_ten);
        listView.setAdapter(mTopTenAdapter);

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

    public class FetchTopTenTask extends AsyncTask<String, Void, SpotifyListData[]> {

        @Override
        protected SpotifyListData[] doInBackground(String... params) {
            SpotifyApi api = new SpotifyApi();
            SpotifyService spotify = api.getService();
            SpotifyListData[] spotifyDataArray = new SpotifyListData[10];
            Tracks topTracks;
            List<Track> items;
            String name;
            String detail;
            String image;

            if (params[0].length() == 0) {
                for (int i = 0; i < spotifyDataArray.length; i++) {
                    spotifyDataArray[i] = new SpotifyListData("", "", "", "");
                }
            } else {
                // https://github.com/spotify/android-sdk/issues/130
                Map<String, Object> options = new HashMap<>();
                options.put("country", "GB");
                topTracks = spotify.getArtistTopTrack(params[0], options);

                items = topTracks.tracks;
                for (int i = 0; i < spotifyDataArray.length; i++) {
                    if (i < items.size()) {
                        name = items.get(i).name;
                        detail = items.get(i).album.name;
                        findImageClosestSize finder = new findImageClosestSize();
                        image = finder.findImageUrl(items.get(i).album.images, 200, 200);
                        spotifyDataArray[i] = new SpotifyListData(name, detail, image, "tracks");
                    } else {
                        spotifyDataArray[i] = new SpotifyListData("", "", "", "");
                    }
                }
            }
            return spotifyDataArray;
        }

        @Override
        protected void onPostExecute(SpotifyListData[] data) {
            if (data[0] != null) {
                mSpotifyArrayList.clear();
                for (int i = 0; i < data.length; i++) {
                    mSpotifyArrayList.add(data[i]);
                }
                mTopTenAdapter.notifyDataSetChanged();
            }
        }
    }
}
