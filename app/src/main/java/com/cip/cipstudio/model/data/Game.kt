package com.cip.cipstudio.model.data


import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.cip.cipstudio.repository.MyFirebaseRepository
import com.cip.cipstudio.repository.IGDBRepository
import com.google.android.gms.tasks.Task
import java.io.Serializable

data class Game(
    var name : String,
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
    var isGameFavourite : Boolean,
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

    fun setGameToFavourite() : Task<Void> {
        return MyFirebaseRepository.getInstance().setGameToFavourite(gameId.toString()).addOnSuccessListener {
            isGameFavourite = true
        }
    }

    fun removeGameFromFavourite() : Task<Void> {
        return MyFirebaseRepository.getInstance().removeGameFromFavourite(gameId.toString()).addOnSuccessListener {
            isGameFavourite = false
        }
    }

}