package com.cip.cipstudio.viewmodel

import android.app.Activity

import android.util.Log
import android.view.View
import android.widget.*
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cip.cipstudio.R
import com.cip.cipstudio.adapters.GameScreenshotsRecyclerViewAdapter
import com.cip.cipstudio.adapters.GamesRecyclerViewAdapter
import com.cip.cipstudio.databinding.ActivityGameDetailisBinding
import com.cip.cipstudio.model.data.Game
import com.cip.cipstudio.repository.IGDBRepository
import com.cip.cipstudio.view.widgets.LoadingSpinner
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.squareup.picasso.Picasso
import kotlin.collections.ArrayList


class GameDetailsViewModel(
    val game: Game,
    val binding: ActivityGameDetailisBinding,
) : ViewModel() {

    val igdbRepository : IGDBRepository = IGDBRepository(generate = false)
    private lateinit var rvSimilarGamesAdapter : GamesRecyclerViewAdapter
    private lateinit var rvGameScreenshotsAdapter : GameScreenshotsRecyclerViewAdapter

    init {
        binding.llPageLayout.visibility = View.GONE
        LoadingSpinner.showLoadingDialog(binding.root.context)

        _setGameScreenshots {
            _setGenres{
                _setPlatforms{
                    _setSimilarGames {
                        (binding.root.context as Activity).runOnUiThread {
                            binding.tvGameDetailsPlatforms.text = it
                            Picasso.get().load("https:${game.cover_url}").into(binding.ivGameDetailsCover)
                            //_doSynchronousActions()
                            LoadingSpinner.dismiss()
                            binding.llPageLayout.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }
    }

    fun _setGameScreenshots(onSuccess: () -> Unit){
        igdbRepository.getScreenshots(game.screenShotIds){arr->
            val screenshotIds : ArrayList<String> = arrayListOf()
            (0 until arr.length()).forEach{
                val screenshot = arr.getJSONObject(it)
                screenshotIds.add(screenshot.getString("url"))
            }
            // Creo il layout manager (fondamentale)
            val manager = LinearLayoutManager(binding.root.context)
            // Imposto l'orientamento a orizzontale
            manager.orientation = RecyclerView.HORIZONTAL
            // Setto il layoutmanager alla RV
            (binding.root.context as Activity).runOnUiThread {
                rvGameScreenshotsAdapter = GameScreenshotsRecyclerViewAdapter(binding.root.context, screenshotIds)
                binding.rvGameDetailsScreenshots.setLayoutManager(manager)
                binding.rvGameDetailsScreenshots.setItemViewCacheSize(50)
                binding.rvGameDetailsScreenshots.itemAnimator = null
                binding.rvGameDetailsScreenshots.adapter = rvGameScreenshotsAdapter
                onSuccess.invoke()
            }
        }

    }

    fun _setSimilarGames(onSuccess: () -> Unit){

        var ids : String = ""
        game.similarGamesIds.forEach {
            ids += it.toString() + ","
        }
        ids = ids.substring(0, ids.length-1)

        var payload = "fields *; where id = ($ids);"

        Log.i("PAYLOAD", payload)

        // Creo il layout manager (fondamentale)
        val manager = LinearLayoutManager(binding.root.context)
        // Imposto l'orientamento a orizzontale
        manager.orientation = RecyclerView.HORIZONTAL
        // Setto il layoutmanager alla RV

        igdbRepository.getGamesByPayload(payload){
            (binding.root.context as Activity).runOnUiThread {
                rvSimilarGamesAdapter = GamesRecyclerViewAdapter(binding.root.context, it)
                binding.rvSimilarGames.setLayoutManager(manager)
                binding.rvSimilarGames.setItemViewCacheSize(50)
                binding.rvSimilarGames.itemAnimator = null
                binding.rvSimilarGames.adapter = rvSimilarGamesAdapter
                onSuccess.invoke()
            }
        }
    }

    /**
     * Imposta le piattaforme.
     * onSuccess è la funzione di callback che viene chiamata quando
     * la funzione principale ha fatto la sua chiamata
     */

    fun _setPlatforms(onSuccess: (String) -> Unit) {
        var platformsString = ""
        igdbRepository.getPlatforms(game.platformsId) { arr ->
            (0 until arr.length()).forEach {
                val _platform = arr.getJSONObject(it)
                platformsString = _platform.getString("name") + if (platformsString != "") " / " + platformsString else ""
            }
            onSuccess.invoke(platformsString)
        }
    }

    /**
     * Imposta i generi.
     * onSuccess è la funzione di callback che viene chiamata quando
     * la funzione principale ha fatto la sua chiamata
     */

    fun _setGenres(onSuccess: ()->Unit){
        var genreStrings :  ArrayList<String> = arrayListOf()
        igdbRepository.getGenres(game.genreIds){ arr->
            (0 until arr.length()).forEach {
                val _genre = arr.getJSONObject(it)
                genreStrings.add(_genre.getString("name"))
                (binding.root.context as Activity).runOnUiThread {
                    binding.glGridGenreLayout.addView(_createChip(_genre.getString("name")))
                }
            }
            onSuccess.invoke()
        }
    }

    /**
     * Crea e ritorna il chip
     */

    fun _createChip(label : String) : Chip{
        val chip = Chip(binding.root.context, null, R.layout.genre_chip)
        val chipDrawable = ChipDrawable.createFromAttributes(
            binding.root.context,
            null,
            0,
            com.cip.cipstudio.R.style.genre_chip
        )
        chip.setChipDrawable(chipDrawable)
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(15, 0, 15, 0)
        chip.layoutParams = params
        chip.text = label
        return chip
    }

}

