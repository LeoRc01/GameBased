package com.cip.cipstudio.viewmodel

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cip.cipstudio.R
import com.cip.cipstudio.adapters.GamesRecyclerViewAdapter
import com.cip.cipstudio.databinding.PlatformBottomSheetBinding
import com.cip.cipstudio.model.data.GameDetails
import com.cip.cipstudio.repository.IGDBRepositoryRemote
import com.cip.cipstudio.utils.IsFromFragmentEnum
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PlatformDetailsDialogViewModel(val binding: PlatformBottomSheetBinding) : ViewModel() {

    private val gameRepository = IGDBRepositoryRemote
    private lateinit var gamesWithPlatform : List<GameDetails>

    init {
        viewModelScope.launch(Dispatchers.Main) {
            val job = viewModelScope.launch(Dispatchers.IO) {
                gamesWithPlatform = gameRepository.getGamesByPlatform(binding.platDetails!!.id)
            }
            job.join()
            initializeRecyclerView(gamesWithPlatform)
        }
    }

    private fun initializeRecyclerView(gamesWithPlatform : List<GameDetails>){
        val adapter = GamesRecyclerViewAdapter(binding.root.context,
            gamesWithPlatform,
            IsFromFragmentEnum.DIALOG)
        val recyclerView = binding.platformBottomSheetGamesWithThisPlatform
        val manager = LinearLayoutManager(binding.root.context)
        manager.orientation = RecyclerView.HORIZONTAL
        recyclerView.layoutManager = manager
        recyclerView.setItemViewCacheSize(50)
        recyclerView.itemAnimator = null
        recyclerView.adapter = adapter
    }


}