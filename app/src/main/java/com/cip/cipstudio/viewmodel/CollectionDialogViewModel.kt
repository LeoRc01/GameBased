package com.cip.cipstudio.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cip.cipstudio.dataSource.repository.IGDBRepository.IGDBRepositoryRemote
import com.cip.cipstudio.model.data.GameDetails
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CollectionDialogViewModel(collectionName : String,
                                setGameCollection: (List<GameDetails>) -> Unit,
                                onSuccess: () -> Unit) : ViewModel() {

    private lateinit var collectionGames : List<GameDetails>
    private val gameRepository = IGDBRepositoryRemote

    init{
        viewModelScope.launch(Dispatchers.Main) {
            val job = viewModelScope.launch(Dispatchers.IO) {
                collectionGames = gameRepository.getGamesByCollectionName(collectionName, false)
            }
            job.join()
            //Log.i("COLLECTIONS", collectionGames.toString())
            setGameCollection.invoke(collectionGames)
            onSuccess.invoke()
        }
    }
}