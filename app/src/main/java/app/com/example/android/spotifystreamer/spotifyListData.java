package app.com.example.android.spotifystreamer;

/**
 * Created by Steven on 04/07/2015.
 * Technique learned from (23:40):
 *   https://plus.google.com/events/chlh8qqr5q5grs1lajpqnvvql8k?authkey=CNXMrZuHsMWhNg
 */
public class SpotifyListData {
    String spotifyDataName;
    String spotifyDataDetail;
    String spotifyDataImage;
    String callType;

    public SpotifyListData(String sName, String sDetail, String sImage, String cType) {
        this.spotifyDataName = sName;
        this.spotifyDataDetail = sDetail;
        this.spotifyDataImage = sImage;
        this.callType = cType;
    }
}
