package com.cip.cipstudio.model

import android.net.Uri
import android.util.Log
import com.cip.cipstudio.exception.NotLoggedException
import com.cip.cipstudio.dataSource.repository.HistoryRepository
import com.cip.cipstudio.dataSource.repository.FirebaseRepository
import com.cip.cipstudio.model.entity.GameViewedHistoryEntry
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks.forException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object User {
    private val TAG = User::class.java.simpleName

    lateinit var uid: String
    var email: String? = null
    var username: String? = null
    var downloadUrl : Uri? = null
    private val auth = FirebaseAuth.getInstance()
    private val firebaseRepository = FirebaseRepository
    private var storageReference : StorageReference? = null

    init {
        retrieveDataFromCurrentUser()
    }

    private fun retrieveDataFromCurrentUser() {
        if (isLogged()) {
            uid = auth.currentUser!!.uid
            email = auth.currentUser!!.email
            username = auth.currentUser!!.displayName
            firebaseRepository.login()
            storageReference = FirebaseStorage.getInstance().getReference("/images/${uid}")
            retrieveUrlDownload()
        }
        else {
            uid = "guest"
            email = null
            username = null
            storageReference = null

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

    suspend fun getRecentlyViewed(db: HistoryRepository) : List<String> {
        return db.getHistory(uid)
    }

    suspend fun addGamesToRecentlyViewed(gameIdAdd: String, db: HistoryRepository) {
        lateinit var gameViewedRecently: List<String>
        retrieveDataFromCurrentUser()
        withContext(Dispatchers.Main) {
            gameViewedRecently = db.getHistory(uid)
            db.insert(gameIdAdd, uid)

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
                    GameViewedHistoryEntry(id, uid, a["dateTime"] as Long)
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

    fun logout() {
        if (!isLogged()) {
            throw NotLoggedException()
        }

        auth.signOut()
        retrieveDataFromCurrentUser()
    }

    private fun retrieveUrlDownload() {
        storageReference!!.downloadUrl.addOnSuccessListener {
            downloadUrl = it
            Picasso.get().load(downloadUrl)
        }
    }

    fun uploadImage(selectedPhotoUri: Uri?) : Task<*> {
        if (!isLogged()) {
            return forException<DataSnapshot>(NotLoggedException())
        }

        return storageReference!!.putFile(selectedPhotoUri!!).addOnSuccessListener {
            retrieveUrlDownload()
        }
    }

    fun reauthenticate(email: String, password: String) : Task<*> {
        if (!isLogged()) {
            return forException<Uri>(NotLoggedException())
        }

        val credential = EmailAuthProvider.getCredential(email, password)
        return auth.currentUser!!.reauthenticate(credential)
    }

    fun updateEmail(email: String): Task<*> {
        if (!isLogged()) {
            return forException<Uri>(NotLoggedException())
        }

        return auth.currentUser?.updateEmail(email)!!.addOnSuccessListener {
            retrieveDataFromCurrentUser()
        }
    }

    fun updatePassword(newPassword: String): Task<*> {
        if (!isLogged()) {
            return forException<Uri>(NotLoggedException())
        }

        return auth.currentUser?.updatePassword(newPassword)!!.addOnSuccessListener {
            logout() }
    }

    fun updateUsername(changeRequest: UserProfileChangeRequest) : Task<*>? {
        if (!isLogged()) {
            return forException<Uri>(NotLoggedException())
        }

        return auth.currentUser?.updateProfile(changeRequest)?.addOnSuccessListener {
            retrieveDataFromCurrentUser()
        }
    }

    suspend fun deleteHistory(db: HistoryRepository) {
        retrieveDataFromCurrentUser()
        withContext(Dispatchers.Main) {
           db.deleteAll(uid)

            if (isLogged()) {
                firebaseRepository.deleteGamesFromRecentlyViewed()
            }
        }
    }
}