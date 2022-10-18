package com.cip.cipstudio.view

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.HORIZONTAL
import androidx.recyclerview.widget.RecyclerView.Orientation
import com.cip.cipstudio.R
import com.cip.cipstudio.adapters.MostRatedGamesRecyclerViewAdapter
import com.cip.cipstudio.model.data.Game
import com.cip.cipstudio.repository.IGDBRepository
import com.google.android.material.progressindicator.CircularProgressIndicator


class MainActivity : AppCompatActivity() {

    private lateinit var mostRatedGamesRecyclerView : RecyclerView
    private lateinit var mostRatedGamesRecyclerViewAdapter : MostRatedGamesRecyclerViewAdapter
    private lateinit var gameRepo : IGDBRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        gameRepo = IGDBRepository()

        supportActionBar!!.hide()


        // Nascondo la listview mentre caricano gli item
        //mostRatedGamesRecyclerView.visibility = View.GONE
        initializeMostRatedGamesRV()


/*
        val sharedPref = this.getPreferences(Context.MODE_PRIVATE)
        val gameRepo : IGDBRepository = IGDBRepository()
        val saved_access_token : String = sharedPref!!.getString("ACCESS_TOKEN", "NOT_SET")!!
        if(saved_access_token.equals("NOT_SET")){
            gameRepo.generateAccessToken()
        }
*/
        


        //LoadingSpinner.showLoadingDialog(this)

        /*

        gameRepo.ACCESS_TOKEN.observe(this, Observer{
            if(it!=null){

                Log.i("ACCESS_TOKEN", it)
                //IGDBWrapper.setCredentials(gameRepo.getClientID(), it)
                gameRepo.getGame(){
                    LoadingSpinner.dismiss()
                }
                // Kotlin Example
                // val bytes = apiProtoRequest(Endpoints.GAMES,  "fields *;")
                // val listOfGames: List<Game> = GameResult.parseFrom(bytes).gamesList
            }else{
                LoadingSpinner.showLoadingDialog(this)
            }
        })

        */

        


    }


    fun initializeMostRatedGamesRV(){

        // Imposto la RV
        mostRatedGamesRecyclerView = findViewById(R.id.rvMostRatedGames)

        // Creo il layout manager (fondamentale)
        val manager = LinearLayoutManager(this)
        // Imposto l'orientamento a orizzontale
        manager.orientation = HORIZONTAL
        // Setto il layoutmanager alla RV
        mostRatedGamesRecyclerView.setLayoutManager(manager)
        mostRatedGamesRecyclerView.setItemViewCacheSize(50)
        mostRatedGamesRecyclerView.itemAnimator = null


        var games : ArrayList<Game> = ArrayList<Game>()
        mostRatedGamesRecyclerViewAdapter = MostRatedGamesRecyclerViewAdapter(this, games)
        mostRatedGamesRecyclerView.adapter = mostRatedGamesRecyclerViewAdapter

        IGDBRepository.ACCESS_TOKEN.observe(this, Observer{
            if(it!=null){
                gameRepo.getMostRatedGames(){
                    runOnUiThread {
                        // Stuff that updates the UI
                        mostRatedGamesRecyclerViewAdapter.importItems(it)
                        findViewById<CircularProgressIndicator>(R.id.lsMostRatedGames).visibility = View.GONE
                    }
                }
            }
        })
    }
}
