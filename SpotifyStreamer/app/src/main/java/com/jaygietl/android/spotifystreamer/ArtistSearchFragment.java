package com.jaygietl.android.spotifystreamer;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


/**
 * A placeholder fragment containing a simple view.
 */
public class ArtistSearchFragment extends Fragment {

    private ArrayAdapter<String> mArtistSearchAdapter;
    private EditText mEdit;
    private List<String> artistNames = new ArrayList<String>();

    public ArtistSearchFragment() {
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
                    doArtistSearch();
                    handled = true;
                }
                return handled;
            }
        });

        List<String> artistNames = new ArrayList<>();
        artistNames.add("BossToneS");
        artistNames.add("Foo Fighters");
        artistNames.add("Queen");
        artistNames.add("BossToneS");
        artistNames.add("Foo Fighters");
        artistNames.add("Queen");
        artistNames.add("BossToneS");
        artistNames.add("Foo Fighters");
        artistNames.add("Queen");

        mArtistSearchAdapter = new ArrayAdapter<String>(
                getActivity(),
                R.layout.list_item_artist_result,
                R.id.list_item_artist_name_result_textview,
                new ArrayList<String>()
        );

        ListView listView = (ListView) rootView.findViewById(R.id.list_view_artist_result);
        listView.setAdapter(mArtistSearchAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String artist = mArtistSearchAdapter.getItem(position);
                Toast.makeText(getActivity(), artist, Toast.LENGTH_SHORT).show();
                /*Intent intent = new Intent(getActivity(), DetailActivity.class)
                        .putExtra(Intent.EXTRA_TEXT, forecast);
                startActivity(intent);*/
            }
        });

        return rootView;
    }

    private void doArtistSearch(){
        Log.v("[EditText]", mEdit.getText().toString());
        FetchArtistTask fetchArtistTask = new FetchArtistTask();
        fetchArtistTask.execute(mEdit.getText().toString());
    }

    public class FetchArtistTask extends AsyncTask<String, Void, List<String>> {

        private final String LOG_TAG = FetchArtistTask.class.getSimpleName();

        @Override
        protected List<String> doInBackground(String... params) {

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

            spotify.searchArtists(artistName, new Callback<ArtistsPager>() {
                @Override
                public void success(ArtistsPager artistsPager, Response response) {
                    if( artistsPager.artists != null ) {

                        if( artistNames != null ) {
                            artistNames.clear();
                        } else {
                            artistNames = new ArrayList<String>();
                        }

                        for( Artist artist : artistsPager.artists.items ) {
                            artistNames.add( artist.name + " " + artist.id );
                        }

                    }
                }

                @Override
                public void failure(RetrofitError error) {

                }
            });

            /*spotify.getAlbum("2dIGnmEIy1WZIcZCFSj6i8", new Callback<Album>() {
                @Override
                public void success(Album album, Response response) {
                    Log.d("Album success", album.name);
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.d("Album failure", error.toString());
                }
            });*/

            return artistNames;
        }

        @Override
        protected void onPostExecute(List<String> result) {
            if( result != null && !result.isEmpty() ) {
                mArtistSearchAdapter.clear();
                for( String dayForecastStr : result ) {
                    mArtistSearchAdapter.add(dayForecastStr);
                }
            } else {
                Toast.makeText(getActivity(), "We didn't find anything that matched. Try something else.", Toast.LENGTH_SHORT).show();
            }
        }


    }
}
