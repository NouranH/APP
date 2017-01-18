package com.example.noran.mymovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class DetailsFragment extends Fragment {
    private TextView titleTextView;
    private TextView overViewTextView;
    private TextView dateViewTextView;
    private ImageView imageView;
    private TextView voteAveragTextView;
    private ProgressBar mTrailerProgressBar;
    private ProgressBar mReviewProgressBar;
    private LinearLayout linearLayoutTrailer;
    private LinearLayout linearLayoutReview;
    private Button button;
    String movie_id;
    boolean favOrNot = false ;


    private ArrayList<Trailer> mTrailerData = new ArrayList<>();

    private ArrayList<Review> mReviewData = new ArrayList<>();

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.activity_details_fragment, container, false);

        getActivity().setTitle("MovieDetails");


        titleTextView = (TextView) rootView.findViewById(R.id.title);
        overViewTextView = (TextView) rootView.findViewById(R.id.overView);
        dateViewTextView = (TextView) rootView.findViewById(R.id.dateView);
        voteAveragTextView = (TextView) rootView.findViewById(R.id.voteAverage);
        imageView = (ImageView) rootView.findViewById(R.id.grid_item_image);
        mTrailerProgressBar = (ProgressBar) rootView.findViewById(R.id.trailerProgressBar);
        mReviewProgressBar = (ProgressBar) rootView.findViewById(R.id.reviewProgressBar);
        linearLayoutTrailer = (LinearLayout) rootView.findViewById(R.id.trailerLL);
        linearLayoutReview = (LinearLayout) rootView.findViewById(R.id.reviewLL);
        button=(Button) rootView.findViewById(R.id.btn);



        Movie movie = getMovieData();

        if(movie != null) {
            titleTextView.setText(movie.getTitle());
            overViewTextView.setText(movie.getPlotSynopsis());
            dateViewTextView.setText(movie.getReleaseDate());
            voteAveragTextView.setText(movie.getVoteAverage());
            movie_id = movie.getId();
            Picasso.with(getActivity()).load("http://image.tmdb.org/t/p/w185/" + (movie.getImage())).into(imageView);


            /********************************************************************************************************/
            final AsyncTaskCallBack TrailersResponseCallback = new AsyncTaskCallBack() {
                @Override
                public void onTaskFinish(String result) {
                    try {
                        mTrailerData = getTrailerDataFromJson(result);
                        ShowTrailers(mTrailerData, inflater);
                    } catch (Exception e) {
                        Log.v(DetailsFragment.class.getSimpleName(), e.getMessage());
                    }
                }

                ;
            };

            String TRALER_URL = "http://api.themoviedb.org/3/movie/" + movie_id + "/videos?api_key="+getResources().getString(R.string.my_api_key)+" ";
            String REVIEW_URL = "http://api.themoviedb.org/3/movie/" + movie_id + "/reviews?api_key="+getResources().getString(R.string.my_api_key)+" ";


            new MyAsyncTask(TrailersResponseCallback, mTrailerProgressBar, getActivity()).execute(TRALER_URL);


            final AsyncTaskCallBack ReviewsResponseCallback = new AsyncTaskCallBack() {
                @Override
                public void onTaskFinish(String result) {
                    try {
                        mReviewData = getReviewDataFromJson(result);
                        ShowReviews(mReviewData, inflater);
                    } catch (Exception e) {
                        Log.v(DetailsFragment.class.getSimpleName(), e.getMessage());
                    }
                }

                ;
            };

            new MyAsyncTask(ReviewsResponseCallback, mReviewProgressBar, getActivity()).execute(REVIEW_URL);




            final SharedPreferences mySharedPreferences = getActivity().getSharedPreferences("MySharedPreferences", Context.MODE_PRIVATE);
            String AllIDs = mySharedPreferences.getString("FavMoviesIDs", "");
            if (AllIDs.contains(movie.getId())) {
                favOrNot = true;
                button.setText("Remove Movie from favouroties");
            } else {
                favOrNot = false;
                button.setText("Add Movie to favouroties");
            }


            final Movie finalMovie = movie;
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (favOrNot) {
                        String IDs = mySharedPreferences.getString("FavMoviesIDs", "");
                        if (!IDs.isEmpty()) {
                            IDs = IDs.replace(finalMovie.getId()+ " ", "");
                            mySharedPreferences.edit().putString("FavMoviesIDs", IDs).apply();
                        }

                        button.setText("Add Movie to favouroties");
                        favOrNot = false;
                    }
                    else {


                        mySharedPreferences.edit().putString("FavMoviesIDs", mySharedPreferences.getString("FavMoviesIDs", "") + finalMovie.getId() + " ").apply();

                        button.setText("Remove Movie from favouroties");
                        favOrNot = true;
                    }
                }
            });
        }
        return rootView;
    }


    private Movie getMovieData() {
        Bundle b = getArguments();
        if(b == null){
            b = getActivity().getIntent().getExtras();
        }
        return (Movie) b.get("item");
    }



    private void ShowTrailers(ArrayList<Trailer> mTrailerData, LayoutInflater inflater) {

        for (final Trailer trailer : mTrailerData) {
            View view = inflater.inflate(R.layout.trailer_item, null);
            TextView TrailerNameTV = (TextView) view.findViewById(R.id.trailer_name);
            ImageView ClickPic = (ImageView) view.findViewById(R.id.click_image);

            TrailerNameTV.setText(trailer.getName());
            ClickPic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=" + trailer.getKey()));
                    startActivity(browserIntent);
                }
            });


            linearLayoutTrailer.addView(view);

        }

    }

    private void ShowReviews(ArrayList<Review> mReviewData, LayoutInflater inflater) {

        for (final Review review : mReviewData) {
            View view = inflater.inflate(R.layout.review_item, null);
            TextView ReviewAuthorText = (TextView) view.findViewById(R.id.review_author);
            TextView ReviewContentText = (TextView) view.findViewById(R.id.review_content);

            ReviewAuthorText.setText(review.getAuthor());
            ReviewContentText.setText(review.getContent());


            linearLayoutReview.addView(view);

        }

    }





    private ArrayList<Trailer> getTrailerDataFromJson(String movieJsonStr)
            throws JSONException {
        ArrayList<Trailer> myTrailers = new ArrayList<Trailer>();

        JSONObject jsonResponse;

        jsonResponse = new JSONObject(movieJsonStr);

        JSONArray jsonMainNode = jsonResponse.optJSONArray("results");

        int lengthJsonArr = jsonMainNode.length();

        for (int i = 0; i < lengthJsonArr; i++) {
            Trailer trailer = new Trailer();

            JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);


            trailer.setName((jsonChildNode.optString("name").toString()));
            trailer.setKey(jsonChildNode.optString("key").toString());


            myTrailers.add(trailer);
        }
        return myTrailers;
    }



    private ArrayList<Review> getReviewDataFromJson(String movieJsonStr)
            throws JSONException {
        ArrayList<Review> myReviews = new ArrayList<Review>();

        JSONObject jsonResponse;

        jsonResponse = new JSONObject(movieJsonStr);

        JSONArray jsonMainNode = jsonResponse.optJSONArray("results");


        int lengthJsonArr = jsonMainNode.length();

        for (int i = 0; i < lengthJsonArr; i++) {
            Review review = new Review();

            JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

            review.setAuthor((jsonChildNode.optString("author").toString()));
            review.setContent(jsonChildNode.optString("content").toString());


            myReviews.add(review);
        }
        return myReviews;
    }





}




