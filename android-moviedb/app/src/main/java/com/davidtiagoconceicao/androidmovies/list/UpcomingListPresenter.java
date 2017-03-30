package com.davidtiagoconceicao.androidmovies.list;

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

import io.reactivex.BackpressureStrategy;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.observers.ResourceSingleObserver;
import io.reactivex.subscribers.ResourceSubscriber;

/**
 * Presenter for upcoming movies list.
 * <p>
 * Created by david on 22/02/17.
 */

final class UpcomingListPresenter implements UpcomingListContract.Presenter {

    private final CompositeDisposable compositeDisposable;
    private final UpcomingListContract.View view;
    private final MoviesRemoteRepository moviesRemoteRepository;
    private final GenresRemoteRepository genresRemoteRepository;
    private final ConfigurationRepository configurationRepository;

    private int currentPageCount;

    private LongSparseArray<Genre> genres;
    private ImageConfiguration imageConfiguration;

    UpcomingListPresenter(
            UpcomingListContract.View view,
            MoviesRemoteRepository moviesRemoteRepository,
            GenresRemoteRepository genresRemoteRepository,
            ConfigurationRepository configurationRepository) {

        this.view = view;
        this.moviesRemoteRepository = moviesRemoteRepository;
        this.genresRemoteRepository = genresRemoteRepository;
        this.configurationRepository = configurationRepository;
        this.compositeDisposable = new CompositeDisposable();

        this.view.setPresenter(this);
    }

    @Override
    public void onAttach() {

        if (genres == null) {
            view.showLoading(true);

            genres = new LongSparseArray<>();

            loadGenres();

        } else if (currentPageCount == 0) {
            refresh();
        }
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
    public void onLoadMore() {
        currentPageCount++;
        loadMoreItems();
    }

    @Override
    public void refresh() {
        currentPageCount = 1;
        loadMoreItems();
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
    void loadMoreItems() {
        view.showLoading(true);
        compositeDisposable.add(
                moviesRemoteRepository.getUpcoming(currentPageCount)
                        .map(new Function<Movie, Movie>() {
                            @Override
                            public Movie apply(Movie movie) throws Exception {
                                return MovieUtil.mapMovieFields(movie, imageConfiguration, genres);
                            }
                        })
                        .toList()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new ResourceSingleObserver<List<Movie>>() {
                            @Override
                            public void onSuccess(List<Movie> movies) {

                                if (currentPageCount == 1) {
                                    view.clearList();
                                }
                                view.addMovies(movies);

                                view.showLoading(false);
                            }

                            @Override
                            public void onError(Throwable throwable) {

                                view.showLoading(false);
                            }
                        }));
    }

    //Called by inner classes
    @SuppressWarnings("WeakerAccess")
    void loadGenres() {
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
                                    loadImageConfiguration();
                                } else {
                                    refresh();
                                }
                            }
                        }));
    }

    //Called by inner classes, default avoid accessors
    @SuppressWarnings("WeakerAccess")
    void loadImageConfiguration() {
        compositeDisposable.add(
                configurationRepository.getImageConfiguration()
                        .observeOn(AndroidSchedulers.mainThread())
                        .toFlowable(BackpressureStrategy.BUFFER)
                        .subscribeWith(
                                new ResourceSubscriber<ImageConfiguration>() {
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
                                        refresh();
                                    }
                                }));
    }

}
