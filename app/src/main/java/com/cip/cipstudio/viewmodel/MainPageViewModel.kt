package com.cip.cipstudio.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cip.cipstudio.adapters.GamesRecyclerViewAdapter
import com.cip.cipstudio.model.data.GameDetails
import com.cip.cipstudio.repository.IGDBRepository
import com.cip.cipstudio.repository.IGDBRepositoryRemote
import com.cip.cipstudio.utils.GameTypeEnum
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainPageViewModel() : ViewModel() {

    private val TAG = "MainPageViewModel"

    private val gameRepository : IGDBRepository = IGDBRepositoryRemote
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