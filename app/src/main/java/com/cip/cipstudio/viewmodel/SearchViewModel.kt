package com.cip.cipstudio.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cip.cipstudio.adapters.GamesBigRecyclerViewAdapter
import com.cip.cipstudio.dataSource.repository.HistoryRepository
import com.cip.cipstudio.dataSource.repository.IGDBRepository
import com.cip.cipstudio.databinding.FragmentSearchBinding
import com.cip.cipstudio.model.data.GameDetails
import com.cip.cipstudio.dataSource.repository.IGDBRepositoryImpl.IGDBRepositoryRemote
import com.cip.cipstudio.dataSource.repository.historyRepositoryImpl.HistoryRepositoryLocal
import com.cip.cipstudio.model.User
import com.cip.cipstudio.utils.GameTypeEnum
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class SearchViewModel(val binding : FragmentSearchBinding) : ViewModel(){

    var searchtext : MutableLiveData<String> = MutableLiveData()

    private val gameRepository : IGDBRepository = IGDBRepositoryRemote

    fun initializeRecyclerView(refresh : Boolean,
                               updateUI : (List<GameDetails>)->Unit) {

        var games :List<GameDetails>
        viewModelScope.launch(Dispatchers.Main) {
            games = withContext(Dispatchers.IO) {
                gameRepository.searchGames(searchtext, refresh)
            }
            updateUI.invoke(games)
        }


    }

    /*

    val isPageLoading : MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>(true)
    }

    private lateinit var lastViewdGames : ArrayList<GameDetails>
    private lateinit var lastViewGamesRecyclerViewAdapter: GamesBigRecyclerViewAdapter

    private val historyRepository: HistoryRepository = HistoryRepositoryLocal(binding.root.context)

    init {

        viewModelScope.launch(Dispatchers.Main){
            val list = User.getRecentlyViewed(historyRepository)

            lastViewdGames = withContext(Dispatchers.IO){
                IGDBRepositoryRemote.getGamesByIds(list, false) as ArrayList<GameDetails>
            }

            initializeRecyclerView(lastViewdGames.sortedBy { list.indexOf(it.id) })

            isPageLoading.postValue(false)
        }



    }

    private fun initializeRecyclerView(games : List<GameDetails>) {
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

    */

}