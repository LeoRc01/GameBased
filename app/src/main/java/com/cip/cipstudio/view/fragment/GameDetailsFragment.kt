package com.cip.cipstudio.view.fragment

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.cip.cipstudio.R
import com.cip.cipstudio.databinding.FragmentGameDetailsBinding
import com.cip.cipstudio.model.data.Game
import com.cip.cipstudio.model.data.GameDetails
import com.cip.cipstudio.repository.IGDBRepositoryRemote
import com.cip.cipstudio.view.widgets.LoadingSpinner
import com.cip.cipstudio.viewmodel.GameDetailsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GameDetailsFragment : Fragment() {
    private lateinit var gameDetailsViewModel: GameDetailsViewModel
    private lateinit var gameDetailsBinding: FragmentGameDetailsBinding
    private var gameCurrent: GameDetails? = null

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        gameDetailsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_game_details, container, false)

        gameDetailsBinding.fGameDetailsClPageLayout.visibility = View.GONE
        LoadingSpinner.showLoadingDialog(requireContext())

        initializeFragment(){
            gameDetailsBinding.vm = gameDetailsViewModel
            initializeShowMore()
            LoadingSpinner.dismiss()
            gameDetailsBinding.fGameDetailsClPageLayout.visibility = View.VISIBLE
        }
        return gameDetailsBinding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        gameDetailsBinding.unbind()
    }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun initializeShowMore() {
        gameDetailsBinding.fGameDetailsTvShowMoreDescription.setOnClickListener {
            val params = gameDetailsBinding.fGameDetailsTvGameDetailsDescription.layoutParams
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT
            gameDetailsBinding.fGameDetailsTvGameDetailsDescription.layoutParams = params
            gameDetailsBinding.fGameDetailsTvShowMoreDescription.visibility = View.GONE
            gameDetailsBinding.fGameDetailsTvGameDetailsDescription.foreground = null
        }
    }

    private fun initializeFragment( onSuccess: () -> Unit = {}) {
        val gameId = arguments?.get("game_id") as String
        val jobIO = lifecycleScope.launch(Dispatchers.IO) {
            gameCurrent = IGDBRepositoryRemote.getGamesDetails(gameId)
        }

        val jobVm = lifecycleScope.launch(Dispatchers.Main) {
            jobIO.join()
            gameDetailsViewModel = GameDetailsViewModel(gameCurrent!!, gameDetailsBinding)
        }

        lifecycleScope.launch(Dispatchers.Main) {
            jobVm.join()
            onSuccess.invoke()
        }
    }

}