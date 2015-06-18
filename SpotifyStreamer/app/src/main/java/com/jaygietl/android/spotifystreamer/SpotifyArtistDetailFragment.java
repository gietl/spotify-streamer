package com.jaygietl.android.spotifystreamer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A placeholder fragment containing a simple view.
 */
public class SpotifyArtistDetailFragment extends Fragment {

    private String mArtistIdStr;

    public SpotifyArtistDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Intent intent = getActivity().getIntent();
        View rootView = inflater.inflate(R.layout.fragment_spotify_artist_detail, container, false);
        if( intent != null && intent.hasExtra("ARTIST_ID") && intent.hasExtra("ARTIST_NAME") ) {
            mArtistIdStr = intent.getStringExtra("ARTIST_ID");
            ((TextView) rootView.findViewById(R.id.detail_text))
                    .setText(mArtistIdStr);

        }

        return rootView;
    }
}
