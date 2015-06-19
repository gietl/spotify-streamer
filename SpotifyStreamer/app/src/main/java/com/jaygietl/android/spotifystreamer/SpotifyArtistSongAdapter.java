package com.jaygietl.android.spotifystreamer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by jgietl on 6/18/15.
 */
public class SpotifyArtistSongAdapter extends ArrayAdapter<SpotifyArtistSong> {

    public SpotifyArtistSongAdapter(Context context, ArrayList<SpotifyArtistSong> artistSongs) {
        super(context, 0, artistSongs);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        SpotifyArtistSong artistSong = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_artist_song_result, parent, false);
        }

        ImageView artistImage = (ImageView) convertView.findViewById(R.id.list_item_artist_album_image_result_imageview);
        if( artistSong.albumArtSmall != null && !"".equals(artistSong.albumArtSmall) ) {
            Picasso.with(getContext()).load(artistSong.albumArtSmall).into(artistImage);
        }

        TextView songName = (TextView) convertView.findViewById(R.id.list_item_artist_song_name_result_textview);
        songName.setText(artistSong.songName);

        TextView albumName = (TextView) convertView.findViewById(R.id.list_item_artist_album_name_result_textview);
        albumName.setText(artistSong.albumName);

        return convertView;
    }
}
