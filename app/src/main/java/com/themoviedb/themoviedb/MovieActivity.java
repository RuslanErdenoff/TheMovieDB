package com.themoviedb.themoviedb;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.themoviedb.themoviedb.models.movies.MovieDetails;
import com.themoviedb.themoviedb.utils.DataService;
import com.themoviedb.themoviedb.utils.ServerConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieActivity extends AppCompatActivity {
    public static final String MOVIE_ID_TAG="id_tag";
    private ImageView posterImage;
    private ImageView backDropImage;
    private TextView titleText;
    private TextView taglineText;
    private TextView ratingText;
    private RatingBar ratingBar;
    private TextView dateReleaseText;
    private TextView overviewText;
    private TextView genresText;
    private FrameLayout frameLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);
        frameLayout = findViewById(R.id.frame_detail_container);
        posterImage = findViewById(R.id.image_view_poster_detail);
        backDropImage = findViewById(R.id.image_view_backdrop_detail);
        titleText = findViewById(R.id.text_view_title_detail);
        taglineText = findViewById(R.id.text_view_tagline_detail);
        ratingText = findViewById(R.id.text_view_rating_detail);
        ratingBar = findViewById(R.id.rating_bar);
        dateReleaseText = findViewById(R.id.text_view_date_release_detail);
        overviewText = findViewById(R.id.text_view_overview_detail);
        genresText = findViewById(R.id.text_view_genres_detail);
        if(getIntent()!=null){
            final long currentId = getIntent().getLongExtra(MOVIE_ID_TAG,0L);
            DataService service = ServerConnection.getDataServiceInstance();
            Call<MovieDetails> call = service.getDetails(currentId,ServerConnection.getDefaultQueryOptions());

            call.enqueue(new Callback<MovieDetails>() {
                @Override
                public void onResponse(Call<MovieDetails> call, Response<MovieDetails> response) {
                    if(response.isSuccessful()){
                        fillViews(response.body());
                    }
                }

                @Override
                public void onFailure(Call<MovieDetails> call, Throwable t) {

                }
            });
        }
    }
    private void fillViews(MovieDetails movieDetails){


        titleText.setText(movieDetails.getTitle());
        taglineText.setText(movieDetails.getTagline());
        ratingText.setText("("+movieDetails.getVoteAverage()+")");
        ratingBar.setRating(movieDetails.getVoteAverage());
        dateReleaseText.setText("Дата релиза : "+movieDetails.getReleaseDate());
        overviewText.setText(movieDetails.getOverview());

        String genrestString = "";

        for(int i = 0; i < movieDetails.getGenres().size();i++){
            if(i!=movieDetails.getGenres().size()-1){
                genrestString+= movieDetails.getGenres().get(i).getName()+", ";
            }else{
                genrestString+=movieDetails.getGenres().get(i).getName();
            }
        }
        frameLayout.setVisibility(View.VISIBLE);
        Picasso.get().load(ServerConnection.IMAGE_URL+movieDetails.getBackdropPath()).fit().centerCrop().into(backDropImage);
        Picasso.get().load(ServerConnection.IMAGE_URL+movieDetails.getPosterPath()).into(posterImage);
        genresText.setText(genrestString);
    }
}
