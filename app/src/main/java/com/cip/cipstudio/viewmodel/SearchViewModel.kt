package com.cip.cipstudio.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cip.cipstudio.dataSource.repository.IGDBRepository
import com.cip.cipstudio.databinding.FragmentSearchBinding
import com.cip.cipstudio.model.data.GameDetails
import com.cip.cipstudio.dataSource.repository.IGDBRepositoryImpl.IGDBRepositoryRemote
import com.cip.cipstudio.dataSource.repository.RecentSearchesRepository
import com.cip.cipstudio.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.collections.ArrayList


class SearchViewModel(val binding : FragmentSearchBinding) : ViewModel(){

    private val gameRepository : IGDBRepository = IGDBRepositoryRemote
    private lateinit var gamesResults : ArrayList<GameDetails>
    private lateinit var recentSearchResults : ArrayList<String>

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

    fun addSearchSuggestions(query: String, onSuccess: (List<String>) -> Unit) {

        val searchSuggestionsResults = arrayListOf<String>()

        isPageLoading.postValue(true)
        viewModelScope.launch(Dispatchers.Main){
            gamesResults = withContext(Dispatchers.IO){
                gameRepository.getSearchSuggestions(query) as ArrayList<GameDetails>
            }
            isPageLoading.postValue(false)

            for (game in gamesResults)
                searchSuggestionsResults += game.name

            onSuccess.invoke(searchSuggestionsResults)
        }

    }

    fun addRecentSearches(offset: Int, query: String, searchDB: RecentSearchesRepository, onSuccess: (List<String>) -> Unit) {
        isPageLoading.postValue(true)

        viewModelScope.launch(Dispatchers.Main){
            recentSearchResults = withContext(Dispatchers.IO){
                User.getRecentlySearched(query, searchDB, offset) as ArrayList<String>
            }
            isPageLoading.postValue(false)
            onSuccess.invoke(recentSearchResults)

        }

    }

}