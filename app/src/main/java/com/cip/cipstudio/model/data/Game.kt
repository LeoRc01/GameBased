package com.cip.cipstudio.model.data


import android.util.Log
import androidx.databinding.BindingAdapter
import androidx.lifecycle.MutableLiveData
import com.cip.cipstudio.model.data.util.toDate
import com.cip.cipstudio.repository.IGDBRepository
import org.json.JSONArray
import java.sql.Timestamp
import java.io.Serializable

data class Game(val name : String,
                val description : String,
                val releaseDate : String,
                val userRatingValue : Int,
                val userRatingCount : Int,
                val criticsRatingValue : Int,
                val criticsRatingCount : Int,
                val platformsId : ArrayList<Int>,
                val genreIds : ArrayList<Int>,
                val similarGamesIds : ArrayList<Int>,
                val screenShotIds : ArrayList<Int>,
                //val platforms : ArrayList<Platform>,
                val gameId : Int,
                ) : Serializable{

    var cover_url : String? = null

    fun getCover(onSuccess:(String)->Unit){
        if(cover_url==null){
            val gameRepo = IGDBRepository(generate = false)
            gameRepo.getGameCover(gameId){
                cover_url = it
                cover_url = cover_url!!.replace("t_thumb", "t_cover_big")
                onSuccess.invoke(cover_url!!)
            }
        }
    }

}