package com.cip.cipstudio.model

import android.util.Log
import com.cip.cipstudio.NotLoggedException
import com.cip.cipstudio.dataSource.repository.HistoryRepository
import com.cip.cipstudio.dataSource.repository.FirebaseRepository
import com.cip.cipstudio.model.entity.GameViewedHistoryEntry
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks.forException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.database.DataSnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

object User {
    private val TAG = User::class.java.simpleName

    lateinit var uid: String
    var email: String? = null
    var username: String? = null
    var photoUrl: String? = null
    private val auth = FirebaseAuth.getInstance()
    private val firebaseRepository = FirebaseRepository

    init {
        retrieveDataFromCurrentUser()
    }

    private fun retrieveDataFromCurrentUser() {
        if (isLogged()) {
            uid = auth.currentUser!!.uid
            email = auth.currentUser!!.email
            username = auth.currentUser!!.displayName
            photoUrl = auth.currentUser!!.photoUrl.toString()
        }
        else {
            uid = UUID.randomUUID().toString()
        }
    }

    fun login(email: String,
              password: String) : Task<AuthResult> {
        val task = auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                retrieveDataFromCurrentUser()
            }
        return task
    }

    fun register(email: String,
                 password: String,
                 username: String) : Task<AuthResult> {
        val task = auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                val usernameUpdates = userProfileChangeRequest { displayName = username }
                auth.currentUser!!.updateProfile(usernameUpdates)
                    .addOnSuccessListener {
                        Log.i(TAG, "Username registered successfully")
                    }
                retrieveDataFromCurrentUser()
            }
        return task
    }

    suspend fun addGamesToRecentlyViewed(gameIdAdd: String, db: HistoryRepository) {
        lateinit var gameViewedRecently: List<String>
        withContext(Dispatchers.Main) {
            gameViewedRecently = db.getFirstTenHistory()
            db.insert(gameIdAdd)

            if (isLogged()) {
                val gameIdDelete =
                    if (gameViewedRecently.size == 10 && gameViewedRecently.last() != gameIdAdd)
                        gameViewedRecently.last()
                    else
                        null
                firebaseRepository.addGamesToRecentlyViewed(gameIdAdd, gameIdDelete)
            }
        }
    }

    fun syncRecentlyViewedGames(db: HistoryRepository) {
        if (!isLogged()) {
            throw NotLoggedException()
        }
        firebaseRepository.getRecentlyViewedGames().addOnSuccessListener {
            if (it.value != null) {
                val games = (it.value as Map<*, *>).map { el ->
                    val a = el.value as Map<*,*>
                    val id = el.key as String
                    GameViewedHistoryEntry(id, a["dateTime"] as Long)
                }

                GlobalScope.launch(Dispatchers.IO) {
                    db.syncHistory(games)
                }
            }
        }
    }

    fun setGameToFavourite(gameId: String) : Task<*> {
        if (!isLogged()) {
            return forException<DataSnapshot>(NotLoggedException())
        }
        return firebaseRepository.setGameToFavourite(gameId)
    }

    fun removeGameFromFavourite(gameId: String) : Task<*> {
        if (!isLogged()) {
            return forException<DataSnapshot>(NotLoggedException())
        }
        return firebaseRepository.removeGameFromFavourite(gameId)
    }

    fun getFavouriteGames() : Task<DataSnapshot>{
        if (!isLogged()) {
            return forException<DataSnapshot>(NotLoggedException())
        }
        return firebaseRepository.getFavourites()
    }

    fun isGameFavourite(gameId: String) : Task<DataSnapshot?> {
        if (!isLogged()) {
            return forException<DataSnapshot>(NotLoggedException())
        }

        return firebaseRepository.isGameFavourite(gameId)
    }

    fun isLogged() : Boolean {
        return auth.currentUser != null
    }

}