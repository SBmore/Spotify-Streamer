package app.com.example.android.spotifystreamer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends ActionBarActivity implements ArtistSearchFragment.Callback {

    public static String PACKAGE_NAME;
    private final String TOP_TRACKS_FRAGMENT_TAG = "TTFTAG";
    public boolean mIsTablet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIsTablet = getResources().getBoolean(R.bool.tablet);
        PACKAGE_NAME = getApplicationContext().getPackageName();
        setContentView(R.layout.activity_main);

        if (mIsTablet) {
            // if top_ten_tracks_container exists then the device must be a tablet
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.top_ten_tracks_container, new TopTenTracksFragment(), TOP_TRACKS_FRAGMENT_TAG)
                        .commit();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);  // removed until there are options to click
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(String artistName, String artistID) {
        if (mIsTablet) {
            Bundle args = new Bundle();
            args.putString(TopTenTracksFragment.TOP_TEN_DATA_KEY_ID, artistID);
            args.putString(TopTenTracksFragment.TOP_TEN_DATA_KEY_NAME, artistName);

            TopTenTracksFragment fragment = new TopTenTracksFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.top_ten_tracks_container, fragment, TOP_TRACKS_FRAGMENT_TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this, TopTenTracksActivity.class);
            intent.putExtra(TopTenTracksFragment.TOP_TEN_DATA_KEY_ID, artistID);
            intent.putExtra(TopTenTracksFragment.TOP_TEN_DATA_KEY_NAME, artistName);
            startActivity(intent);
        }
    }
}
