package com.cip.cipstudio.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cip.cipstudio.dataSource.repository.IGDBRepositoryImpl.IGDBRepositoryRemote
import com.cip.cipstudio.model.data.GameDetails
import com.cip.cipstudio.utils.GameTypeEnum
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GameListViewModel : ViewModel() {
    val isPageLoading : MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>(true)
    }
    private val gameRepository = IGDBRepositoryRemote

    fun getGames(gameTypeEnum: GameTypeEnum,
                 offset: Int = 0,
                 updateUI : (ArrayList<GameDetails>)->Unit) {
        isPageLoading.postValue(true)
        var games :List<GameDetails>
        viewModelScope.launch(Dispatchers.Main) {
            games = withContext(Dispatchers.IO) {
                gameRepository.getGamesByType(gameTypeEnum, false, pageIndex= offset)
            }
            updateUI.invoke(games as ArrayList<GameDetails>)
            isPageLoading.postValue(false)
        }
    }

}