package com.jaygietl.android.spotifystreamer.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.jaygietl.android.spotifystreamer.R;
import com.jaygietl.android.spotifystreamer.model.SpotifyArtist;
import com.jaygietl.android.spotifystreamer.model.SpotifyArtistSong;
import com.jaygietl.android.spotifystreamer.adapter.SpotifyArtistSongAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.AlbumSimple;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;
import retrofit.RetrofitError;


/**
 * A placeholder fragment containing a simple view.
 */
public class SpotifyArtistDetailFragment extends Fragment {

    private SpotifyArtist mArtist;
    private ArrayList<SpotifyArtistSong> artistSongs;
    private SpotifyArtistSongAdapter mArtistSongAdapter;

    public SpotifyArtistDetailFragment() {
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);

        outState.putParcelableArrayList("artist_songs", artistSongs);
        outState.putParcelable("artist", mArtist);

        //getToast("onSaveInstanceState");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Intent intent = getActivity().getIntent();
        View rootView = inflater.inflate(R.layout.fragment_spotify_artist_detail, container, false);

        if( intent != null && intent.hasExtra("artist") ) {

            mArtist = intent.getParcelableExtra("artist");

            doArtistTopSongSearch();
        }

        if( savedInstanceState != null ) {

            if( savedInstanceState .getParcelableArrayList("artist_songs") != null ) {
                artistSongs = savedInstanceState.getParcelableArrayList("artist_songs");
            }
            if( savedInstanceState .getParcelable("artist") != null ) {
                mArtist =  savedInstanceState.getParcelable("artist");
            }


        }

        setupAdapter();

        ListView listView = (ListView) rootView.findViewById(R.id.list_view_artist_song_result);
        listView.setAdapter(mArtistSongAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                SpotifyArtistSong artistSong = mArtistSongAdapter.getItem(position);

                getToast(artistSong.previewURL);

                /*Intent intent = new Intent(getActivity(), SpotifyArtistDetail.class)
                        .putExtra("artist", artist);
                //.putExtra("ARTIST_ID", artist.spotifyId);

                startActivity(intent);*/

            }
        });

        return rootView;
    }

    private void setupAdapter() {

        if( artistSongs == null ) {
            artistSongs = new ArrayList<>();
        }

        mArtistSongAdapter = new SpotifyArtistSongAdapter(
                getActivity(), artistSongs
        );
    }

    private void doArtistTopSongSearch() {
        //Log.v("[EditText]", mEdit.getText().toString());
        FetchArtistTopSongTask fetchArtistTopSongTask = new FetchArtistTopSongTask();
        String searchIdString = mArtist.spotifyId;
        fetchArtistTopSongTask.execute(searchIdString);
    }

    //Based on a stackoverflow snippet
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public class FetchArtistTopSongTask extends AsyncTask<String, Void, ArrayList<SpotifyArtistSong>> {

        private final String LOG_TAG = FetchArtistTopSongTask.class.getSimpleName();

        @Override
        protected ArrayList<SpotifyArtistSong> doInBackground(String... params) {

            if( params.length == 0 ) {
                return null;
            }

            SpotifyApi api = new SpotifyApi();

            // Most (but not all) of the Spotify Web API endpoints require authorisation.
            // If you know you'll only use the ones that don't require authorisation you can skip this step
            //api.setAccessToken("myAccessToken");

            SpotifyService spotify = api.getService();

            final String artistSpotifyId = params[0];

            //Log.d(LOG_TAG, "Looking for Artists with the name: " + artistName);
            Map<String, Object> queryParams = new HashMap<String, Object>();
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String location = prefs.getString(getString(R.string.country_code_key),
                    getString(R.string.pref_country_code_default));
            queryParams.put("country",location);

            try {
                Tracks tracks = spotify.getArtistTopTrack(artistSpotifyId, queryParams);

                if (artistSongs != null) {
                    artistSongs.clear();
                } else {
                    artistSongs = new ArrayList<SpotifyArtistSong>();
                }

                if (tracks != null && tracks.tracks != null) {

                    for (Track track : tracks.tracks) {
                        AlbumSimple albumSimple = track.album;
                        String trackPreviewUrl = track.preview_url;
                        String trackName = track.name;
                        String trackAlbum = albumSimple.name;
                        String albumImageLarge = null;
                        String albumImageSmall = null;

                        if (albumSimple.images != null && !albumSimple.images.isEmpty()) {

                            int albumImageCount = albumSimple.images.size();

                            albumImageLarge = albumSimple.images.get(0).url;

                            if (albumImageCount > 1) {
                                albumImageSmall = albumSimple.images.get(1).url;
                            } else {
                                albumImageSmall = albumImageLarge;
                            }
                        }

                        SpotifyArtistSong artistSong = new SpotifyArtistSong(trackName,
                                trackAlbum,
                                albumImageLarge,
                                albumImageSmall,
                                trackPreviewUrl);

                        artistSongs.add(artistSong);

                    }

                } else {
                    return new ArrayList<>();
                }
            } catch (RetrofitError re) {

                //Logging error for now, would like to show a message to the user instead
                Log.e(LOG_TAG, re.getMessage());
                return null;
            }


            return new ArrayList<>(artistSongs);
        }

        @Override
        protected void onPostExecute(ArrayList<SpotifyArtistSong> result) {

            mArtistSongAdapter.clear();

            if( result == null ) {

                getToast(getResources().getString(R.string.connection_error));

            } else if( !result.isEmpty() ) {

                for( SpotifyArtistSong artistSong : result ) {
                    mArtistSongAdapter.add(artistSong);
                }

            } else {

                getToast("We didn't find anything that matched. Go back and try again!");

            }
        }


    }

    private void getToast(String message) {
        Context context = getActivity();
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
