package com.cip.cipstudio.viewmodel

import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cip.cipstudio.databinding.FragmentFavouriteBinding
import com.cip.cipstudio.repository.MyFirebaseRepository

class FavouriteViewModel(val binding : FragmentFavouriteBinding) : ViewModel() {

    val isPageLoading : MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>(true)
    }



    init {
        MyFirebaseRepository.getInstance().getFavorites().addOnSuccessListener {
            isPageLoading.postValue(false)
        }
    }

}