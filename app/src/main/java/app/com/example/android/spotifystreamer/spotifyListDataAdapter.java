package app.com.example.android.spotifystreamer;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Steven on 04/07/2015.
 * Technique learned from Udacity Webcast (25:15):
 * https://plus.google.com/events/chlh8qqr5q5grs1lajpqnvvql8k?authkey=CNXMrZuHsMWhNg
 */
public class SpotifyListDataAdapter extends ArrayAdapter<SpotifyListData> {

    public SpotifyListDataAdapter(Activity context, List<SpotifyListData> spotifyData) {
        super(context, 0, spotifyData);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SpotifyListData spotifyData = getItem(position);

        int layout = 0;
        int name = 0;
        int image = 0;
        Integer detail = 0;

        // get the layout and ID ints specific to the two fragments
        if (spotifyData.callType == "artist") {
            layout = R.layout.list_item_artist_info;
            name = R.id.artist_name_textview;
            image = R.id.artist_thumbnail_ImageView;
        } else if (spotifyData.callType == "tracks") {
            layout = R.layout.list_item_top_tracks_info;
            name = R.id.track_name_textview;
            detail = R.id.track_detail_textView;
            image = R.id.track_thumbnail_ImageView;
        }

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(layout, parent, false);
        }

        TextView nameView = (TextView) convertView.findViewById(name);
        nameView.setText(spotifyData.spotifyDataName);

        // detail is only relevant to the 'top ten tracks' call
        if (!detail.equals(0)) {
            TextView detailView = (TextView) convertView.findViewById(detail);
            detailView.setText(spotifyData.spotifyDataDetail);
        }

        ImageView thumbnailView = (ImageView) convertView.findViewById(image);

        // ingore images for empty rows in the list view
        if (spotifyData.spotifyDataImage != "") {
            thumbnailView.setVisibility(View.VISIBLE);
            Picasso.with(getContext()).load(spotifyData.spotifyDataImage).into(thumbnailView);
        } else {
            // discourage phantom pictures on empty rows
            thumbnailView.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }
}
