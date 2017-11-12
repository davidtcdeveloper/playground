package com.davidtiagoconceicao.androidmovies.data.remote.configuration

import com.davidtiagoconceicao.androidmovies.commons.retrofit.RetrofitServiceGenerator
import com.davidtiagoconceicao.androidmovies.data.ImageConfiguration
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.util.regex.Pattern

/**
 * Class for obtaining configuration data.
 *
 *
 * Created by david on 23/02/17.
 */

class ConfigurationRepository {

    private val sizeRegex = "w\\d*"

    fun getImageConfiguration(): Single<ImageConfiguration> {
        return RetrofitServiceGenerator.generateService(ConfigurationEndpoint::class.java)
                .configuration
                .map { configurationsResponseEnvelope ->
                    val configurationResponse = configurationsResponseEnvelope.images()
                    val baseUrl = configurationResponse.baseUrl()
                    val backdropUrl = baseUrl + getBiggestSize(configurationResponse.backdropSizes())!!
                    val posterUrl = baseUrl + getBiggestSize(configurationResponse.posterSizes())!!
                    ImageConfiguration.builder()
                            .backdropBaseUrl(backdropUrl)
                            .posterBaseUrl(posterUrl)
                            .build()
                }
                .subscribeOn(Schedulers.io())
    }

    /**
     * Gets the biggest image size, discarding the original size.
     */
    private fun getBiggestSize(descriptionSizes: List<String>): String? {
        val pattern = Pattern.compile(sizeRegex)
        return descriptionSizes.lastOrNull { pattern.matcher(it).matches() }
    }
}
