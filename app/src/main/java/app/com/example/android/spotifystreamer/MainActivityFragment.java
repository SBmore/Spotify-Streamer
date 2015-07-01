package app.com.example.android.spotifystreamer;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.io.IOError;
import java.util.ArrayList;
import java.util.Arrays;
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
    private ArrayAdapter<String> mArtistSearchAdapter;
    private String[] mArtistIDArray = new String[10];
    private TextWatcher mTextWatcher = new TextWatcher() {
        public void afterTextChanged(Editable s) {
            updateArtists();
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }
    };

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mArtistSearchAdapter = new ArrayAdapter<String>(
                getActivity(),
                R.layout.list_item_artist,
                R.id.list_item_artist_textview,
                new ArrayList<String>());

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mEditTest = (EditText) rootView.findViewById(R.id.editText_artist_search);
        mEditTest.addTextChangedListener(mTextWatcher);

        ListView listView = (ListView) rootView.findViewById(R.id.listview_artists);
        listView.setAdapter(mArtistSearchAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if(mArtistIDArray[i] != null && mArtistIDArray[i] != "") {
                    Intent intent = new Intent(getActivity(), top_ten_tracks.class);
                    intent.putExtra("artistName", mArtistSearchAdapter.getItem(i));
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
        updateArtists();
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

    public class FetchArtistsTask extends AsyncTask<String, Void, String[]> {

        @Override
        protected String[] doInBackground(String... params) {
            SpotifyApi api = new SpotifyApi();
            SpotifyService spotify = api.getService();
            ArtistsPager artistsGet;
            List<Artist> items;
            String[] artistArray = new String[10];

            if (params[0].length() == 0) {
                artistsGet = null;
            } else {
                artistsGet = spotify.searchArtists("artist:" + params[0] + "**");
            }

            if (artistsGet == null) {
                for (int i = 0; i < artistArray.length; i++) {
                    artistArray[i] = "";
                }
            } else {
                items = artistsGet.artists.items;
                for (int i = 0; i < artistArray.length; i++) {
                    if (i < items.size()) {
                        artistArray[i] = items.get(i).name;
                        mArtistIDArray[i] = items.get(i).id;
                    } else {
                        artistArray[i] = "";
                    }
                }
            }

            return artistArray;
        }

        @Override
        protected void onPostExecute(String[] strings) {
            if (strings != null) {
                List<String> topArtistResults = new ArrayList<>(Arrays.asList(strings));
                mArtistSearchAdapter.clear();
                mArtistSearchAdapter.addAll(topArtistResults);
            }
        }
    }
}