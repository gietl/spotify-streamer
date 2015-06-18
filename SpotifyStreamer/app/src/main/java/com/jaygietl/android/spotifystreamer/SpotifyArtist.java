package com.jaygietl.android.spotifystreamer;

/**
 * Created by jgietl on 6/17/15.
 */
public class SpotifyArtist {

    public String artistName;
    public String imageUrl;
    public String spotifyId;

    public SpotifyArtist(String artistName, String imageUrl, String spotifyId) {
        this.artistName = artistName;
        this.imageUrl = imageUrl;
        this.spotifyId = spotifyId;
    }
}
