package com.jaygietl.android.spotifystreamer;

/**
 * Created by jgietl on 6/17/15.
 */
public class SpotifyArtistDetail {

    public String artistName;
    public String imageUrl;
    public String spotifyId;

    public SpotifyArtistDetail( String artistName, String imageUrl, String spotifyId ) {
        this.artistName = artistName;
        this.imageUrl = imageUrl;
        this.spotifyId = spotifyId;
    }
}
