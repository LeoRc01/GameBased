package com.cip.cipstudio.repository

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

/**
 * lazy Singleton
 */
class MyFirebaseRepository {

    companion object{

        private var instance : MyFirebaseRepository? = null
        private var db : FirebaseFirestore? = null
        private val userId : String = FirebaseAuth.getInstance().currentUser!!.uid

        fun getInstance() : MyFirebaseRepository {
            if(instance == null && db == null){
                instance = MyFirebaseRepository()
                db = FirebaseFirestore.getInstance()
                return instance!!
            }
            return instance!!
        }


    }

    fun getFavorites() : Task<QuerySnapshot>{
        return db!!
            .collection("users")
            .document(userId)
            .collection("favourites")
            .get()
    }

    fun setGameToFavourite(gameId : String) : Task<Void>{
        return db!!
            .collection("users")
            .document(userId)
            .collection("favourites")
            .document(gameId)
            .set(hashMapOf("gameId" to gameId))
    }

    fun removeGameFromFavourite(gameId : String) : Task<Void>{
        return db!!
            .collection("users")
            .document(userId)
            .collection("favourites")
            .document(gameId)
            .delete()
    }


    /**
     * se viene ritornato null allora il gioco non è tra i preferiti
     */
    fun isGameFavourite(gameId : String) : Task<DocumentSnapshot?>{
        return db!!
            .collection("users")
            .document(userId)
            .collection("favourites")
            .document(gameId)
            .get()
    }

}