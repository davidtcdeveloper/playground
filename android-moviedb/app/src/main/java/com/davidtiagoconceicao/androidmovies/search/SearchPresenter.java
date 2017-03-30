package com.davidtiagoconceicao.androidmovies.search;

import android.util.Log;
import android.util.LongSparseArray;

import com.davidtiagoconceicao.androidmovies.commons.MovieUtil;
import com.davidtiagoconceicao.androidmovies.data.Genre;
import com.davidtiagoconceicao.androidmovies.data.ImageConfiguration;
import com.davidtiagoconceicao.androidmovies.data.Movie;
import com.davidtiagoconceicao.androidmovies.data.remote.configuration.ConfigurationRepository;
import com.davidtiagoconceicao.androidmovies.data.remote.genre.GenresRemoteRepository;
import com.davidtiagoconceicao.androidmovies.data.remote.movie.MoviesRemoteRepository;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.BackpressureStrategy;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subscribers.ResourceSubscriber;

/**
 * Presenter for upcoming movies list.
 * <p>
 * Created by david on 22/02/17.
 */

final class SearchPresenter implements SearchContract.Presenter {

    private final CompositeDisposable compositeDisposable;
    private final SearchContract.View view;
    private final MoviesRemoteRepository moviesRemoteRepository;
    private final GenresRemoteRepository genresRemoteRepository;
    private final ConfigurationRepository configurationRepository;

    private LongSparseArray<Genre> genres;
    private ImageConfiguration imageConfiguration;
    private final PublishSubject<String> searchSubject;

    SearchPresenter(
            SearchContract.View view,
            MoviesRemoteRepository moviesRemoteRepository,
            GenresRemoteRepository genresRemoteRepository,
            ConfigurationRepository configurationRepository) {

        this.view = view;
        this.moviesRemoteRepository = moviesRemoteRepository;
        this.genresRemoteRepository = genresRemoteRepository;
        this.configurationRepository = configurationRepository;
        this.compositeDisposable = new CompositeDisposable();

        searchSubject = PublishSubject.create();
        compositeDisposable.add(
                searchSubject
                        .filter(new Predicate<String>() {
                            @Override
                            public boolean test(String s) throws Exception {
                                return s.length() >= 3;
                            }
                        })
                        .debounce(300, TimeUnit.MILLISECONDS)
                        .flatMap(new Function<String, ObservableSource<Movie>>() {
                            @Override
                            public ObservableSource<Movie> apply(String s) throws Exception {
                                return SearchPresenter.this.moviesRemoteRepository.searchMovie(s)
                                        .map(new Function<Movie, Movie>() {
                                            @Override
                                            public Movie apply(Movie movie) throws Exception {
                                                return MovieUtil.mapMovieFields(movie, imageConfiguration, genres);
                                            }
                                        });
                            }
                        })
                        .toList()
                        .observeOn(AndroidSchedulers.mainThread())
                        .toFlowable()
                        .subscribeWith(new ResourceSubscriber<List<Movie>>() {
                            @Override
                            public void onNext(List<Movie> movies) {
                                SearchPresenter.this.view.showResult(movies);
                            }

                            @Override
                            public void onError(Throwable t) {
                                SearchPresenter.this.handleException(t);
                                SearchPresenter.this.view.showLoading(false);
                            }

                            @Override
                            public void onComplete() {
                                SearchPresenter.this.view.showLoading(false);
                            }
                        }));

        this.view.setPresenter(this);
    }

    @Override
    public void onAttach() {
        //No action required
        view.showLoading(false);
    }

    @Override
    public void onDetach() {
        compositeDisposable.clear();
    }

    @Override
    public void onDestroy() {
        compositeDisposable.dispose();
    }

    @Override
    public void search(final String query) {
        if (genres == null) {
            view.showLoading(true);

            genres = new LongSparseArray<>();

            loadGenres(query);

        } else {
            searchSubject.onNext(query);
        }
    }

    //Called by inner classes, default avoid accessors
    @SuppressWarnings("WeakerAccess")
    void handleException(Throwable e) {
        Log.e(getClass().getSimpleName(), "Exception on stream", e);
        view.showErrorLoading();
        view.showLoading(false);
    }

    //Called by inner classes, default avoid accessors
    @SuppressWarnings("WeakerAccess")
    void loadGenres(final String query) {
        compositeDisposable.add(
                genresRemoteRepository.getGenres()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new ResourceSubscriber<Genre>() {
                            @Override
                            public void onNext(Genre genre) {
                                genres.put(genre.id(), genre);
                            }

                            @Override
                            public void onError(Throwable t) {
                                handleException(t);
                            }

                            @Override
                            public void onComplete() {
                                if (imageConfiguration == null) {
                                    loadImageConfiguration(query);
                                } else {
                                    searchSubject.onNext(query);
                                }
                            }
                        }));
    }

    //Called by inner classes, default avoid accessors
    @SuppressWarnings("WeakerAccess")
    void loadImageConfiguration(final String query) {
        compositeDisposable.add(
                configurationRepository.getImageConfiguration()
                        .observeOn(AndroidSchedulers.mainThread())
                        .toFlowable(BackpressureStrategy.BUFFER)
                        .subscribeWith(new ResourceSubscriber<ImageConfiguration>() {
                            @Override
                            public void onNext(ImageConfiguration imageConfigurationResponse) {
                                imageConfiguration = imageConfigurationResponse;
                            }

                            @Override
                            public void onError(Throwable t) {
                                handleException(t);
                            }

                            @Override
                            public void onComplete() {
                                searchSubject.onNext(query);
                            }
                        }));
    }
}
