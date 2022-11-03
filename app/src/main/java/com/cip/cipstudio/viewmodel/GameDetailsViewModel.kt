package com.cip.cipstudio.viewmodel

import android.app.Activity
import android.graphics.drawable.Drawable
import android.provider.Settings.Global.getString
import android.util.Log
import android.view.View
import android.widget.*
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.BindingAdapter
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cip.cipstudio.BR
import com.cip.cipstudio.R
import com.cip.cipstudio.adapters.GameScreenshotsRecyclerViewAdapter
import com.cip.cipstudio.adapters.GamesRecyclerViewAdapter
import com.cip.cipstudio.databinding.FragmentGameDetailsBinding
import com.cip.cipstudio.model.data.Game
import com.cip.cipstudio.repository.IGDBRepository
import com.cip.cipstudio.repository.MyFirebaseRepository
import com.cip.cipstudio.view.widgets.LoadingSpinner
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.squareup.picasso.Picasso


class GameDetailsViewModel(
    val game: Game,
    private val binding: FragmentGameDetailsBinding,
) : ViewModel() {

    private val igdbRepository : IGDBRepository = IGDBRepository(generate = false)
    var isGameFavourite : MutableLiveData<Boolean> = MutableLiveData<Boolean>(game.isGameFavourite)
    private lateinit var rvSimilarGamesAdapter : GamesRecyclerViewAdapter
    private lateinit var rvGameScreenshotsAdapter : GameScreenshotsRecyclerViewAdapter

    init {
        binding.fGameDetailsClPageLayout.visibility = View.GONE
        LoadingSpinner.showLoadingDialog(binding.root.context)

        MyFirebaseRepository.getInstance().isGameFavourite(game.gameId.toString()).addOnSuccessListener {
            if(it!=null){

                isGameFavourite.postValue(it.data!=null)

                _setGameScreenshots {
                    _setGenres{
                        _setPlatforms{
                            _setSimilarGames {
                                (binding.root.context as Activity).runOnUiThread {
                                    LoadingSpinner.dismiss()
                                    binding.fGameDetailsClPageLayout.visibility = View.VISIBLE
                                }
                            }
                        }
                    }
                }
            }
        }


    }

    fun getCoverImageUrl(): String? {
        return "https:${game.cover_url}"
    }


    companion object{

        @BindingAdapter("bind:imageUrl")
        @JvmStatic
        fun loadImage(view: ImageView, imageUrl: String?) {
            Picasso.get()
                .load(imageUrl)
                .into(view)
        }
    }

    private fun _setGameScreenshots(onSuccess: () -> Unit){
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
                binding.fGameDetailsRvScreenshots.setLayoutManager(manager)
                binding.fGameDetailsRvScreenshots.setItemViewCacheSize(50)
                binding.fGameDetailsRvScreenshots.itemAnimator = null
                binding.fGameDetailsRvScreenshots.adapter = rvGameScreenshotsAdapter
                onSuccess.invoke()
            }
        }

    }

    private fun _setSimilarGames(onSuccess: () -> Unit){

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
                binding.fGameDetailsRvSimilarGames.setLayoutManager(manager)
                binding.fGameDetailsRvSimilarGames.setItemViewCacheSize(50)
                binding.fGameDetailsRvSimilarGames.itemAnimator = null
                binding.fGameDetailsRvSimilarGames.adapter = rvSimilarGamesAdapter
                onSuccess.invoke()
            }
        }
    }

    /**
     * Imposta le piattaforme.
     * onSuccess è la funzione di callback che viene chiamata quando
     * la funzione principale ha fatto la sua chiamata
     */

    private fun _setPlatforms(onSuccess: () -> Unit) {
        var platformsString = ""
        igdbRepository.getPlatforms(game.platformsId) { arr ->
            (0 until arr.length()).forEach {
                val _platform = arr.getJSONObject(it)
                platformsString = _platform.getString("name") + if (platformsString != "") " / " + platformsString else ""
            }
            binding.fGameDetailsTvGameDetailsPlatforms.text = platformsString
            onSuccess.invoke()
        }
    }

    /**
     * Imposta i generi.
     * onSuccess è la funzione di callback che viene chiamata quando
     * la funzione principale ha fatto la sua chiamata
     */

    private fun _setGenres(onSuccess: ()->Unit){
        var genreStrings :  ArrayList<String> = arrayListOf()
        igdbRepository.getGenres(game.genreIds){ arr->
            (0 until arr.length()).forEach {
                val _genre = arr.getJSONObject(it)
                genreStrings.add(_genre.getString("name"))
                (binding.root.context as Activity).runOnUiThread {
                    binding.fGameDetailsGlGridGenreLayout.addView(_createChip(_genre.getString("name")))
                }
            }
            onSuccess.invoke()
        }
    }

    /**
     * Crea e ritorna il chip
     */

    private fun _createChip(label : String) : Chip{
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

    fun setFavouriteStatus(){
        LoadingSpinner.showLoadingDialog(binding.root.context)
        if(!isGameFavourite.value!!){
            // Aggiungere ai preferiti
            game.setGameToFavourite().addOnSuccessListener {
                isGameFavourite.postValue(true)
                LoadingSpinner.dismiss()
                Toast.makeText(binding.root.context, binding.root.context.getString(R.string.fav_success_add), Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                LoadingSpinner.dismiss()
                Toast.makeText(binding.root.context, binding.root.context.getString(R.string.fav_error), Toast.LENGTH_SHORT).show()
            }
        }else{
            // rimuovere dai preferiti
            game.removeGameFromFavourite().addOnSuccessListener {
                isGameFavourite.postValue(false)
                LoadingSpinner.dismiss()
                Toast.makeText(binding.root.context, binding.root.context.getString(R.string.fav_success_remove), Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                LoadingSpinner.dismiss()
                Toast.makeText(binding.root.context, binding.root.context.getString(R.string.fav_error), Toast.LENGTH_SHORT).show()
            }
        }
    }

}

