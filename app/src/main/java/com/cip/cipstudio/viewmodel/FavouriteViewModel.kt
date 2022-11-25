package com.cip.cipstudio.viewmodel

import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cip.cipstudio.R
import com.cip.cipstudio.databinding.FragmentFavouriteBinding
import com.cip.cipstudio.model.data.GameDetails
import com.cip.cipstudio.dataSource.repository.IGDBRepositoryImpl.IGDBRepositoryRemote
import com.cip.cipstudio.model.User
import kotlinx.coroutines.*

class FavouriteViewModel(val binding : FragmentFavouriteBinding) : ViewModel() {

    private val user = User

    val isPageLoading : MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>(true)
    }

    private val favouriteGamesIds : ArrayList<String> by lazy {
        arrayListOf()
    }

    lateinit var favouriteGames : ArrayList<GameDetails>

    fun initialize(refresh : Boolean, updateUI: (ArrayList<GameDetails>) -> Unit) {
        isPageLoading.postValue(true)
        user.getFavouriteGames().addOnSuccessListener {
            (it.value as Map<*, *>).forEach {
                favouriteGamesIds.add(it.value.toString())
            }
            viewModelScope.launch(Dispatchers.Main) {
                favouriteGames = withContext(Dispatchers.IO){
                    IGDBRepositoryRemote.getGamesByIds(favouriteGamesIds, refresh) as ArrayList<GameDetails>
                }
                updateUI.invoke(favouriteGames)
                isPageLoading.postValue(false)
            }
        }
            .addOnFailureListener {
                Toast.makeText(
                        binding.root.context,
                        binding.root.context.getString(R.string.invalid_operation_must_logged),
                        Toast.LENGTH_SHORT)
                    .show()
            }
    }

}