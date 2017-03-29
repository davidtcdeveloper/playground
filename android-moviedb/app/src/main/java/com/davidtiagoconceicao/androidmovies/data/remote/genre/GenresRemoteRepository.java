package com.davidtiagoconceicao.androidmovies.data.remote.genre;

import com.davidtiagoconceicao.androidmovies.commons.retrofit.RetrofitServiceGenerator;
import com.davidtiagoconceicao.androidmovies.data.Genre;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Repository for remote genres operations.
 * <p>
 * Created by david on 22/02/17.
 */

public final class GenresRemoteRepository {

    public Observable<Genre> getGenres() {
        return RetrofitServiceGenerator.generateService(GenresEndpoint.class)
                .getGenres()
                .map(new Function<GenresListResponse, List<GenreResponse>>() {
                    @Override
                    public List<GenreResponse> apply(GenresListResponse genresListResponse) throws Exception {
                        return genresListResponse.genres();
                    }
                })
                .flatMapIterable(new Function<List<GenreResponse>, Iterable<GenreResponse>>() {
                    @Override
                    public Iterable<GenreResponse> apply(List<GenreResponse> genreResponses) throws Exception {
                        return genreResponses;
                    }
                })
                .map(new Function<GenreResponse, Genre>() {
                    @Override
                    public Genre apply(GenreResponse genreResponse) throws Exception {
                        return Genre.create(genreResponse.id(), genreResponse.name());
                    }
                })
                .subscribeOn(Schedulers.io());
    }

}
