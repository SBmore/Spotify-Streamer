package app.com.example.android.spotifystreamer;

import android.content.Context;
import android.content.Intent;
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
    private String[] mArtistIDArray = new String[10];
    private SpotifyListDataAdapter mArtistInfoAdapter;
    private ArrayList<SpotifyListData> mSpotifyArrayList = new ArrayList<>();

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mArtistInfoAdapter = new SpotifyListDataAdapter(getActivity(), mSpotifyArrayList);

        mEditTest = (EditText) rootView.findViewById(R.id.editText_artist_search);
        mEditTest.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    updateArtists();
                }
                return false;
            }
        });

        ListView listView = (ListView) rootView.findViewById(R.id.listview_artists);
        listView.setAdapter(mArtistInfoAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if (mArtistIDArray[i] != null && mArtistIDArray[i] != "") {
                    Intent intent = new Intent(getActivity(), TopTenTracks.class);
                    intent.putExtra("artistID", mArtistIDArray[i]);
                    startActivity(intent);
                }
            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public void updateArtists() {
        String query;
        try {
            query = mEditTest.getText().toString();
            new FetchArtistsTask().execute(query);
        } catch (IOError e) {
            Log.e(LOG_TAG, "Error ", e);
        }
    }

    public class FetchArtistsTask extends AsyncTask<String, Void, SpotifyListData[]> {

        @Override
        protected SpotifyListData[] doInBackground(String... params) {
            SpotifyApi api = new SpotifyApi();
            SpotifyService spotify = api.getService();
            SpotifyListData[] spotifyDataArray = new SpotifyListData[10];
            ArtistsPager artistsGet;
            List<Artist> items;
            String name;
            String detail = "";
            String image;

            artistsGet = spotify.searchArtists("artist:" + params[0] + "**");

            items = artistsGet.artists.items;

            if (items.size() == 0) {
                return null;
            } else {
                for (int i = 0; i < spotifyDataArray.length; i++) {
                    mArtistIDArray[i] = items.get(i).id;
                    name = items.get(i).name;
                    findImageClosestSize finder = new findImageClosestSize();
                    image = finder.findImageUrl(items.get(i).images, 200, 200);

                    spotifyDataArray[i] = new SpotifyListData(name, detail, image, "artist");
                }
                return spotifyDataArray;
            }
        }


        @Override
        protected void onPostExecute(SpotifyListData[] data) {
            if (data != null) {
                mSpotifyArrayList.clear();
                for (int i = 0; i < data.length; i++) {
                    mSpotifyArrayList.add(data[i]);
                }
                mArtistInfoAdapter.notifyDataSetChanged();
            } else {
                Context context = getActivity().getApplication().getApplicationContext();
                CharSequence text = "No results found.\nPlease refine your search!";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
        }
    }
}