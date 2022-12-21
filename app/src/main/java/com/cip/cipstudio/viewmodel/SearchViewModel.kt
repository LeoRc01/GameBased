package com.cip.cipstudio.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cip.cipstudio.dataSource.filter.Filter
import com.cip.cipstudio.dataSource.filter.criteria.Criteria
import com.cip.cipstudio.dataSource.repository.IGDBRepository.IGDBRepository
import com.cip.cipstudio.databinding.FragmentSearchBinding
import com.cip.cipstudio.model.data.GameDetails
import com.cip.cipstudio.dataSource.repository.IGDBRepository.IGDBRepositoryRemote
import com.cip.cipstudio.dataSource.repository.recentSearchesRepository.RecentSearchesRepository
import com.cip.cipstudio.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.collections.ArrayList


class SearchViewModel(val binding : FragmentSearchBinding) : ViewModel() {

    private val gameRepository : IGDBRepository = IGDBRepositoryRemote
    private lateinit var recentSearchResults : ArrayList<String>

    val isPageLoading : MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>(false)
    }
    val isMoreDataAvailable : MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>(true)
    }

    fun addGameResults(offset: Int, query: String, filter: Filter, onSuccess: (List<GameDetails>) -> Unit) {
        isPageLoading.postValue(true)
        viewModelScope.launch(Dispatchers.Main){
            val gamesResults = withContext(Dispatchers.IO){
                gameRepository.searchGames(query, offset, 10, false, filter.getFilterCriteria(), filter.getSortCriteria()) as ArrayList<GameDetails>
            }
            isPageLoading.postValue(false)
            onSuccess.invoke(gamesResults)
        }

    }

    fun addSearchSuggestions(offset: Int,
                             query: String,
                             searchDB: RecentSearchesRepository,
                             criteria: Criteria,
                             onSuccess: (List<String>) -> Unit) {

        isPageLoading.postValue(true)
        viewModelScope.launch(Dispatchers.Main){
            val suggestion = withContext(Dispatchers.IO){
                gameRepository.getSearchSuggestions(query, filterCriteria = criteria) as ArrayList<GameDetails>
            }
            val recentSearch = withContext(Dispatchers.IO){
                User.getSearchHistory(query, searchDB, offset) as ArrayList<String>
            }
            isPageLoading.postValue(false)

            onSuccess.invoke((recentSearch + suggestion.map { it.name }).distinct())
        }
    }

    fun addRecentSearches(offset: Int, query: String = "", searchDB: RecentSearchesRepository, onSuccess: (List<String>) -> Unit) {
        isPageLoading.postValue(true)

        viewModelScope.launch(Dispatchers.Main){
            recentSearchResults = withContext(Dispatchers.IO){
                User.getSearchHistory(query, searchDB, offset) as ArrayList<String>
            }
            isPageLoading.postValue(false)
            onSuccess.invoke(recentSearchResults)

        }

    }



}