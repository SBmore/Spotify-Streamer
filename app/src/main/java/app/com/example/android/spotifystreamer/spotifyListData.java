package app.com.example.android.spotifystreamer;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Steven on 04/07/2015.
 * Parcelable technique learned from Udacity Webcast:
 *   https://plus.google.com/u/0/events/cfftk1qo4tjn7enecof6f9oes0o?authkey=CNu5uui-k5qAtQE
 */
public class SpotifyListData implements Parcelable {
    String spotifyDataName;
    String spotifyDataDetail = "";
    String spotifySmallImage;
    String spotifyLargeImage = "";
    String nextContentLink;
    String callType;

    public SpotifyListData(String sName, String sImage, String cType, String nContentLink) {
        this.spotifyDataName = sName;
        this.spotifySmallImage = sImage;
        this.callType = cType;
        this.nextContentLink = nContentLink;
    }

    public void setSpotifyDataDetail (String sDetail) {
        this.spotifyDataDetail = sDetail;
    }

    public void setSpotifyLargeImage (String lImage) {
        this.spotifyLargeImage = lImage;
    }

    private SpotifyListData(Parcel in) {
        spotifyDataName = in.readString();
        spotifyDataDetail = in.readString();
        spotifySmallImage = in.readString();
        spotifyLargeImage = in.readString();
        callType = in.readString();
        nextContentLink = in.readString();
    }

    @Override
    public int describeContents() { return 0; }

    public String toString() { return spotifyDataName + "--" + spotifyDataDetail + "--" +
            spotifySmallImage + "--" + spotifyLargeImage  + "--" + callType + "--" + nextContentLink; }

    public void writeToParcel(Parcel parcel, int i)
    {
        parcel.writeString(spotifyDataName);
        parcel.writeString(spotifyDataDetail);
        parcel.writeString(spotifySmallImage);
        parcel.writeString(spotifyLargeImage);
        parcel.writeString(callType);
        parcel.writeString(nextContentLink);
    }

    public final Parcelable.Creator<SpotifyListData> CREATOR = new Parcelable.Creator<SpotifyListData>() {
        @Override
        public SpotifyListData createFromParcel(Parcel parcel) { return new SpotifyListData(parcel); }

        @Override
        public SpotifyListData[] newArray(int i) { return new SpotifyListData[i]; }
    };
}
