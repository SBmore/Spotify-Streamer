package app.com.example.android.spotifystreamer;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

/**
 * Created by Steven on 18/08/2015.
 */
public class NowPlayingDialogFragment extends DialogFragment {
    private MediaPlayer mMediaPlayer = new MediaPlayer();
    private static int mSelectedSong;
    private static final String NOW_PLAYING_SAVE_KEY = "NOW_PLAYING_SAVE_KEY";
    private static SpotifyListData[] mSpotifyDataArray = new SpotifyListData[10];
    private static ViewHolder mViewHolder;
    private static String mArtistName;
    private static boolean mStartAudio;
    public BroadcastReceiver mReceiver;
    private int mSongDuration;

    static NowPlayingDialogFragment newInstance(SpotifyListData[] data, int i, String artistName,
                                                boolean startAudio) {
        NowPlayingDialogFragment f = new NowPlayingDialogFragment();

        mSpotifyDataArray = data;
        mSelectedSong = i;
        mArtistName = artistName;
        mStartAudio = startAudio;

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        int style;
        int theme;

        IntentFilter filter = new IntentFilter();
        filter.addAction(MusicService.UPDATE_UI_FILTER);
        mReceiver = new MediaUpdateReceiver();
        getActivity().registerReceiver(mReceiver, filter);

        style = DialogFragment.STYLE_NORMAL;
        theme = android.R.style.Theme_Holo_Light_Dialog;

        setStyle(style, theme);

        // don't restart the music if the screen has turned or the user selects same song
        if (savedInstanceState == null) {
            if (mStartAudio) {
                prepareAudio(MusicService.START_SONG_FILTER);
            }
        } else {
            mSongDuration = savedInstanceState.getInt(NOW_PLAYING_SAVE_KEY);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(NOW_PLAYING_SAVE_KEY, mSongDuration);
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_now_playing_dialog, container, false);

        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mViewHolder = new ViewHolder(v);
        final Toast[] toast = new Toast[1];

        prepareUI();

        ImageButton prevTrackView = mViewHolder.previousButtonView;
        ImageButton pausePlayView = mViewHolder.playPauseButtonView;
        ImageButton nextTrackView = mViewHolder.nextButtonView;
        final SeekBar seekBar = mViewHolder.trackSeekBar;

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int newPosition;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Change the elapsed counter dynamically as the user scrubs
                newPosition = mSongDuration / 100 * progress;
                TextView progressView = mViewHolder.trackProgressView;
                progressView.setText(Utility.millisecondsToSeconds(newPosition));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Remove the handler callback so the seekbar doesn't skip away from the user
                Intent intent = new Intent(getActivity(), MusicService.class);
                intent.setAction("");
                intent.putExtra(MusicService.REMOVE_CALLBACK, true);
                getActivity().startService(intent);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Call the service to skip the track to where the user moved the seek bar to
                Intent intent = new Intent(getActivity(), MusicService.class);
                intent.setAction(MusicService.SEEK_FILTER);
                intent.putExtra(MusicService.UPDATE_CURRENT_POS, newPosition);
                intent.putExtra(MusicService.ADD_CALLBACK, true);

                getActivity().startService(intent);
            }
        });

        prevTrackView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Skip song or show toast if no songs come before this one
                if (mSelectedSong > 0) {
                    mSelectedSong = mSelectedSong - 1;
                    prepareAudio(MusicService.PREVIOUS_FILTER);
                    prepareUI();
                } else {
                    String text = "There is no previous track to play.";
                    if (toast[0] != null) {
                        toast[0].cancel();
                    }
                    toast[0] = Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT);
                    toast[0].show();
                }
            }
        });
        pausePlayView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prepareAudio(MusicService.PLAY_PAUSE_FILTER);
            }
        });
        nextTrackView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Skip song or show toast if no songs come after this one
                if (mSelectedSong < mSpotifyDataArray.length - 1) {
                    mSelectedSong = mSelectedSong + 1;
                    prepareAudio(MusicService.NEXT_FILTER);
                    prepareUI();
                } else {
                    String text = "There are no further tracks to play.";
                    if (toast[0] != null) {
                        toast[0].cancel();
                    }
                    toast[0] = Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT);
                    toast[0].show();
                }
            }
        });
        return v;
    }

    private void prepareAudio(String filter) {
        if (Utility.isNetworkAvailable(getActivity())) {
            Intent intent = new Intent(getActivity(), MusicService.class);
            intent.setAction(filter);

            String url = mSpotifyDataArray[mSelectedSong].nextContentLink;
            intent.putExtra(MusicService.UPDATE_URL, url);

            getActivity().startService(intent);
        } else {
            String text = "No connection found.\nPlease connect to the internet!";
            Toast toast = Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private void prepareUI() {
        String albumName = mSpotifyDataArray[mSelectedSong].spotifyDataDetail;
        String trackName = mSpotifyDataArray[mSelectedSong].spotifyDataName;
        String albumImg = mSpotifyDataArray[mSelectedSong].spotifyLargeImage;

        TextView artistNameView = mViewHolder.artistNameView;
        artistNameView.setText(mArtistName);

        TextView albumView = mViewHolder.albumNameView;
        albumView.setText(albumName);

        TextView trackView = mViewHolder.trackNameView;
        trackView.setText(trackName);

        if (mSongDuration > 0) {
            TextView durationView = mViewHolder.trackDurationView;
            durationView.setText(Utility.millisecondsToSeconds(mSongDuration));
        }

        ImageView thumbnailView = mViewHolder.albumArtView;
        thumbnailView.setVisibility(View.VISIBLE);
        Picasso.with(getActivity()).load(albumImg).into(thumbnailView);
    }

    /**
     * The system calls this only when creating the layout in a dialog.
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mReceiver);
    }

    public static class ViewHolder {
        public final ImageView albumArtView;
        public final TextView artistNameView;
        public final TextView albumNameView;
        public final TextView trackNameView;
        public final SeekBar trackSeekBar;
        public final TextView trackProgressView;
        public final TextView trackDurationView;
        public final ImageButton previousButtonView;
        public final ImageButton playPauseButtonView;
        public final ImageButton nextButtonView;

        public ViewHolder(View view) {
            artistNameView = (TextView) view.findViewById(R.id.artist_name_textview);
            albumNameView = (TextView) view.findViewById(R.id.album_name_textview);
            albumArtView = (ImageView) view.findViewById(R.id.album_artwork_imageview);
            trackNameView = (TextView) view.findViewById(R.id.track_name_textview);
            trackSeekBar = (SeekBar) view.findViewById(R.id.track_seekbar);
            trackProgressView = (TextView) view.findViewById(R.id.track_progress_textview);
            trackDurationView = (TextView) view.findViewById(R.id.track_duration_textview);
            previousButtonView = (ImageButton) view.findViewById(R.id.previous_song_imageview);
            playPauseButtonView = (ImageButton) view.findViewById(R.id.pause_play_song_imageview);
            nextButtonView = (ImageButton) view.findViewById(R.id.next_song_imageview);
        }
    }

    public class MediaUpdateReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra(MusicService.UPDATE_DURATION)) {
                mSongDuration = intent.getIntExtra(MusicService.UPDATE_DURATION, 0);
                TextView durationView = mViewHolder.trackDurationView;
                durationView.setText(Utility.millisecondsToSeconds(mSongDuration));
            }
            if (intent.hasExtra(MusicService.UPDATE_CURRENT_POS)) {
                SeekBar seekBarView = mViewHolder.trackSeekBar;
                int pos = intent.getIntExtra(MusicService.UPDATE_CURRENT_POS, 0);
                seekBarView.setProgress(pos);
            }
            if (intent.hasExtra(MusicService.UPDATE_PROGRESS)) {
                TextView progressView = mViewHolder.trackProgressView;
                int prog = intent.getIntExtra(MusicService.UPDATE_PROGRESS, 0);
                progressView.setText(Utility.millisecondsToSeconds(prog));
            }
            if (intent.hasExtra(MusicService.IS_PAUSED)) {
                ImageButton playPauseButtonView = mViewHolder.playPauseButtonView;
                Boolean isPaused = intent.getBooleanExtra(MusicService.IS_PAUSED, false);
                if (isPaused) {
                    playPauseButtonView.setImageResource(R.drawable.ic_media_play);
                } else {
                    playPauseButtonView.setImageResource(R.drawable.ic_media_pause);
                }
            }
        }
    }
}
