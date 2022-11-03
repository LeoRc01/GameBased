package com.cip.cipstudio.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.cip.cipstudio.R
import com.cip.cipstudio.databinding.ActivityGameDetailisBinding
import com.cip.cipstudio.databinding.FragmentGameDetailsBinding
import com.cip.cipstudio.model.data.Game
import com.cip.cipstudio.viewmodel.GameDetailsViewModel

class GameDetailsFragment : Fragment() {
    private lateinit var gameDetailsViewModel: GameDetailsViewModel
    private lateinit var gameDetailsBinding: FragmentGameDetailsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        gameDetailsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_game_details, container, false)

        val currentGame : Game = arguments?.get("game") as Game

        // TODO: Use the ViewModel (per ora è temporaneo)
        gameDetailsViewModel = GameDetailsViewModel(currentGame, ActivityGameDetailisBinding.inflate(layoutInflater))

        gameDetailsBinding.vm = gameDetailsViewModel
        gameDetailsBinding.game = currentGame
        gameDetailsBinding.lifecycleOwner = this

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_game_details, container, false)
    }

}