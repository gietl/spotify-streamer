package com.jaygietl.android.spotifystreamer;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Image;


/**
 * A placeholder fragment containing a simple view.
 */
public class ArtistSearchFragment extends Fragment {

    private SpotifyArtistAdapter mArtistSearchAdapter;
    private EditText mEdit;


    public ArtistSearchFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // retain this fragment
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mEdit = (EditText) rootView.findViewById(R.id.edit_text_artist_search);
        mEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                    InputMethodManager in = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(v
                                    .getApplicationWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);

                    doArtistSearch();
                    handled = true;

                }
                return handled;
            }
        });

        mArtistSearchAdapter = new SpotifyArtistAdapter(
                getActivity(), new ArrayList<SpotifyArtist>()
        );

        ListView listView = (ListView) rootView.findViewById(R.id.list_view_artist_result);
        listView.setAdapter(mArtistSearchAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                SpotifyArtist artist = mArtistSearchAdapter.getItem(position);

                Toast.makeText(getActivity(), artist.artistName, Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getActivity(), SpotifyArtistDetail.class)
                        .putExtra("ARTIST_NAME", artist.artistName)
                        .putExtra("ARTIST_ID", artist.spotifyId);

                startActivity(intent);

            }
        });

        return rootView;
    }

    private void doArtistSearch(){
        Log.v("[EditText]", mEdit.getText().toString());
        FetchArtistTask fetchArtistTask = new FetchArtistTask();
        fetchArtistTask.execute(mEdit.getText().toString());
    }

    public class FetchArtistTask extends AsyncTask<String, Void, ArrayList<SpotifyArtist>> {

        private final String LOG_TAG = FetchArtistTask.class.getSimpleName();

        /*private ProgressDialog dialog = new ProgressDialog(getActivity());

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.dialog.setMessage("Fetching results...");
            this.dialog.show();
        }*/

        @Override
        protected ArrayList<SpotifyArtist> doInBackground(String... params) {

            if( params.length == 0 ) {
                return null;
            }

            SpotifyApi api = new SpotifyApi();

            // Most (but not all) of the Spotify Web API endpoints require authorisation.
            // If you know you'll only use the ones that don't require authorisation you can skip this step
            //api.setAccessToken("myAccessToken");

            SpotifyService spotify = api.getService();

            final String artistName = params[0];

            //Log.d(LOG_TAG, "Looking for Artists with the name: " + artistName);
            ArtistsPager artistsPager = spotify.searchArtists(artistName);

            ArrayList<SpotifyArtist> artistDetails = new ArrayList<SpotifyArtist>();

            if( artistsPager.artists != null && !artistsPager.artists.items.isEmpty() ) {

                for( Artist artist : artistsPager.artists.items ) {

                    List<Image> artistImages = artist.images;
                    String thumbImageUrl = "";
                    for( Image image : artistImages ) {
                        if( image.width.equals(64) ) {
                            thumbImageUrl = image.url;
                        }
                    }

                    SpotifyArtist artistDetail = new SpotifyArtist( artist.name, thumbImageUrl, artist.id );
                    artistDetails.add(artistDetail);


                }

            } else {
                return null;
            }

            return artistDetails;
        }

        @Override
        protected void onPostExecute(ArrayList<SpotifyArtist> result) {

            if( result != null && !result.isEmpty() ) {
                mArtistSearchAdapter.clear();
                for( SpotifyArtist artistDetail : result ) {
                    mArtistSearchAdapter.add(artistDetail);
                }
            } else {
                Toast.makeText(getActivity(), "We didn't find anything that matched. Try something else.", Toast.LENGTH_SHORT).show();
            }
        }


    }
}
