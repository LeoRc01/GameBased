package com.cip.cipstudio.viewmodel

import android.provider.ContactsContract.CommonDataKinds.StructuredName
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
import com.cip.cipstudio.dataSource.repository.RecentSearchesRepository
import com.cip.cipstudio.dataSource.repository.historyRepositoryImpl.RecentSearchesRepositoryLocal
import com.cip.cipstudio.model.User
import com.cip.cipstudio.utils.ActionGameDetailsEnum
import com.cip.cipstudio.utils.GameTypeEnum
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.collections.ArrayList


class SearchViewModel(val binding : FragmentSearchBinding) : ViewModel(){

    private val gameRepository : IGDBRepository = IGDBRepositoryRemote
    private lateinit var gamesResults : ArrayList<GameDetails>
    private lateinit var recentSearchResults : ArrayList<String>
    //private lateinit var filteredSearchResults : ArrayList<String>

    /*fun initializeRecyclerView(refresh : Boolean,
                               query:String,
                               updateUI : (List<GameDetails>)->Unit) {

        var gamesResults : ArrayList<GameDetails>
        viewModelScope.launch(Dispatchers.Main) {
            gamesResults = withContext(Dispatchers.IO) {
                gameRepository.searchGames(query, refresh) as ArrayList<GameDetails>
            }
            updateUI.invoke(gamesResults)
        }

    }*/

    val isPageLoading : MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>(false)
    }

    fun addGameResults(offset: Int, query: String, onSuccess: (List<GameDetails>) -> Unit) {
        isPageLoading.postValue(true)
        viewModelScope.launch(Dispatchers.Main){
            gamesResults = withContext(Dispatchers.IO){
                gameRepository.searchGames(query, offset) as ArrayList<GameDetails>
            }
            isPageLoading.postValue(false)
            onSuccess.invoke(gamesResults)
        }

    }

    fun addRecentSearches(offset: Int, query: String, searchDB: RecentSearchesRepository, onSuccess: (List<String>) -> Unit) {
        isPageLoading.postValue(true)

        viewModelScope.launch(Dispatchers.Main){
            recentSearchResults = withContext(Dispatchers.IO){
                User.getRecentlySearched(query, searchDB, offset) as ArrayList<String>
            }

            isPageLoading.postValue(false)

            /*filteredSearchResults = ArrayList()

            for (result in recentSearchResults.indices) {
                if(result contains(query))
                    filteredSearchResults += result.toString()
            }*/

            onSuccess.invoke(recentSearchResults)
            //onSuccess.invoke(filteredSearchResults)

        }

    }

}