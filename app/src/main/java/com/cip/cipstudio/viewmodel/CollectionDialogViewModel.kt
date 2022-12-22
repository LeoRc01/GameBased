package com.cip.cipstudio.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cip.cipstudio.dataSource.repository.IGDBRepository.IGDBRepositoryRemote
import com.cip.cipstudio.model.data.GameDetails
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CollectionDialogViewModel(collectionName : String,
                                setGameCollection: (List<GameDetails>) -> Unit,
                                onSuccess: () -> Unit) : ViewModel() {

    private val gameRepository = IGDBRepositoryRemote

    init{
        viewModelScope.launch(Dispatchers.Main) {
            val collectionGames =  withContext(Dispatchers.IO) {
                gameRepository.getGamesByCollectionName(collectionName, false)
            }
            setGameCollection.invoke(collectionGames)
            onSuccess.invoke()
        }
    }
}