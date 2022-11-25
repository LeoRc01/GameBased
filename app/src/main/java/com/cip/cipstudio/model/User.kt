package com.cip.cipstudio.model

import android.util.Log
import com.cip.cipstudio.NotLoggedException
import com.cip.cipstudio.dataSource.repository.HistoryRepository
import com.cip.cipstudio.dataSource.repository.FirebaseRepository
import com.cip.cipstudio.model.entity.GameViewedHistoryEntry
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.database.DataSnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object User {
    private val TAG = User::class.java.simpleName

    var isUserLogged = false
    var uid: String? = null
    var email: String? = null
    var username: String? = null
    var photoUrl: String? = null
    private val auth = FirebaseAuth.getInstance()
    private val firebaseRepository = FirebaseRepository

    init {
        loginUser()
    }

    fun loginUser() {
        uid = auth.currentUser!!.uid
        email = auth.currentUser!!.email
        username = auth.currentUser!!.displayName
        photoUrl = auth.currentUser!!.photoUrl.toString()
        isUserLogged = true
    }

    fun login(email: String,
              password: String) : Task<AuthResult> {
        val task = auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                loginUser()
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
                loginUser()
            }
        return task
    }

    suspend fun addGamesToRecentlyViewed(gameIdAdd: String, db: HistoryRepository) {
        lateinit var gameViewedRecently: List<String>
        withContext(Dispatchers.Main) {
            gameViewedRecently = db.getFirstTenHistory()
            db.insert(gameIdAdd)

            if (isUserLogged) {
                if (gameViewedRecently.size == 10) {

                }
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
        if (!isUserLogged) {
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

    fun setGameToFavourite(gameId: String) : Task<Void> {
        if (!isUserLogged) {
            throw NotLoggedException()
        }
        return firebaseRepository.setGameToFavourite(gameId)
    }

    fun removeGameFromFavourite(gameId: String) : Task<Void> {
        if (!isUserLogged) {
            throw NotLoggedException()
        }
        return firebaseRepository.removeGameFromFavourite(gameId)
    }

    fun getFavouriteGames() : Task<DataSnapshot> {
        if (!isUserLogged) {
            throw NotLoggedException()
        }
        return firebaseRepository.getFavourites()
    }

    fun isGameFavourite(gameId: String) : Task<DataSnapshot?> {
        if (!isUserLogged) {
            throw NotLoggedException()
        }
        return firebaseRepository.isGameFavourite(gameId)
    }

}