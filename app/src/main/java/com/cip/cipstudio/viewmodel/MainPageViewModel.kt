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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainPageViewModel(val context: Context) : ViewModel() {

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
    fun initializeRecyclerView(recyclerView : RecyclerView,
                               adapter : GamesRecyclerViewAdapter,
                               getGames : suspend () -> List<GameDetails>,
                               updateUI : (List<GameDetails>)->Unit
    ){
        // Creo il layout manager (fondamentale)
        val manager = LinearLayoutManager(context)
        // Imposto l'orientamento a orizzontale
        manager.orientation = RecyclerView.HORIZONTAL
        // Setto il layoutmanager alla RV
        recyclerView.setLayoutManager(manager)
        recyclerView.setItemViewCacheSize(50)
        recyclerView.itemAnimator = null
        recyclerView.adapter = adapter

        var games :List<GameDetails> = listOf()
        viewModelScope.launch(Dispatchers.Main) {
            val job = launch(Dispatchers.IO) {
                games = getGames.invoke()
            }
            job.join()
            updateUI.invoke(games)
        }


    }

}