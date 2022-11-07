package com.cip.cipstudio.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cip.cipstudio.adapters.GamesRecyclerViewAdapter
import com.cip.cipstudio.model.data.Game
import com.cip.cipstudio.model.data.GameDetailsJson
import com.cip.cipstudio.repository.IGDBRepository
import com.cip.cipstudio.repository.IGDBRepositoryRemote
import com.cip.cipstudio.repository.IGDBRepositorydwa
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray

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
                               getGames : suspend () -> List<GameDetailsJson>,
                               updateUI : (List<GameDetailsJson>)->Unit
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

        var games :List<GameDetailsJson> = listOf()
        viewModelScope.launch(Dispatchers.Main) {
            val job = launch(Dispatchers.IO) {
                games = getGames.invoke()
            }
            job.join()
            updateUI.invoke(games)
        }


    }

}