package com.cip.cipstudio.model

import android.net.Uri
import android.util.Log
import com.cip.cipstudio.dataSource.repository.AISelector
import com.cip.cipstudio.exception.NotLoggedException
import com.cip.cipstudio.dataSource.repository.historyRepository.HistoryRepository
import com.cip.cipstudio.dataSource.repository.FirebaseRepository
import com.cip.cipstudio.model.data.AIModel
import com.cip.cipstudio.dataSource.repository.recentSearchesRepository.RecentSearchesRepository
import com.cip.cipstudio.model.entity.GameViewedHistoryEntry
import com.cip.cipstudio.model.entity.RecentSearchesHistoryEntry
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks.forException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.database.DataSnapshot
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.util.Base64

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
            downloadUrl = null

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

    suspend fun getRecentlyViewed(historyDB: HistoryRepository, offset: Int = 0) : List<String> {
        return historyDB.getHistory(uid, pageIndex= offset)
    }

    suspend fun addGamesToRecentlyViewed(gameIdAdd: String, historyDB: HistoryRepository) {
        lateinit var gameViewedRecently: List<String>
        retrieveDataFromCurrentUser()
        withContext(Dispatchers.Main) {
            gameViewedRecently = historyDB.getHistory(uid)
            historyDB.insert(gameIdAdd, uid)

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

    fun syncFromRemote(historyDB: HistoryRepository, recentSearchesDB: RecentSearchesRepository) {
        if (!isLogged()) {
            throw NotLoggedException()
        }
        firebaseRepository.getRecentlyViewedGames().addOnSuccessListener {
            if (it.value != null) {
                val games = (it.value as Map<*, *>).map { el ->
                    val a = el.value as Map<*,*>
                    val idGame = el.key as String
                    GameViewedHistoryEntry(idGame, uid, a["dateTime"] as Long)
                }

                GlobalScope.launch(Dispatchers.IO) {
                    historyDB.syncHistory(games)
                }
            }
        }

        firebaseRepository.getRecentlySearchesQueries().addOnSuccessListener {
            if (it.value != null) {
                val queries = (it.value as Map<*, *>).map { el ->
                    val a = el.value as Map<*,*>
                    val query = el.key as String

                    try {
                        val decodedQuery = String(Base64.decode(query, Base64.NO_WRAP))
                        RecentSearchesHistoryEntry(decodedQuery, uid, a["dateTime"] as Long)
                    }
                    catch (iae : IllegalArgumentException) {
                        RecentSearchesHistoryEntry(query, uid, a["dateTime"] as Long)
                    }
                }

                GlobalScope.launch(Dispatchers.IO) {
                    recentSearchesDB.syncRecentSearches(queries)
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
        AISelector.weightedItems = arrayListOf<AIModel>()
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

    suspend fun deleteHistory(historyDB: HistoryRepository) {
        retrieveDataFromCurrentUser()
        withContext(Dispatchers.Main) {
           historyDB.deleteAll(uid)

            if (isLogged()) {
                firebaseRepository.deleteGamesFromRecentlyViewed()
            }
        }
    }

    suspend fun deleteUser(historyDB: HistoryRepository, searchDB: RecentSearchesRepository) : Task<*> {
        if (!isLogged()) {
            throw NotLoggedException()
        }
        retrieveDataFromCurrentUser()
        historyDB.deleteAll(uid)
        searchDB.deleteAll(uid)
        storageReference!!.delete().addOnSuccessListener {
            Log.i(TAG, "Image deleted successfully")
        }
        return Firebase.auth.currentUser!!.delete().addOnSuccessListener {
            retrieveDataFromCurrentUser()
        }

    }

    suspend fun getRecentSearches(query: String, recentSearchDB: RecentSearchesRepository, offset: Int = 0) : List<String> {
        return recentSearchDB.getRecentSearches(query, userId = uid, pageIndex= offset)
    }

    suspend fun deleteQueryFromRecentSearches(query: String, recentSearchesDB: RecentSearchesRepository) {
        retrieveDataFromCurrentUser()
        withContext(Dispatchers.Main) {
            recentSearchesDB.delete(query, uid)
            firebaseRepository.deleteQueryFromRecentlySearch(query)
        }
    }

    suspend fun deleteRecentSearches(recentSearchesDB: RecentSearchesRepository) {
        retrieveDataFromCurrentUser()
        withContext(Dispatchers.Main) {
            recentSearchesDB.deleteAll(uid)
            firebaseRepository.deleteAllQueriesFromRecentlySearch()
        }
    }

    suspend fun addSearchToRecentlySearched(query: String, recentSearchesDB: RecentSearchesRepository) {
        retrieveDataFromCurrentUser()
        withContext(Dispatchers.Main) {
            val recentSearches = recentSearchesDB.getRecentSearches(query, uid)
            recentSearchesDB.insert(query, uid)
            if (isLogged()) {
                val queryToDelete =
                    if (recentSearches.size == 10 && recentSearches.last() != query)
                        recentSearches.last()
                    else
                        null
                firebaseRepository.addQueryToRecentlySearch(query, queryToDelete)
            }
        }
    }

}