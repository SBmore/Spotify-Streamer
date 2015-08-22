package app.com.example.android.spotifystreamer;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.List;

import kaaes.spotify.webapi.android.models.Image;

/**
 * Created by Steven on 22/08/2015.
 */
public class Utility {
    public static String findImageUrl(List<Image> images, int desiredHeight, int desiredWidth) {

        int closestHeight = 0;
        int closestWidth = 0;
        int index = 0;
        int height;
        int width;

        if (images.size() > 0) {
            // find closest size to the desired one
            for (int i = 0; i < images.size(); i++) {
                height = images.get(i).height;
                width = images.get(i).width;
                if (i == 0) {
                    // if this is the first picture then set it as the closest one
                    closestHeight = height;
                    closestWidth = width;
                    index = i;
                } else {
                    // otherwise look for the one closest to the size (equal to or larger than)
                    if ((closestHeight > height) && (height > desiredHeight) &&
                            (closestWidth > width) && (width > desiredWidth)) {
                        closestHeight = height;
                        closestWidth = width;
                        index = i;
                    }
                }
            }
            return images.get(index).url;
        } else {
            // return silhouette to indicate no image found
            return "http://i.imgur.com/0I2zfkf.png";
        }
    }

    // Source:  http://stackoverflow.com/questions/4238921/detect-whether-there-is-an-internet-connection-available-on-android
    // User:    Alexandre Jasmin
    // Changes: getActivity().getApplicationContext() to access getSystemService
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static String millisecondsToSeconds(int milliseconds) {
        double seconds = milliseconds / 1000;
        long minutes = (long) seconds / 60;  // casting from double to long to round down
        long remaining = (milliseconds / 1000) - (minutes * 60);
        String duration;

        if (remaining < 10) {
            duration = minutes + ":" + "0" + remaining;
        } else {
            duration = minutes + ":" + remaining;
        }

        return duration;
    }
}
