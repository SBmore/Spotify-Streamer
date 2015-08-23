package app.com.example.android.spotifystreamer;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.io.IOException;

/**
 * Created by Steven on 23/08/2015.
 */
public class MusicService extends Service implements OnCompletionListener,
        OnPreparedListener, OnErrorListener, OnSeekCompleteListener {

    public static final String UPDATE_UI_FILTER = "UPDATE_UI_FILTER";
    public static final String START_SONG_FILTER = "START_SONG_FILTER";
    public static final String PLAY_PAUSE_FILTER = "PLAY_PAUSE_FILTER";
    public static final String NEXT_FILTER = "NEXT_FILTER";
    public static final String PREVIOUS_FILTER = "PREVIOUS_FILTER";
    public static final String SEEK_FILTER = "SEEK_FILTER";
    public static final String UPDATE_DURATION = "UPDATE_DURATION";
    public static final String UPDATE_PROGRESS = "UPDATE_PROGRESS";
    public static final String UPDATE_CURRENT_POS = "UPDATE_CURRENT_POS";
    public static final String UPDATE_URL = "UPDATE_URL";
    public static final String IS_PAUSED = "IS_PAUSED";
    public static final String REMOVE_CALLBACK = "REMOVE_CALLBACK";
    public static final String ADD_CALLBACK = "ADD_CALLBACK";

    private MediaPlayer mMediaPlayer = new MediaPlayer();
    private Handler mHandler = new Handler();
    private boolean mHandlerActive = false;

    @Override
    public void onCreate() {
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnErrorListener(this);
        mMediaPlayer.setOnSeekCompleteListener(this);
        mMediaPlayer.reset();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            Intent outIntent = new Intent();
            outIntent.setAction(UPDATE_UI_FILTER);

            if (intent.getBooleanExtra(REMOVE_CALLBACK, false)) {
                mHandler.removeCallbacks(mUpdateUIProgressRunnable);
            } else if (intent.getBooleanExtra(ADD_CALLBACK, false)) {
                updateProgress();
            }

            switch (action) {
                case START_SONG_FILTER:
                    playHandler(intent.getStringExtra(UPDATE_URL));
                    outIntent.putExtra(IS_PAUSED, false);
                    sendBroadcast(outIntent);
                    break;
                case PLAY_PAUSE_FILTER:
                    if (mMediaPlayer.isPlaying()) {
                        outIntent.putExtra(IS_PAUSED, true);
                        mMediaPlayer.pause();
                    } else {
                        outIntent.putExtra(IS_PAUSED, false);
                        if (mMediaPlayer.getDuration() > 0) {
                            mMediaPlayer.start();
                        } else {
                            playHandler(intent.getStringExtra(UPDATE_URL));
                        }
                    }
                    sendBroadcast(outIntent);
                    break;
                case NEXT_FILTER:
                    mHandler.removeCallbacks(mUpdateUIProgressRunnable);
                    playHandler(intent.getStringExtra(UPDATE_URL));
                    break;
                case PREVIOUS_FILTER:
                    mHandler.removeCallbacks(mUpdateUIProgressRunnable);
                    playHandler(intent.getStringExtra(UPDATE_URL));
                    break;
                case SEEK_FILTER:
                    mMediaPlayer.seekTo(intent.getIntExtra(UPDATE_CURRENT_POS, 0));
                    break;
            }
        }
        return START_STICKY;
    }

    public void playHandler(String url) {
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
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mMediaPlayer != null) {
            mHandler.removeCallbacks(mUpdateUIProgressRunnable);
            mHandlerActive = false;
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
            }
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    // When the track reaches the end set the play/pause to play and reset the seekbar
    // TODO: If there's time it would be nice for it to move to the next track
    @Override
    public void onCompletion(MediaPlayer mp) {
        mHandler.removeCallbacks(mUpdateUIProgressRunnable);

        Intent intent = new Intent();
        intent.setAction(UPDATE_UI_FILTER);
        intent.putExtra(IS_PAUSED, true);
        intent.putExtra(UPDATE_PROGRESS, 0);
        intent.putExtra(UPDATE_CURRENT_POS, 0);

        sendBroadcast(intent);
        stopSelf();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        updateProgress();

        Intent intent = new Intent();
        intent.setAction(UPDATE_UI_FILTER);
        intent.putExtra(UPDATE_DURATION, mMediaPlayer.getDuration());
        intent.putExtra(UPDATE_PROGRESS, mMediaPlayer.getCurrentPosition());
        intent.putExtra(IS_PAUSED, false);

        sendBroadcast(intent);
        mp.start();
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {
        if (!mp.isPlaying()) {
            mp.start();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private Runnable mUpdateUIProgressRunnable = new Runnable() {
        @Override
        public void run() {
            double currentPosition = mMediaPlayer.getCurrentPosition();
            double trackLength = mMediaPlayer.getDuration();
            long progress = (long) ((100 / trackLength) * currentPosition);

            Intent intent = new Intent();
            intent.setAction(UPDATE_UI_FILTER);
            intent.putExtra(UPDATE_CURRENT_POS, (int) progress);
            intent.putExtra(UPDATE_PROGRESS, (int) currentPosition);

            sendBroadcast(intent);

            mHandler.postDelayed(this, 100);
        }
    };

    public void updateProgress() {
        mHandlerActive = true;
        mHandler.postDelayed(mUpdateUIProgressRunnable, 100);
    }
}
