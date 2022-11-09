package com.cip.cipstudio.repository

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase

/**
 * lazy Singleton
 */
class MyFirebaseRepository {

    companion object{

        private var instance : MyFirebaseRepository? = null
        private var db : FirebaseDatabase? = null
        private val userId : String = FirebaseAuth.getInstance().currentUser!!.uid

        fun getInstance() : MyFirebaseRepository {
            if(instance == null && db == null){
                instance = MyFirebaseRepository()
                db = FirebaseDatabase.getInstance("https://cip-studio-default-rtdb.europe-west1.firebasedatabase.app")
                return instance!!
            }
            return instance!!
        }


    }

    fun setGameToFavourite(gameId : String) : Task<Void> {
        return db!!
            .getReference("users").child(userId).child("favourites").child(gameId).setValue(gameId)
    }
    fun removeGameFromFavourite(gameId : String) : Task<Void>{
        return db!!
            .getReference("users")
            .child(userId)
            .child("favourites")
            .child(gameId)
            .removeValue()
    }

    fun getFavorites() : Task<DataSnapshot>{
        return db!!
            .getReference("users")
            .child(userId)
            .child("favourites").get()
    }


    /**
     * se viene ritornato null allora il gioco non è tra i preferiti
     */
    fun isGameFavourite(gameId : String) : Task<DataSnapshot?>{
        return db!!
            .getReference("users")
            .child(userId)
            .child("favourites")
            .child(gameId)
            .get()
    }

}