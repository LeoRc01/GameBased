package com.cip.cipstudio.viewmodel

import android.util.Log
import android.view.View
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
import com.cip.cipstudio.utils.ActionGameDetailsEnum
import com.cip.cipstudio.utils.GameTypeEnum
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class SearchViewModel(val binding : FragmentSearchBinding) : ViewModel(){

    private val gameRepository : IGDBRepository = IGDBRepositoryRemote

    fun initializeRecyclerView(refresh : Boolean,
                               query:String,
                               updateUI : (List<GameDetails>)->Unit) {

        var gamesResults : ArrayList<GameDetails>
        viewModelScope.launch(Dispatchers.Main) {
            gamesResults = withContext(Dispatchers.IO) {
                gameRepository.searchGames(query, refresh) as ArrayList<GameDetails>
            }
            updateUI.invoke(gamesResults)
        }

    }

}