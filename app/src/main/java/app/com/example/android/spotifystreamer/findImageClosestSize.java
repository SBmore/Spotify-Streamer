package app.com.example.android.spotifystreamer;

import java.util.List;

import kaaes.spotify.webapi.android.models.Image;

/**
 * Created by Steven on 04/07/2015.
 */
public class findImageClosestSize {

    public String findImageUrl(List<Image> images, int desiredHeight, int desiredWidth) {

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
}
