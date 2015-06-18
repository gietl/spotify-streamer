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
 * Created by jgietl on 6/17/15.
 */
public class SpotifyArtistAdapter extends ArrayAdapter<SpotifyArtist> {
    public SpotifyArtistAdapter(Context context, ArrayList<SpotifyArtist> artistDetails) {
        super(context, 0, artistDetails);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        SpotifyArtist artistDetail = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_artist_result, parent, false);
        }

        ImageView artistImage = (ImageView) convertView.findViewById(R.id.list_item_artist_image_result_imageview);
        if( artistDetail.imageUrl != null && !"".equals(artistDetail.imageUrl) ) {
            Picasso.with(getContext()).load(artistDetail.imageUrl).into(artistImage);
        }

        TextView artistName = (TextView) convertView.findViewById(R.id.list_item_artist_name_result_textview);
        artistName.setText(artistDetail.artistName);

        return convertView;

    }
}
