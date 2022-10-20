package com.cip.cipstudio.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.HORIZONTAL
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.cip.cipstudio.R
import com.cip.cipstudio.adapters.GamesRecyclerViewAdapter
import com.cip.cipstudio.model.data.Game
import com.cip.cipstudio.repository.IGDBRepository
import com.cip.cipstudio.viewmodel.MainActivityViewModel
import com.google.android.material.progressindicator.CircularProgressIndicator


class MainActivity : AppCompatActivity() {

    private lateinit var gameRepo : IGDBRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        gameRepo = IGDBRepository()

        supportActionBar!!.hide()

        val mainActivityViewModel : MainActivityViewModel =
            MainActivityViewModel(this,
                findViewById<RecyclerView>(R.id.rvMostRatedGames),
                GamesRecyclerViewAdapter(this, ArrayList<Game>()),
                findViewById<RecyclerView>(R.id.rvMostHypedGames),
                GamesRecyclerViewAdapter(this, ArrayList<Game>()),
                gameRepo
            )

        mainActivityViewModel.initializeRecyclerView(
            mainActivityViewModel.mostRatedGamesRecyclerView,
            mainActivityViewModel.mostRatedGamesRecyclerViewAdapter,
            "fields name, cover, total_rating; where total_rating_count > 0 & aggregated_rating_count > 0; sort total_rating desc;"
        ){
               runOnUiThread {
                   // Stuff that updates the UI
                   mainActivityViewModel.mostRatedGamesRecyclerViewAdapter.importItems(it)
                   findViewById<CircularProgressIndicator>(R.id.lsMostRatedGames).visibility = View.GONE
               }
        }

        mainActivityViewModel.initializeRecyclerView(
            mainActivityViewModel.mostHypedGamesRecyclerView,
            mainActivityViewModel.mostHypedGamesRecyclerViewAdapter,
            "fields name, cover, total_rating; where total_rating_count > 0 & aggregated_rating_count > 0; sort hypes desc;"
        ){
            runOnUiThread {
                // Stuff that updates the UI
                mainActivityViewModel.mostHypedGamesRecyclerViewAdapter.importItems(it)
                findViewById<CircularProgressIndicator>(R.id.lsMostHypedGames).visibility = View.GONE
            }
        }

    }
}
