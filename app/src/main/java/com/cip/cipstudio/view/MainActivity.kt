package com.cip.cipstudio.view

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.api.igdb.request.IGDBWrapper
import com.cip.cipstudio.R
import com.cip.cipstudio.repository.IGDBRepository
import com.cip.cipstudio.view.widgets.LoadingSpinner as LoadingSpinner

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
/*
        val sharedPref = this.getPreferences(Context.MODE_PRIVATE)
        val gameRepo : IGDBRepository = IGDBRepository()
        val saved_access_token : String = sharedPref!!.getString("ACCESS_TOKEN", "NOT_SET")!!
        if(saved_access_token.equals("NOT_SET")){
            gameRepo.generateAccessToken()
        }
*/
        

        val gameRepo : IGDBRepository = IGDBRepository()
        LoadingSpinner.showLoadingDialog(this)

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
        


    }
}
