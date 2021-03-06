package com.jaygietl.android.spotifystreamer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.jaygietl.android.spotifystreamer.R;
import com.jaygietl.android.spotifystreamer.model.SpotifyArtist;


public class SpotifyArtistDetail extends ActionBarActivity {

    private SpotifyArtist mArtist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spotify_artist_detail);

        Intent intent = getIntent();
        if( intent != null && intent.hasExtra("artist") ) {
            mArtist = intent.getParcelableExtra("artist");
            if( mArtist != null ) {

                ActionBar actionBar = getSupportActionBar();
                if( actionBar != null ) {
                    actionBar.setSubtitle(mArtist.artistName);
                }

            }

        }



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_spotify_artist_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
