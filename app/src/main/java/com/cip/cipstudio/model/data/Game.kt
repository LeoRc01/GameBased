package com.cip.cipstudio.model.data


import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.cip.cipstudio.repository.IGDBRepository

data class Game(val name : String, val gameId : Int) {

    var cover_url : String? = null

    fun getCover(onSuccess:(String)->Unit){
        if(cover_url==null){
            val gameRepo = IGDBRepository(generate = false)
            gameRepo.getGameCover(gameId){
                cover_url = it
                onSuccess.invoke(cover_url!!)
            }
        }


    }

}