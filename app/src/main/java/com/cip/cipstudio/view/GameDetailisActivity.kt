package com.cip.cipstudio.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import com.cip.cipstudio.R
import com.cip.cipstudio.model.data.Game
import com.cip.cipstudio.viewmodel.GameDetailsViewModel

class GameDetailisActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_detailis)

        supportActionBar!!.hide()

        val currentGame : Game = intent.extras!!.get("game") as Game

        val gameDetailsViewModel : GameDetailsViewModel =
            GameDetailsViewModel(this,
                                        currentGame,
                                        findViewById(R.id.tvGameDetailsTitle),
                                        findViewById(R.id.tvGameDetailsDescription),
                                        findViewById(R.id.tvGameDetailsReleaseDate),
                                        findViewById(R.id.tvUserRatingValue),
                                        findViewById(R.id.tvUserRatingCounter),
                                        findViewById(R.id.tvCriticsRatingValue),
                                        findViewById(R.id.tvCriticsRatingCounter),
                                        findViewById(R.id.tvGameDetailsPlatforms),
                                        findViewById(R.id.tvShowMoreDescription),
                                        findViewById(R.id.cpiUserRating),
                                        findViewById(R.id.cpiCriticsRating),
                                        findViewById(R.id.glGridGenreLayout),
                                        findViewById(R.id.llPageLayout),
                                        findViewById(R.id.rvSimilarGames),
                                        findViewById(R.id.ivGameDetailsCover),
            )
    }
}