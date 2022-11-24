package com.cip.cipstudio.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cip.cipstudio.R
import com.cip.cipstudio.adapters.GamesBigRecyclerViewAdapter
import com.cip.cipstudio.databinding.FragmentSearchBinding
import com.cip.cipstudio.model.data.GameDetails
import com.cip.cipstudio.repository.IGDBRepositoryRemote
import com.cip.cipstudio.repository.MyFirebaseRepository
import com.cip.cipstudio.utils.ActionGameDetailsEnum
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class SearchViewModel(val binding : FragmentSearchBinding) : ViewModel(){

    val isPageLoading : MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>(true)
    }

    private lateinit var lastViewdGames : ArrayList<GameDetails>
    private lateinit var lastViewGamesRecyclerViewAdapter: GamesBigRecyclerViewAdapter

    init {

        MyFirebaseRepository.getInstance().getRecentlyViewedGames().addOnSuccessListener {
            if(it!=null && it.exists()){ // controllo ridondante?
                var orderedGameIds : ArrayList<String> = getOrderedGameIds((it.value as Map<String, Map<String, Any>>))

                viewModelScope.launch(Dispatchers.Main){

                    val job = viewModelScope.launch(Dispatchers.IO) {
                        lastViewdGames = orderGameList(orderedGameIds, IGDBRepositoryRemote.getGamesByIds(orderedGameIds) as ArrayList<GameDetails>)
                    }
                    job.join()

                    initializeRecyclerView(lastViewdGames)

                    isPageLoading.postValue(false)
                }
            }

        }
    }

    fun getOrderedGameIds(elements : Map<String, Map<String, Any>>) : ArrayList<String>{
        val orderedListByDateTime = elements
            .toList()
            .sortedBy { (_, value) ->
                value["dateTime"] as Long
            }.reversed()
            .toMap()
        var orderedGameIds : ArrayList<String> = arrayListOf()

        orderedListByDateTime.forEach {
            orderedGameIds.add(it.value["gameId"].toString())
        }

        return orderedGameIds
    }

    fun orderGameList(_orderedGameIds : ArrayList<String>, _unorderedGameList : ArrayList<GameDetails>) : ArrayList<GameDetails>{
        val orderedGameList = arrayListOf<GameDetails>()
        _orderedGameIds.forEach {
            _unorderedGameList.forEach { game ->
                if(game.id == it){
                    orderedGameList.add(game)
                    return@forEach
                }
            }
        }
        return orderedGameList
    }

    fun initializeRecyclerView(games : ArrayList<GameDetails>) {
        lastViewGamesRecyclerViewAdapter =
            GamesBigRecyclerViewAdapter(binding.root.context,
                games)
        val manager = LinearLayoutManager(binding.root.context)
        manager.orientation = RecyclerView.HORIZONTAL
        binding.fSearchRvLastViewedGames.layoutManager = manager
        binding.fSearchRvLastViewedGames.setItemViewCacheSize(50)
        binding.fSearchRvLastViewedGames.itemAnimator = null
        binding.fSearchRvLastViewedGames.adapter = lastViewGamesRecyclerViewAdapter
    }

}