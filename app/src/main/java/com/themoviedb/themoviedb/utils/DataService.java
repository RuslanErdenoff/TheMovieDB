package com.themoviedb.themoviedb.utils;

import com.themoviedb.themoviedb.models.movies.Movie;
import com.themoviedb.themoviedb.models.movies.MovieDetails;
import com.themoviedb.themoviedb.models.movies.MoviesResponce;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

public interface DataService {
    @GET("movie/popular/")
    Call<MoviesResponce> getMovies(@QueryMap Map<String,String> options);
    @GET("movie/{movie_id}")
    Call<MovieDetails> getDetails(@Path("movie_id") long id,@QueryMap Map<String,String> options);
    @GET("search/movie")
    Call<MoviesResponce> getFoundedMovies(@QueryMap Map<String,String> options);
}
