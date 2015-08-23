package app.com.example.android.spotifystreamer;

import android.app.Dialog;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
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

import java.io.IOException;

/**
 * Created by Steven on 18/08/2015.
 */
public class NowPlayingDialogFragment extends DialogFragment {
    private MediaPlayer mMediaPlayer = new MediaPlayer();
    private static int mSelectedSong;
    private static SpotifyListData[] mSpotifyDataArray = new SpotifyListData[10];
    private static ViewHolder mViewHolder;
    private static String mArtistName;
    private Handler mHandler = new Handler();
    private String mElapsed;
    private boolean mHandlerActive = false;

    static NowPlayingDialogFragment newInstance(SpotifyListData[] data, int i, String artistName) {
        NowPlayingDialogFragment f = new NowPlayingDialogFragment();

        mSpotifyDataArray = data;
        mSelectedSong = i;
        mArtistName = artistName;

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        int style;
        int theme;

        style = DialogFragment.STYLE_NORMAL;
        theme = android.R.style.Theme_Holo_Light_Dialog;

        setStyle(style, theme);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_now_playing_dialog, container, false);

        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mViewHolder = new ViewHolder(v);
        final Toast[] toast = new Toast[1];

        prepareAudio();
        prepareUI();

        ImageButton prevTrackView = mViewHolder.previousButtonView;
        ImageButton pausePlayView = mViewHolder.playPauseButtonView;
        ImageButton nextTrackView = mViewHolder.nextButtonView;
        final SeekBar seekBar = mViewHolder.trackSeekBar;

        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                String duration = Utility.millisecondsToSeconds(mMediaPlayer.getDuration());
                mElapsed = Utility.millisecondsToSeconds(mMediaPlayer.getCurrentPosition());

                TextView durationView = mViewHolder.trackDurationView;
                durationView.setText(duration);

                TextView progressView = mViewHolder.trackProgressView;
                progressView.setText(mElapsed);

                mViewHolder.playPauseButtonView.setImageResource(R.drawable.ic_media_pause);
                mp.start();
            }
        });

        mMediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
            @Override
            public void onSeekComplete(MediaPlayer mp) {
                if (!mp.isPlaying()) {
                    mp.start();
                    mViewHolder.playPauseButtonView.setImageResource(R.drawable.ic_media_pause);
                }
            }
        });

        // When the track reaches the end set the play/pause to play and reset the seekbar
        // TODO: If there's time it would be nice for it to move to the next track
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mViewHolder.playPauseButtonView.setImageResource(R.drawable.ic_media_play);
                seekBar.setProgress(0);
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int newPosition;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mHandler.removeCallbacks(mUpdateUIProgressRunnable);
                mHandlerActive = false;

                int trackLength = mMediaPlayer.getDuration();
                newPosition = trackLength / 100 * progress;

                mElapsed = Utility.millisecondsToSeconds(newPosition);

                TextView progressView = mViewHolder.trackProgressView;
                progressView.setText(mElapsed);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mMediaPlayer.seekTo(newPosition);
                updateProgress();
            }
        });

        prevTrackView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSelectedSong > 0) {
                    mSelectedSong = mSelectedSong - 1;
                    prepareAudio();
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
                if (mMediaPlayer.isPlaying()) {
                    mMediaPlayer.pause();
                    mViewHolder.playPauseButtonView.setImageResource(R.drawable.ic_media_play);
                } else {
                    if (!mHandlerActive) {
                        updateProgress();
                    }
                    mMediaPlayer.start();
                    mViewHolder.playPauseButtonView.setImageResource(R.drawable.ic_media_pause);
                }
            }
        });
        nextTrackView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSelectedSong < mSpotifyDataArray.length - 1) {
                    mSelectedSong = mSelectedSong + 1;
                    prepareAudio();
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

    private void prepareAudio() {
        String url = mSpotifyDataArray[mSelectedSong].nextContentLink;

        if (mHandlerActive) {
            mHandler.removeCallbacks(mUpdateUIProgressRunnable);
            mHandlerActive = false;
        }
        mMediaPlayer.reset();

        try {
            mMediaPlayer.setDataSource(url);
        } catch (IOException e) {

        }

        mMediaPlayer.prepareAsync();
        updateProgress();
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

        ImageView thumbnailView = mViewHolder.albumArtView;
        thumbnailView.setVisibility(View.VISIBLE);
        Picasso.with(getActivity()).load(albumImg).into(thumbnailView);
    }

    /**
     * The system calls this only when creating the layout in a dialog.
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        boolean isTablet = getActivity().getResources().getBoolean(R.bool.tablet);
//        Dialog dialog = super.onCreateDialog(savedInstanceState);
//        if (!isTablet) {
//            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        }
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mHandler.removeCallbacks(mUpdateUIProgressRunnable);
        mHandlerActive = false;
        mMediaPlayer.stop();
        mMediaPlayer.release();
        mMediaPlayer = null;
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

    private Runnable mUpdateUIProgressRunnable = new Runnable() {
        public void run() {
            double currentPosition = mMediaPlayer.getCurrentPosition();
            double trackLength = mMediaPlayer.getDuration();
            long progress = (long) ((100 / trackLength) * currentPosition);

            mViewHolder.trackProgressView.setText(Utility.millisecondsToSeconds((int) currentPosition));
            mViewHolder.trackSeekBar.setProgress((int) progress);

            mHandler.postDelayed(this, 100);
        }
    };

    public void updateProgress() {
        mHandlerActive = true;
        mHandler.postDelayed(mUpdateUIProgressRunnable, 100);
    }
}
