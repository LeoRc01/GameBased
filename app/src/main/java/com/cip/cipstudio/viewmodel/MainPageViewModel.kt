package com.cip.cipstudio.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cip.cipstudio.dataSource.repository.AISelector
import com.cip.cipstudio.dataSource.repository.FirebaseRepository
import com.cip.cipstudio.dataSource.repository.IGDBRepository.IGDBRepository
import com.cip.cipstudio.dataSource.repository.IGDBRepository.IGDBRepositoryRemote
import com.cip.cipstudio.model.User
import com.cip.cipstudio.model.data.GameDetails
import com.cip.cipstudio.utils.GameTypeEnum
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class MainPageViewModel() : ViewModel() {

    private val TAG = "MainPageViewModel"
    private val lock = Any()
    private val gameRepository : IGDBRepository = IGDBRepositoryRemote

    constructor(onSuccess: () -> Unit) : this() {
        viewModelScope.launch(Dispatchers.Main) {
            if(User.isLogged()){
                val snapshot = FirebaseRepository.getFYP().await()
                AISelector.fetchDataFromFirebase(snapshot)
            }
            onSuccess.invoke()
        }
    }

    /**
     * initializeRecyclerView:
     *
     * - Imposta il layout manager
     *
     * - inizializza una recyclerview passata come parametro
     *
     * - gli imposta l'adapter
     *
     * - esegue la query al DB con il payload indicato
     *
     * - aggiorna la UI tramite la funzione updateUI passata come parametro
     */
    fun initializeRecyclerView(gameTypeEnum: GameTypeEnum,
                               refresh : Boolean,
                               updateUI : (List<GameDetails>)->Unit) {
        var games :List<GameDetails>
        viewModelScope.launch(Dispatchers.Main) {
            games = withContext(Dispatchers.IO) {
                gameRepository.getGamesByType(gameTypeEnum, refresh)
            }
            updateUI.invoke(games)
        }
    }

}