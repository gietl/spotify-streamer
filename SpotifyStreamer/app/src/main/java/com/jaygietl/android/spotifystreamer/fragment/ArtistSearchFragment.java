package com.jaygietl.android.spotifystreamer.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

import com.jaygietl.android.spotifystreamer.R;
import com.jaygietl.android.spotifystreamer.activity.SpotifyArtistDetail;
import com.jaygietl.android.spotifystreamer.adapter.SpotifyArtistAdapter;
import com.jaygietl.android.spotifystreamer.model.SpotifyArtist;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Image;
import retrofit.RetrofitError;


/**
 * A placeholder fragment containing a simple view.
 */
public class ArtistSearchFragment extends Fragment {

    private SpotifyArtistAdapter mArtistSearchAdapter;
    private EditText mEdit;

    private ArrayList<SpotifyArtist> mArtistDetails;
    private String mSearchString;


    public ArtistSearchFragment() {
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);

        outState.putParcelableArrayList("artists", mArtistDetails);
        outState.putString("search_value", mEdit.getText().toString());

        //getToast("onSaveInstanceState");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //getToast("onCreateView");

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        if( savedInstanceState != null ) {

            if( savedInstanceState .getParcelableArrayList("artists") != null ) {
                mArtistDetails = savedInstanceState.getParcelableArrayList("artists");
            }
            if( savedInstanceState .getString("search_value") != null ) {
                mSearchString =  savedInstanceState.getString("search_value");
            }

            //setupAdapter();
        }

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
                    mSearchString = mEdit.getText().toString();
                    if( isNetworkAvailable() ) {
                        doArtistSearch();
                    } else {
                        getToast(getResources().getString(R.string.connection_error));
                    }

                    handled = true;

                }
                return handled;
            }
        });

        setupAdapter();

        ListView listView = (ListView) rootView.findViewById(R.id.list_view_artist_result);
        listView.setAdapter(mArtistSearchAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                SpotifyArtist artist = mArtistSearchAdapter.getItem(position);

                Intent intent = new Intent(getActivity(), SpotifyArtistDetail.class)
                        .putExtra("artist", artist);

                startActivity(intent);

            }
        });

        return rootView;
    }

    //Based on a stackoverflow snippet
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void setupAdapter() {

        if( mArtistDetails == null ) {
            mArtistDetails = new ArrayList<>();
        }

        mArtistSearchAdapter = new SpotifyArtistAdapter(
                getActivity(), mArtistDetails
        );
    }

    private void doArtistSearch() {
        //Log.v("[EditText]", mEdit.getText().toString());
        FetchArtistTask fetchArtistTask = new FetchArtistTask();
        String searchString = mSearchString;
        fetchArtistTask.execute(searchString);
    }

    public class FetchArtistTask extends AsyncTask<String, Void, ArrayList<SpotifyArtist>> {

        private final String LOG_TAG = FetchArtistTask.class.getSimpleName();

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

            try {
                ArtistsPager artistsPager = spotify.searchArtists(artistName);

                if (mArtistDetails != null) {
                    mArtistDetails.clear();
                } else {
                    mArtistDetails = new ArrayList<SpotifyArtist>();
                }

                if (artistsPager.artists != null && !artistsPager.artists.items.isEmpty()) {

                    for (Artist artist : artistsPager.artists.items) {

                        List<Image> artistImages = artist.images;
                        String thumbImageUrl = "";
                        for (Image image : artistImages) {
                            if (image.width.equals(64)) {
                                thumbImageUrl = image.url;
                            }
                        }

                        SpotifyArtist artistDetail = new SpotifyArtist(artist.name, thumbImageUrl, artist.id);
                        mArtistDetails.add(artistDetail);


                    }

                } else {
                    return new ArrayList<>();
                }
            } catch (RetrofitError re) {
                //Logging error for now, would like to show a message to the user instead
                Log.e(LOG_TAG, re.getMessage());
                return null;
            }

            return new ArrayList<>(mArtistDetails);
        }

        @Override
        protected void onPostExecute(ArrayList<SpotifyArtist> result) {

            mArtistSearchAdapter.clear();

            if( result == null ) {

                getToast(getResources().getString(R.string.connection_error));

            } else if( !result.isEmpty() ) {

                for( SpotifyArtist artistDetail : result ) {

                    mArtistSearchAdapter.add(artistDetail);

                }

            } else {

                getToast("We didn't find anything that matched. Try again!");

            }
        }


    }

    /*@Override
    public void onPause() {
        super.onPause();
        getToast("onPause");
    }

    @Override
    public void onResume() {
        super.onResume();
        getToast("onResume");
    }

    @Override
    public void onStop() {
        super.onStop();
        getToast("onStop");
    }

    @Override
    public void onStart() {
        super.onStart();
        getToast("onStart");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getToast("onDestroy");
    }*/

    private void getToast(String message) {
        Context context = getActivity();
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
