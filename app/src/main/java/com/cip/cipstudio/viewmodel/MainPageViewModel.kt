package com.cip.cipstudio.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cip.cipstudio.adapters.GamesRecyclerViewAdapter
import com.cip.cipstudio.model.data.Game
import com.cip.cipstudio.repository.IGDBRepository

class MainPageViewModel(val context: Context) : ViewModel() {

    private val TAG = "MainPageViewModel"

    private val gameRepository = IGDBRepository()
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
        Log.i("PAYLOAD", payload)
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
                gameRepository.getGamesByPayload(payload){
                    updateUI.invoke(it)
                }
            }
        })


    }

}