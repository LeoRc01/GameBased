package com.cip.cipstudio.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cip.cipstudio.dataSource.filter.criteria.FieldCriteria
import com.cip.cipstudio.dataSource.filter.criteria.OperatorCriteria
import com.cip.cipstudio.dataSource.repository.IGDBRepositoryImpl.IGDBRepositoryRemote
import com.cip.cipstudio.model.data.GameDetails
import com.cip.cipstudio.model.data.PlatformDetails
import com.cip.cipstudio.utils.GameTypeEnum
import com.squareup.okhttp.internal.Platform
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class GameListViewModel : ViewModel() {
    val isPageLoading : MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>(true)
    }
    private val gameRepository = IGDBRepositoryRemote

    fun getGames(gameTypeEnum: GameTypeEnum,
                 filterCriteria: OperatorCriteria,
                 offset: Int = 0,
                 updateUI : (ArrayList<GameDetails>)->Unit) {
        isPageLoading.postValue(true)
        var games :List<GameDetails>
        viewModelScope.launch(Dispatchers.Main) {
            games = withContext(Dispatchers.IO) {
                gameRepository.getGamesByType(gameTypeEnum, pageIndex= offset, filterCriteria = filterCriteria)
            }
            updateUI.invoke(games as ArrayList<GameDetails>)
            isPageLoading.postValue(false)
        }
    }

    fun getGenres(updateUI : (ArrayList<JSONObject>)->Unit) {
        isPageLoading.postValue(true)
        viewModelScope.launch(Dispatchers.Main) {
            val genres = withContext(Dispatchers.IO) {
                gameRepository.getGenres()
            }
            updateUI.invoke(genres)
            isPageLoading.postValue(false)
        }
    }

}