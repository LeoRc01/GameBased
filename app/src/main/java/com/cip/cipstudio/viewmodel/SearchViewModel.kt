package com.cip.cipstudio.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cip.cipstudio.dataSource.filter.criteria.Criteria
import com.cip.cipstudio.dataSource.filter.criteria.ViewModelFilter
import com.cip.cipstudio.dataSource.repository.IGDBRepository
import com.cip.cipstudio.databinding.FragmentSearchBinding
import com.cip.cipstudio.model.data.GameDetails
import com.cip.cipstudio.dataSource.repository.IGDBRepositoryImpl.IGDBRepositoryRemote
import com.cip.cipstudio.dataSource.repository.RecentSearchesRepository
import com.cip.cipstudio.model.User
import com.cip.cipstudio.model.data.PlatformDetails
import com.cip.cipstudio.utils.Costant
import com.cip.cipstudio.utils.Costant.platformDefault
import com.cip.cipstudio.utils.GameTypeEnum
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import kotlin.collections.ArrayList


class SearchViewModel(val binding : FragmentSearchBinding) : ViewModel(), ViewModelFilter {

    private val gameRepository : IGDBRepository = IGDBRepositoryRemote
    private lateinit var recentSearchResults : ArrayList<String>

    val isPageLoading : MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>(false)
    }
    val isMoreDataAvailable : MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>(true)
    }
    private var offsetPlatform = -1
    private val platformDefault : ArrayList<PlatformDetails> = Costant.platformDefault
    private val platformDefaultIds = platformDefault.map { it.id }

    fun addGameResults(offset: Int, query: String, filterCriteria: Criteria, onSuccess: (List<GameDetails>) -> Unit) {
        isPageLoading.postValue(true)
        viewModelScope.launch(Dispatchers.Main){
            val gamesResults = withContext(Dispatchers.IO){
                gameRepository.searchGames(query, offset, 10, false, filterCriteria) as ArrayList<GameDetails>
            }
            isPageLoading.postValue(false)
            onSuccess.invoke(gamesResults)
        }

    }

    fun addSearchSuggestions(offset: Int, query: String,searchDB: RecentSearchesRepository, onSuccess: (List<String>) -> Unit) {

        isPageLoading.postValue(true)
        viewModelScope.launch(Dispatchers.Main){
            val suggestion = withContext(Dispatchers.IO){
                gameRepository.getSearchSuggestions(query) as ArrayList<GameDetails>
            }
            val recentSearch = withContext(Dispatchers.IO){
                User.getRecentlySearched(query, searchDB, offset) as ArrayList<String>
            }
            isPageLoading.postValue(false)

            onSuccess.invoke((recentSearch + suggestion.map { it.name }).distinct())
        }
    }

    fun addRecentSearches(offset: Int, query: String = "", searchDB: RecentSearchesRepository, onSuccess: (List<String>) -> Unit) {
        isPageLoading.postValue(true)

        viewModelScope.launch(Dispatchers.Main){
            recentSearchResults = withContext(Dispatchers.IO){
                User.getRecentlySearched(query, searchDB, offset) as ArrayList<String>
            }
            isPageLoading.postValue(false)
            onSuccess.invoke(recentSearchResults)

        }

    }

    override fun getPlatforms(
        updateUI : (List<PlatformDetails>) -> Unit
    ){
        isPageLoading.postValue(true)
        if (offsetPlatform == -1) {
            updateUI.invoke(platformDefault)
            isPageLoading.postValue(false)
        }
        else {
            viewModelScope.launch(Dispatchers.Main) {
                val platforms = withContext(Dispatchers.IO) {
                    gameRepository.getPlatforms(offsetPlatform, platformDefaultIds)
                }
                updateUI.invoke(platforms)
                isPageLoading.postValue(false)
            }
        }
        offsetPlatform++
    }

    override fun getGenres(updateUI : (ArrayList<JSONObject>)->Unit) {
        isPageLoading.postValue(true)
        viewModelScope.launch(Dispatchers.Main) {
            val genres = withContext(Dispatchers.IO) {
                gameRepository.getGenres()
            }
            updateUI.invoke(genres)
            isPageLoading.postValue(false)
        }
    }

    override fun getPlayerPerspectives(updateUI : (ArrayList<JSONObject>)->Unit) {
        isPageLoading.postValue(true)
        viewModelScope.launch(Dispatchers.Main) {
            val playerPerspectives = withContext(Dispatchers.IO) {
                gameRepository.getPlayerPerspectives()
            }
            updateUI.invoke(playerPerspectives)
            isPageLoading.postValue(false)
        }
    }

    override fun getGameModes(updateUI : (ArrayList<JSONObject>)->Unit) {
        isPageLoading.postValue(true)
        viewModelScope.launch(Dispatchers.Main) {
            val gameModes = withContext(Dispatchers.IO) {
                gameRepository.getGameModes()
            }
            updateUI.invoke(gameModes)
            isPageLoading.postValue(false)
        }
    }

    override fun getThemes(updateUI : (ArrayList<JSONObject>)->Unit) {
        isPageLoading.postValue(true)
        viewModelScope.launch(Dispatchers.Main) {
            val themes = withContext(Dispatchers.IO) {
                gameRepository.getThemes()
            }
            updateUI.invoke(themes)
            isPageLoading.postValue(false)
        }
    }

    override fun getYears(updateUI : (List<Float>)->Unit) {
        isPageLoading.postValue(true)
        viewModelScope.launch(Dispatchers.Main) {
            val years = withContext(Dispatchers.IO) {
                gameRepository.getFirstAndLastYearsOfRelease()
            }
            updateUI.invoke(years)
            isPageLoading.postValue(false)
        }
    }

    override fun getCategory(updateUI : (ArrayList<JSONObject>)->Unit) {
        isPageLoading.postValue(true)
        updateUI.invoke(Costant.categoryDefault)
        isPageLoading.postValue(false)
    }


}