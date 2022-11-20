package com.cip.cipstudio.repository

import android.util.Log
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

    fun addGamesToRecentlyViewed(gameId : String) : Task<Void>{
        val now = System.currentTimeMillis()
        val data : Map<String, Any> = hashMapOf("gameId" to gameId,
                                                "dateTime" to now)
        /*
            Primo metodo: leggere tutti i documenti, contarli,
            se sono >= 10 allora elimino il documento con dateTime minore.

            Problema! vado a effettuare X letture dove X è il numero di dati salvati.

            Secondo metodo: utilizzo una variabile di appoggio counter.
            Quando aggiungo un gioco ai recently viewed allora aggiorno quella variabile.
            Se counter >= 10 elimino il gioco con dateTime minore.

            Problema! Quando inizializzo counter a 0? Durante la registrazione dell'utente?
         */

        return db!!
            .getReference("users")
            .child(userId)
            .child("recentlyViewed")
            .child(gameId)
            .setValue(data).addOnSuccessListener {
                getRecentlyViewedGames().addOnSuccessListener {
                    if(it.value!=null){
                        val counter : Int = (it.value as Map<*, *>).size
                        if(counter > 10){
                            var lastElementId : String = ""
                            var lastElementTimeStamp = 0L
                            (it.value as Map<*, *>).forEach { el ->
                                lastElementId = (el.value as Map<*, *>).get("gameId").toString()
                                lastElementTimeStamp = (el.value as Map<*, *>).get("dateTime") as Long
                                return@forEach
                            }

                            (it.value as Map<*, *>).forEach { el ->
                                val data = el.value as Map<*, *>
                                if(lastElementTimeStamp > data["dateTime"] as Long){
                                    lastElementTimeStamp = data["dateTime"] as Long
                                    lastElementId = data["gameId"].toString()
                                }
                            }
                            if(gameId!=lastElementId){
                                db!!
                                    .getReference("users")
                                    .child(userId)
                                    .child("recentlyViewed")
                                    .child(lastElementId)
                                    .removeValue()
                            }
                        }
                    }
                }
            }
    }

    fun getRecentlyViewedGames() : Task<DataSnapshot>{
        return db!!
            .getReference("users")
            .child(userId)
            .child("recentlyViewed")
            .get()
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