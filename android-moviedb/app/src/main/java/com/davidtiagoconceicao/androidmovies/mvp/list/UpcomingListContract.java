package com.davidtiagoconceicao.androidmovies.mvp.list;

import com.davidtiagoconceicao.androidmovies.mvp.BasePresenter;
import com.davidtiagoconceicao.androidmovies.mvp.BaseView;
import com.davidtiagoconceicao.androidmovies.data.Movie;

import java.util.List;

/**
 * Contract for upcoming movies list.
 * <p>
 * Created by david on 22/02/17.
 */

final class UpcomingListContract {

    interface View extends BaseView<UpcomingListContract.Presenter> {

        void addMovies(List<Movie> movie);

        void showLoading(boolean show);

        void clearList();

        void showErrorLoading();
    }

    interface Presenter extends BasePresenter {

        void onLoadMore();

        void refresh();
    }
}
