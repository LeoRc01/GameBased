package com.cip.cipstudio.viewmodel

import android.app.Activity
import android.util.Log
import android.view.View
import android.widget.*
import androidx.databinding.BindingAdapter
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cip.cipstudio.R
import com.cip.cipstudio.adapters.GameScreenshotsRecyclerViewAdapter
import com.cip.cipstudio.adapters.GamesRecyclerViewAdapter
import com.cip.cipstudio.databinding.FragmentGameDetailsBinding
import com.cip.cipstudio.model.data.GameDetails
import com.cip.cipstudio.repository.IGDBRepositoryRemote
import com.cip.cipstudio.repository.IGDBRepositorydwa
import com.cip.cipstudio.repository.MyFirebaseRepository
import com.cip.cipstudio.view.widgets.LoadingSpinner
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


class GameDetailsViewModel(
    private var game: GameDetails,
    private val binding: FragmentGameDetailsBinding,
) : ViewModel() {

    private val TAG = "GameDetailsViewModel"
    var isGameFavourite : MutableLiveData<Boolean> = MutableLiveData<Boolean>(game.isFavourite)
    val isPageLoading : MutableLiveData<Boolean> by lazy{
        MutableLiveData<Boolean>(true)
    }
    private lateinit var rvSimilarGamesAdapter : GamesRecyclerViewAdapter
    private lateinit var rvGameScreenshotsAdapter : GameScreenshotsRecyclerViewAdapter

    init {


        //MyFirebaseRepository.getInstance().isGameFavourite(game.toString()).addOnSuccessListener {
            //if(it!=null){

                //isGameFavourite.postValue(it.data!=null)
                initializeView()
            //}
        //}


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

    fun getGame() : GameDetails{
        return game
    }

    private fun initializeView() {
        viewModelScope.launch(Dispatchers.Main) {
            setScreenshotsRecyclerView()
            setSimilarGamesRecyclerView()
            setPlatformsText()
            setGenresGridLayout()
            binding.loadingModel!!.isPageLoading.postValue(false)
        }
    }

    private fun setScreenshotsRecyclerView() {
        val manager = LinearLayoutManager(binding.root.context)
        manager.orientation = RecyclerView.HORIZONTAL
        rvGameScreenshotsAdapter = GameScreenshotsRecyclerViewAdapter(binding.root.context, game.screenshots)
        binding.fGameDetailsRvScreenshots.layoutManager = manager
        binding.fGameDetailsRvScreenshots.setItemViewCacheSize(50)
        binding.fGameDetailsRvScreenshots.itemAnimator = null
        binding.fGameDetailsRvScreenshots.adapter = rvGameScreenshotsAdapter
    }

    private fun setSimilarGamesRecyclerView() {
        val manager = LinearLayoutManager(binding.root.context)
        manager.orientation = RecyclerView.HORIZONTAL
        rvSimilarGamesAdapter = GamesRecyclerViewAdapter(binding.root.context, game.similarGames, R.id.action_gameDetailsFragment2_self)
        binding.fGameDetailsRvSimilarGames.setLayoutManager(manager)
        binding.fGameDetailsRvSimilarGames.setItemViewCacheSize(50)
        binding.fGameDetailsRvSimilarGames.itemAnimator = null
        binding.fGameDetailsRvSimilarGames.adapter = rvSimilarGamesAdapter
    }

    private fun setPlatformsText() {
        var platformsString = ""
        game.platforms.forEach {
            val platform = it.getString("name")
            platformsString = platform + if (platformsString != "") " / $platformsString" else ""
        }
        binding.fGameDetailsTvGameDetailsPlatforms.text = platformsString
    }

    private fun setGenresGridLayout() {
        game.genres.forEach {
            val genre = it.getString("name")
            binding.fGameDetailsGlGridGenreLayout.addView(createChip(genre))
        }
    }

    /**
     * Crea e ritorna il chip
     */

    private fun createChip(label : String) : Chip{
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

