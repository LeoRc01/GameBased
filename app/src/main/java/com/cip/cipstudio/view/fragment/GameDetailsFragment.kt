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
import androidx.navigation.fragment.findNavController
import com.cip.cipstudio.R
import com.cip.cipstudio.databinding.FragmentGameDetailsBinding
import com.cip.cipstudio.model.data.Game
import com.cip.cipstudio.viewmodel.GameDetailsViewModel

class GameDetailsFragment : Fragment() {
    private lateinit var gameDetailsViewModel: GameDetailsViewModel
    private lateinit var gameDetailsBinding: FragmentGameDetailsBinding

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        gameDetailsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_game_details, container, false)

        val currentGame : Game = arguments?.get("game") as Game

        gameDetailsViewModel = GameDetailsViewModel(currentGame, gameDetailsBinding)

        gameDetailsBinding.vm = gameDetailsViewModel
        gameDetailsBinding.game = currentGame
        gameDetailsBinding.lifecycleOwner = this

        initializeShowMore()

        return gameDetailsBinding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        gameDetailsBinding.unbind()
    }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun initializeShowMore() {
        /*if (gameDetailsBinding.fGameDetailsTvGameDetailsDescription.lineCount < 4) {
            gameDetailsBinding.fGameDetailsTvShowMoreDescription.visibility = View.GONE
            gameDetailsBinding.fGameDetailsTvGameDetailsDescription.foreground = null
            return
        }*/
        gameDetailsBinding.fGameDetailsTvShowMoreDescription.setOnClickListener {
            val params = gameDetailsBinding.fGameDetailsTvGameDetailsDescription.layoutParams
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT
            gameDetailsBinding.fGameDetailsTvGameDetailsDescription.layoutParams = params
            gameDetailsBinding.fGameDetailsTvShowMoreDescription.visibility = View.GONE
            gameDetailsBinding.fGameDetailsTvGameDetailsDescription.foreground = null
        }
    }

}