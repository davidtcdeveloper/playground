package com.davidtiagoconceicao.androidmovies.data.remote.genre;

import io.reactivex.Single;
import retrofit2.http.GET;

/**
 * Endpoint for movies operations.
 * <p>
 * Created by david on 22/02/17.
 */

public interface GenresEndpoint {

    @GET("genre/movie/list")
    Single<GenresListResponse> getGenres();

}
