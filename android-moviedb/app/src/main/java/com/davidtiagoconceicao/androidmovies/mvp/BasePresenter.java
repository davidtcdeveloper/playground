package com.davidtiagoconceicao.androidmovies.mvp;

/**
 * Base interface for presenters.
 * <p>
 * Created by david on 22/02/17.
 */

public interface BasePresenter {

    void onAttach();

    void onDetach();

    void onDestroy();
}
