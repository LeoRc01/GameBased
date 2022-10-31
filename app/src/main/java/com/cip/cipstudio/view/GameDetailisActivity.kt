package com.cip.cipstudio.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.cip.cipstudio.R
import com.cip.cipstudio.databinding.ActivityGameDetailisBinding
import com.cip.cipstudio.databinding.ActivityMainBinding
import com.cip.cipstudio.model.data.Game
import com.cip.cipstudio.viewmodel.GameDetailsViewModel

class GameDetailisActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: ActivityGameDetailisBinding = DataBindingUtil.setContentView(this, R.layout.activity_game_detailis)
        supportActionBar!!.hide()
        val currentGame : Game = intent.extras!!.get("game") as Game
        val gameDetailsViewModel : GameDetailsViewModel =
            GameDetailsViewModel(currentGame, binding)
        binding.vm = gameDetailsViewModel
        binding.game = currentGame

    }
}