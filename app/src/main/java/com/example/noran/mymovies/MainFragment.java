package com.example.noran.mymovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.Serializable;
import java.util.ArrayList;

public class MainFragment extends Fragment {

    private  AsyncTaskCallBack ResponseCallback;
    private NameListener mListener;
    void setmNameListener(NameListener nameListener){
        this.mListener=nameListener;
    }
    private GridView mGridView;
    private ProgressBar mProgressBar;

    private GridViewAdapter mGridAdapter;
    private ArrayList<Movie> mGridData;


    private static final String POPULARITY_DESC = "popularity.desc";
    private static final String RATING_DESC = "vote_average.desc";
    private static final String FAVORITE = "favorite";

    private String mSortBy = POPULARITY_DESC;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search, menu);

        MenuItem action_sort_by_popularity = menu.findItem(R.id.action_sort_by_popularity);
        MenuItem action_sort_by_rating = menu.findItem(R.id.action_sort_by_rating);
        MenuItem action_sort_by_favorite = menu.findItem(R.id.action_sort_by_favorite);

        if (mSortBy.contentEquals(POPULARITY_DESC)) {
            if (!action_sort_by_popularity.isChecked()) {
                action_sort_by_popularity.setChecked(true);
            }
        } else if (mSortBy.contentEquals(RATING_DESC)) {
            if (!action_sort_by_rating.isChecked()) {
                action_sort_by_rating.setChecked(true);
            }
        } else if (mSortBy.contentEquals(FAVORITE)) {
            if (!action_sort_by_popularity.isChecked()) {
                action_sort_by_favorite.setChecked(true);
            }
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_sort_by_popularity:
                if (item.isChecked()) {
                    item.setChecked(false);
                } else {
                    item.setChecked(true);
                }
                mSortBy = POPULARITY_DESC;

                updateMovies(mSortBy);
                break;
            case R.id.action_sort_by_rating:
                if (item.isChecked()) {
                    item.setChecked(false);
                } else {
                    item.setChecked(true);
                }
                mSortBy = RATING_DESC;
                updateMovies(mSortBy);
                break;
            case R.id.action_sort_by_favorite:
                if (item.isChecked()) {
                    item.setChecked(false);
                } else {
                    item.setChecked(true);
                }
                mSortBy = FAVORITE;
                updateMovies(mSortBy);
                break;

        }
        final SharedPreferences mySharedPreferences = getActivity().getSharedPreferences("MySharedPreferences", Context.MODE_PRIVATE);
        mySharedPreferences.edit().putString("sortBy",mSortBy).commit();
        return true;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_gridview, container, false);

        mGridView = (GridView) rootView.findViewById(R.id.gridView);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        mGridData = new ArrayList<>();
        mGridAdapter = new GridViewAdapter(getActivity(), R.layout.grid_item_layout, mGridData);
        mGridView.setAdapter(mGridAdapter);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                Movie item = (Movie) parent.getItemAtPosition(position);

                mListener.setSelectedName(item);

            }
        });

        final SharedPreferences mySharedPreferences = getActivity().getSharedPreferences("MySharedPreferences",Context.MODE_PRIVATE);
        mSortBy = mySharedPreferences.getString("sortBy",POPULARITY_DESC);


        ResponseCallback = new AsyncTaskCallBack() {
            @Override
            public void onTaskFinish(String result) {
                try {
                    mGridData = getMovieDataFromJson(result);
                    mGridAdapter.setGridData(mGridData);

                }catch (Exception e){
                    Log.v(MainFragment.class.getSimpleName(), e.getMessage());
                }
            }
        };

        updateMovies(mSortBy);
        return rootView;
    }



    private void updateMovies(String sort_by) {
        if (!sort_by.contentEquals(FAVORITE)) {

            if (sort_by.equalsIgnoreCase(POPULARITY_DESC)){
                String FEED_sortURL = "http://api.themoviedb.org/3/movie/popular?api_key="+getResources().getString(R.string.my_api_key)+"&sort_by="+sort_by;
                new  MyAsyncTask(ResponseCallback, mProgressBar, getActivity()).execute(FEED_sortURL);
            }

            else {
                String FEED_sortURL = "http://api.themoviedb.org/3/movie/top_rated?api_key="+getResources().getString(R.string.my_api_key)+"&sort_by="+sort_by;
                new  MyAsyncTask(ResponseCallback, mProgressBar, getActivity()).execute(FEED_sortURL);
            }

        }

        else {
            //show favourites movies
            final SharedPreferences mySharedPreferences = getActivity().getSharedPreferences("MySharedPreferences", Context.MODE_PRIVATE);
            String AllIDs = mySharedPreferences.getString("FavMoviesIDs", "");
            final String IDsArray[] = AllIDs.split(" ");
            final ArrayList<Movie> FavMovies = new ArrayList<>();


            if (!AllIDs.equals("")) {

                for (int i = 0; i < IDsArray.length; i++) {
                    String id = IDsArray[i].replaceAll(" ", "");
                    String MovieURL = "https://api.themoviedb.org/3/movie/" + id + "?api_key="+getResources().getString(R.string.my_api_key)+"";
                    final int finalI = i;
                    new MyAsyncTask(new AsyncTaskCallBack() {
                        @Override
                        public void onTaskFinish(String result) {
                            try {
                                Movie movie = getFavouriteMovie(result);
                                FavMovies.add(movie);

                                if (finalI == IDsArray.length - 1) {
                                    mGridAdapter.setGridData(FavMovies);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }, null, getActivity()).execute(MovieURL);
                }
            }else{
                Toast.makeText(getContext(), "No favourites exists", Toast.LENGTH_SHORT).show();
            }
        }
    }





    private Movie getFavouriteMovie(String result) throws JSONException {
        Movie movie = new Movie();
        JSONObject jsonChildNode = new JSONObject(result);


        movie.setId((jsonChildNode.optString("id").toString()));
        movie.setImage(jsonChildNode.optString("poster_path").toString());
        movie.setReleaseDate(jsonChildNode.optString("release_date").toString());
        movie.setPlotSynopsis(jsonChildNode.optString("overview").toString());
        movie.setVoteAverage(jsonChildNode.optString("vote_average").toString());
        movie.setTitle(jsonChildNode.optString("title").toString());


        return movie;
    }

    private ArrayList<Movie> getMovieDataFromJson(String movieJsonStr)
            throws JSONException {
        ArrayList<Movie> myMovieList = new ArrayList<Movie>();

        JSONObject jsonResponse;

        try {

            jsonResponse = new JSONObject(movieJsonStr);

            JSONArray jsonMainNode = jsonResponse.optJSONArray("results");


            int lengthJsonArr = jsonMainNode.length();

            for (int i = 0; i < lengthJsonArr; i++) {
                Movie movie = new Movie();
                JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);




                movie.setId((jsonChildNode.optString("id").toString()));
                movie.setImage(jsonChildNode.optString("poster_path").toString());
                movie.setReleaseDate(jsonChildNode.optString("release_date").toString());
                movie.setPlotSynopsis(jsonChildNode.optString("overview").toString());
                movie.setVoteAverage(jsonChildNode.optString("vote_average").toString());
                movie.setTitle(jsonChildNode.optString("title").toString());

                myMovieList.add(movie);
            }



        } catch (JSONException e) {

            e.printStackTrace();
        }


        return myMovieList;

    }


}




