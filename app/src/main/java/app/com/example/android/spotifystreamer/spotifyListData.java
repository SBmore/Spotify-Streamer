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
    String spotifyDataDetail;
    String spotifyDataImage;
    String spotifyID;
    String trackUrl;
    String callType;

    public SpotifyListData(String sName, String sDetail, String sImage, String cType, String ID,
                           String tUrl) {
        this.spotifyDataName = sName;
        this.spotifyDataDetail = sDetail;
        this.spotifyDataImage = sImage;
        this.callType = cType;
        this.trackUrl = tUrl;
        this.spotifyID = ID;
    }

    private SpotifyListData(Parcel in) {
        spotifyDataName = in.readString();
        spotifyDataDetail = in.readString();
        spotifyDataImage = in.readString();
        callType = in.readString();
        trackUrl = in.readString();
        spotifyID = in.readString();
    }

    @Override
    public int describeContents() { return 0; }

    public String toString() { return spotifyDataName + "--" + spotifyDataDetail + "--" +
            spotifyDataImage + "--" + callType + "--" + trackUrl  + "--" + spotifyID; }

    public void writeToParcel(Parcel parcel, int i)
    {
        parcel.writeString(spotifyDataName);
        parcel.writeString(spotifyDataDetail);
        parcel.writeString(spotifyDataImage);
        parcel.writeString(callType);
        parcel.writeString(trackUrl);
        parcel.writeString(spotifyID);
    }

    public final Parcelable.Creator<SpotifyListData> CREATOR = new Parcelable.Creator<SpotifyListData>() {
        @Override
        public SpotifyListData createFromParcel(Parcel parcel) { return new SpotifyListData(parcel); }

        @Override
        public SpotifyListData[] newArray(int i) { return new SpotifyListData[i]; }
    };
}
