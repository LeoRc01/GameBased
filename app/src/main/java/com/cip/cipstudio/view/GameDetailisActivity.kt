package com.cip.cipstudio.view

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.cip.cipstudio.R
import com.cip.cipstudio.databinding.ActivityGameDetailisBinding
import com.cip.cipstudio.databinding.ActivityMainBinding
import com.cip.cipstudio.model.data.Game
import com.cip.cipstudio.viewmodel.GameDetailsViewModel
import com.google.android.material.button.MaterialButton

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
        binding.lifecycleOwner = this

        gameDetailsViewModel.isGameFavourite.observe(this){
            if(it){
                (binding.btnFav as MaterialButton).icon = getDrawable(R.drawable.ic_favorite)
            }else{
                (binding.btnFav as MaterialButton).icon = getDrawable(R.drawable.ic_favorite_border)
            }

        }

        binding.tvShowMoreDescription.setOnClickListener {
            val params: ViewGroup.LayoutParams = binding.tvGameDetailsDescription.getLayoutParams()
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            binding.tvGameDetailsDescription.setLayoutParams(params);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                binding.tvGameDetailsDescription.foreground = null
            }
            binding.tvShowMoreDescription.visibility = View.GONE
        }

    }
}