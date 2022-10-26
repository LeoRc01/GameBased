package com.cip.cipstudio.viewmodel

import android.content.Context
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cip.cipstudio.R
import com.cip.cipstudio.adapters.GamesRecyclerViewAdapter
import com.cip.cipstudio.model.data.Game
import com.cip.cipstudio.repository.IGDBRepository
import com.google.android.material.progressindicator.CircularProgressIndicator

class MainActivityViewModel(val context : Context,
                            var mostRatedGamesRecyclerView : RecyclerView,
                            var mostRatedGamesRecyclerViewAdapter : GamesRecyclerViewAdapter,
                            var mostHypedGamesRecyclerView : RecyclerView,
                            var mostHypedGamesRecyclerViewAdapter : GamesRecyclerViewAdapter,
                            var gameRepo : IGDBRepository,
                            ) : ViewModel() {

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
                               payload : String,
                               updateUI : (ArrayList<Game>)->Unit
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

        IGDBRepository.ACCESS_TOKEN.observeForever(Observer{
            if(it!=null){
                Log.i("TOKEN ", it)
                gameRepo.getGamesByPayload(payload){
                    updateUI.invoke(it)
                }
            }
        })


    }

}