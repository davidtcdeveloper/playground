package com.davidtiagoconceicao.androidmovies.data.remote.movie;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Endpoint for movies operations.
 * <p>
 * Created by david on 22/02/17.
 */

interface MoviesEndpoint {

    @GET("movie/upcoming")
    Observable<MoviesQueryResponse> getUpcoming(
            @Query("page") int requestedPage);

    @GET("search/movie")
    Observable<MoviesQueryResponse> getSearchResults(
            @Query("query") String query);

}
