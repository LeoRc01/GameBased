package com.cip.cipstudio.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cip.cipstudio.dataSource.repository.historyRepository.HistoryRepository
import com.cip.cipstudio.dataSource.repository.IGDBRepository.IGDBRepositoryRemote
import com.cip.cipstudio.dataSource.repository.historyRepository.HistoryRepositoryLocal
import com.cip.cipstudio.databinding.FragmentHistoryBinding
import com.cip.cipstudio.model.User
import com.cip.cipstudio.model.data.GameDetails
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HistoryViewModel(val binding : FragmentHistoryBinding) : ViewModel() {
    val isPageLoading : MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>(true)
    }
    val isMoreDataAvailable : MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>(true)
    }

    private lateinit var lastViewedGames : ArrayList<GameDetails>
    private val historyRepository: HistoryRepository = HistoryRepositoryLocal(binding.root.context)

    fun addMoreGame(offset: Int = 0,onSuccess: (List<GameDetails>) -> Unit) {
        isPageLoading.postValue(true)
        viewModelScope.launch(Dispatchers.Main){
            val list = User.getRecentlyViewed(historyRepository, offset)
            lastViewedGames = withContext(Dispatchers.IO){
                IGDBRepositoryRemote.getGamesByIds(list, false) as ArrayList<GameDetails>
            }
            isMoreDataAvailable.postValue(lastViewedGames.isNotEmpty())
            onSuccess(lastViewedGames.sortedBy { list.indexOf(it.id) })
            isPageLoading.postValue(false)
        }
    }

    suspend fun deleteHistory() {
        User.deleteHistory(historyRepository)
    }

}