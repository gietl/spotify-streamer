package com.jaygietl.android.spotifystreamer.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by jgietl on 6/17/15.
 */
public class SpotifyArtist implements Parcelable {

    public String artistName;
    public String imageUrl;
    public String spotifyId;

    public SpotifyArtist(String artistName, String imageUrl, String spotifyId) {
        this.artistName = artistName;
        this.imageUrl = imageUrl;
        this.spotifyId = spotifyId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(artistName);
        dest.writeString(imageUrl);
        dest.writeString(spotifyId);
    }


    public static final Parcelable.Creator<SpotifyArtist> CREATOR
            = new Parcelable.Creator<SpotifyArtist>() {
        public SpotifyArtist createFromParcel(Parcel in) {
            return new SpotifyArtist(in);
        }

        public SpotifyArtist[] newArray(int size) {
            return new SpotifyArtist[size];
        }
    };

    private SpotifyArtist(Parcel in) {
        this.artistName = in.readString();
        this.imageUrl = in.readString();
        this.spotifyId = in.readString();
    }
}
