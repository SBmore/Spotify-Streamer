package app.com.example.android.spotifystreamer;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
* Created by Steven on 18/08/2015.
*/
public class NowPlayingDialogFragment extends DialogFragment {
    static final String NOW_PLAYING_DATA_KEY = "TRACK";  // key for intent and parcelable
    private String mTrack;
    int mNum;

    /**
     * Create a new instance of MyDialogFragment, providing "num"
     * as an argument.
     */
    static NowPlayingDialogFragment newInstance(int num) {
        NowPlayingDialogFragment f = new NowPlayingDialogFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("num", num);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNum = getArguments().getInt("num");

        // Pick a style based on the num.
        int style = DialogFragment.STYLE_NO_TITLE;

        int theme;

//        theme = android.R.style.Theme_Holo;
        theme = android.R.style.Theme_Holo_Light_Dialog;
//        theme = android.R.style.Theme_Holo_Light;
//        theme = android.R.style.Theme_Holo_Light_Panel;
//        theme = android.R.style.Theme_Holo_Light;
        setStyle(style, theme);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_now_playing_dialog, container, false);
//        View tv = v.findViewById(R.id.text);
//        ((TextView)tv).setText("Dialog #" + mNum + ": using style "
//                + getNameForNum(mNum));
//
//        // Watch for button clicks.
//        Button button = (Button)v.findViewById(R.id.show);
//        button.setOnClickListener(new OnClickListener() {
//            public void onClick(View v) {
//                // When button is clicked, call up to owning activity.
//                ((FragmentDialog)getActivity()).showDialog();
//            }
//        });

        ImageView thumbnailView = (ImageView) v.findViewById(R.id.album_artwork_imageview);
        thumbnailView.setVisibility(View.VISIBLE);
        Picasso.with(getActivity()).load("http://www.michaelbuble.com/sites/g/files/g2000002856/f/styles/album_thumbnail/public/201304/mb.jpg?itok=hbxhuTQA").into(thumbnailView);

        return v;
    }
}
