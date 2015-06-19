package com.jaygietl.android.spotifystreamer;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by jgietl on 6/18/15.
 */
public class SpotifyArtistSong implements Parcelable {

    public String songName;
    public String albumName;
    public String albumArtLarge;
    public String albumArtSmall;
    public String previewURL;

    public SpotifyArtistSong( String songName, String albumName, String albumArtLarge, String albumArtSmall, String previewURL ) {
        this.songName = songName;
        this.albumName = albumName;
        this.albumArtLarge = albumArtLarge;
        this.albumArtSmall = albumArtSmall;
        this.previewURL = previewURL;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(songName);
        dest.writeString(albumName);
        dest.writeString(albumArtLarge);
        dest.writeString(albumArtSmall);
        dest.writeString(previewURL);
    }

    public static final Parcelable.Creator<SpotifyArtistSong> CREATOR
            = new Parcelable.Creator<SpotifyArtistSong>() {
        public SpotifyArtistSong createFromParcel(Parcel in) {
            return new SpotifyArtistSong(in);
        }

        public SpotifyArtistSong[] newArray(int size) {
            return new SpotifyArtistSong[size];
        }
    };

    private SpotifyArtistSong(Parcel in) {
        this.songName = in.readString();
        this.albumName = in.readString();
        this.albumArtLarge = in.readString();
        this.albumArtSmall = in.readString();
        this.previewURL = in.readString();
    }
}
