package com.cip.cipstudio.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cip.cipstudio.dataSource.filter.criteria.OperatorCriteria
import com.cip.cipstudio.dataSource.repository.IGDBRepositoryImpl.IGDBRepositoryRemote
import com.cip.cipstudio.model.data.GameDetails
import com.cip.cipstudio.model.data.PlatformDetails
import com.cip.cipstudio.utils.Costant
import com.cip.cipstudio.utils.GameTypeEnum
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class GameListViewModel : ViewModel() {
    private val TAG = "GameListViewModel"

    val isPageLoading : MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>(true)
    }
    private val gameRepository = IGDBRepositoryRemote
    private var offsetPlatform = -1
    private val platformDefault : ArrayList<PlatformDetails> = Costant.platformDefault
    private val platformDefaultIds = platformDefault.map { it.id }


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

    fun getPlatforms(
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

    fun getPlayerPerspectives(updateUI : (ArrayList<JSONObject>)->Unit) {
        isPageLoading.postValue(true)
        viewModelScope.launch(Dispatchers.Main) {
            val playerPerspectives = withContext(Dispatchers.IO) {
                gameRepository.getPlayerPerspectives()
            }
            updateUI.invoke(playerPerspectives)
            isPageLoading.postValue(false)
        }
    }

    fun getGameModes(updateUI : (ArrayList<JSONObject>)->Unit) {
        isPageLoading.postValue(true)
        viewModelScope.launch(Dispatchers.Main) {
            val gameModes = withContext(Dispatchers.IO) {
                gameRepository.getGameModes()
            }
            updateUI.invoke(gameModes)
            isPageLoading.postValue(false)
        }
    }

}
