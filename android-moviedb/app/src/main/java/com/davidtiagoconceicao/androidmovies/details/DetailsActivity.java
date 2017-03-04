package com.davidtiagoconceicao.androidmovies.details;

import android.content.Context;
import android.content.Intent;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.davidtiagoconceicao.androidmovies.R;
import com.davidtiagoconceicao.androidmovies.commons.DateFormatUtil;
import com.davidtiagoconceicao.androidmovies.commons.GenreUtil;
import com.davidtiagoconceicao.androidmovies.data.Movie;
import com.davidtiagoconceicao.androidmovies.databinding.ActivityDetailsBinding;
import com.squareup.picasso.Picasso;

/**
 * Activity for displaying movie details.
 * <p>
 * Created by david on 25/02/17.
 */

public final class DetailsActivity extends AppCompatActivity {

    private static final String MOVIE_EXTRA = "MOVIE_EXTRA";

    public static void startForMovie(Movie movie, Context context) {
        Intent intent = new Intent(context, DetailsActivity.class);
        intent.putExtra(MOVIE_EXTRA, movie);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_details);

//        setSupportActionBar(toolbar);

//        toolbar.setNavigationOnClickListener(
//                new ClickListener(this));

        Movie movie = getIntent()
                .getExtras()
                .getParcelable(MOVIE_EXTRA);


        ActivityDetailsBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_details);
        binding.setMovie(movie);
        binding.setGenres(new GenresBinder(movie, this));
        binding.setDate(new DateBinder(movie));
        binding.setImage(new ImageBinder(movie));
    }

    private void bindView(Movie movie) {

//        String posterPath = movie.posterPath();
//        if (posterPath != null) {
//            Picasso.with(this)
//                    .load(posterPath)
//                    .fit()
//                    .centerInside()
//                    .into(imageView);
//        }
//
//        String title = movie.title();
//        toolbar.setTitle(title);
//
//        titleText.setText(title);
//
//        List<Genre> genres = movie.genres();
//
//        assert genres != null;
//        String quantityString = getResources()
//                .getQuantityString(R.plurals.genre_plural, genres.size());
//
//        genresLabelText.setText(quantityString);
//
//        genresListText.setText(
//                GenreUtil.createGenresSpannable(this, genres));
//
//        releaseDateText.setText(
//                DateFormatUtil.formatDate(movie.releaseDate()));
//
//        overviewText.setText(movie.overview());
    }

    private static final class ClickListener implements View.OnClickListener {
        private final DetailsActivity detailsActivity;

        ClickListener(DetailsActivity detailsActivity) {

            this.detailsActivity = detailsActivity;
        }

        @Override
        public void onClick(View v) {
            detailsActivity.onBackPressed();
        }
    }

    public static final class GenresBinder {

        private final Movie movie;
        private final Context context;

        private GenresBinder(Movie movie, Context context) {
            this.movie = movie;
            this.context = context;
        }

        public CharSequence getGenres() {
            return GenreUtil.createGenresSpannable(context, movie.genres());
        }
    }

    public static final class DateBinder {
        private final Movie movie;

        private DateBinder(Movie movie) {
            this.movie = movie;
        }

        public String getReleaseDate() {
            return DateFormatUtil.formatDate(movie.releaseDate());
        }
    }

    public static final class ImageBinder {

        private final Movie movie;

        public ImageBinder(Movie movie) {
            this.movie = movie;
        }

        public String getImageUrl() {
            return movie.posterPath();
        }

        @BindingAdapter({"bind:imageUrl"})
        public static void loadImage(ImageView imageView, String imageUrl) {

            if (imageUrl != null) {
                Picasso.with(imageView.getContext())
                        .load(imageUrl)
                        .fit()
                        .centerInside()
                        .into(imageView);
            }

        }
    }
}
