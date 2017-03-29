package com.davidtiagoconceicao.androidmovies.data.remote.configuration;

import io.reactivex.Observable;
import retrofit2.http.GET;

/**
 * Endpoint for configuration operations.
 * <p>
 * Created by david on 24/02/17.
 */

interface ConfigurationEndpoint {
    @GET("configuration")
    Observable<ConfigurationsResponseEnvelope> getConfiguration();
}
