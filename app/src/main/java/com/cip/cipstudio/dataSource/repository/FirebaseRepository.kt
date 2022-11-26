package com.cip.cipstudio.dataSource.repository

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase

object FirebaseRepository {
    private var db : FirebaseDatabase? = null
    private lateinit var userId : String

    init{
        db = FirebaseDatabase.getInstance("https://cip-studio-default-rtdb.europe-west1.firebasedatabase.app")
        login()
    }

    fun login() {
        if (FirebaseAuth.getInstance().currentUser != null) {
            userId = FirebaseAuth.getInstance().currentUser!!.uid
        }
    }

    fun addGamesToRecentlyViewed(gameIdToAdd : String, gameIdToDelete : String? = null) : Task<Void> {
        val data : Map<String, Any> = hashMapOf("dateTime" to System.currentTimeMillis())
        val ref = db!!.getReference("users/$userId/recentlyViewed")
        val task = ref.child(gameIdToAdd).setValue(data)
        if(gameIdToDelete != null){
            ref.child(gameIdToDelete).removeValue()
        }
        return task
    }

    fun getRecentlyViewedGames() : Task<DataSnapshot>{
        return db!!
            .getReference("users/$userId/recentlyViewed")
            .get()
    }

    fun setGameToFavourite(gameId : String) : Task<Void> {
        return db!!
            .getReference("users/$userId/favourites")
            .child(gameId).setValue(gameId)
    }

    fun removeGameFromFavourite(gameId : String) : Task<Void>{
        return db!!
            .getReference("users/$userId/favourites")
            .child(gameId)
            .removeValue()
    }

    fun getFavourites() : Task<DataSnapshot>{
        return db!!
            .getReference("users/$userId/favourites").get()
    }


    /**
     * se viene ritornato null allora il gioco non è tra i preferiti
     */
    fun isGameFavourite(gameId : String) : Task<DataSnapshot?>{
        return db!!
            .getReference("users/$userId/favourites/$gameId")
            .get()
    }

}