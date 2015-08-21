package app.com.example.android.spotifystreamer;

import android.app.Dialog;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;

/**
* Created by Steven on 18/08/2015.
*/
public class NowPlayingDialogFragment extends DialogFragment {
    static final String NOW_PLAYING_DATA_KEY = "TRACK";  // key for intent and parcelable
    private String mTrack;
    private MediaPlayer mMediaPlayer = new MediaPlayer();

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
        setHasOptionsMenu(true);
        int style;
        int theme;


        style = DialogFragment.STYLE_NO_TITLE;
        theme = android.R.style.Theme_Holo_Light_Dialog;

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

        String url = "https://p.scdn.co/mp3-preview/323627f4a6a5da9705eae7bd6197ad662a5a502f"; // your URL here
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mMediaPlayer.setDataSource(url);
        } catch (IOException e) {

        }
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
            }
        });

        mMediaPlayer.prepareAsync();

        ImageView thumbnailView = (ImageView) v.findViewById(R.id.album_artwork_imageview);
        thumbnailView.setVisibility(View.VISIBLE);
        Picasso.with(getActivity()).load("http://www.michaelbuble.com/sites/g/files/g2000002856/f/styles/album_thumbnail/public/201304/mb.jpg?itok=hbxhuTQA").into(thumbnailView);

        return v;
    }

    /** The system calls this only when creating the layout in a dialog. */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        boolean isTablet = getActivity().getResources().getBoolean(R.bool.tablet);
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        if (!isTablet) {
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        }
        return dialog;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mMediaPlayer.stop();
        mMediaPlayer.release();
    }

    public static class ViewHolder {
        public final ImageView albumArtView;
        public final TextView artistNameView;
        public final TextView albumNameView;
        public final TextView trackNameView;
        public final TextView trackProgressView;
        public final TextView trackLengthView;
        public final ImageView previousButtonView;
        public final ImageView playPauseButtonView;
        public final ImageView nextButtonView;

        public ViewHolder(View view) {
            artistNameView = (TextView) view.findViewById(R.id.artist_name_textview);
            albumNameView = (TextView) view.findViewById(R.id.album_name_textview);
            trackNameView = (TextView) view.findViewById(R.id.track_name_textview);
            albumArtView = (ImageView) view.findViewById(R.id.album_artwork_imageview);
            trackProgressView = (TextView) view.findViewById(R.id.track_progress_textview);
            trackLengthView = (TextView) view.findViewById(R.id.track_length_textview);
            previousButtonView = (ImageView) view.findViewById(R.id.previous_song_imageview);
            playPauseButtonView = (ImageView) view.findViewById(R.id.pause_play_song_imageview);
            nextButtonView = (ImageView) view.findViewById(R.id.next_song_imageview);
        }
    }
}
