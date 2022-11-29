package com.cip.cipstudio.viewmodel

import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.databinding.BindingAdapter
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cip.cipstudio.exception.NotLoggedException
import com.cip.cipstudio.R
import com.cip.cipstudio.databinding.FragmentGameDetailsBinding
import com.cip.cipstudio.model.data.GameDetails
import com.cip.cipstudio.model.data.PlatformDetails
import com.cip.cipstudio.dataSource.repository.IGDBRepositoryImpl.IGDBRepositoryRemote
import com.cip.cipstudio.model.User
import com.cip.cipstudio.view.widgets.LoadingSpinner
import com.google.android.material.button.MaterialButton
import com.google.firebase.database.DataSnapshot
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class GameDetailsViewModel(private val binding: FragmentGameDetailsBinding
) : ViewModel() {


    private lateinit var game: GameDetails
    private lateinit var platformDetails: List<PlatformDetails>

    private val TAG = "GameDetailsVM"
    lateinit var isGameFavourite : MutableLiveData<Boolean>

    private val user = User
    private val gameRepository = IGDBRepositoryRemote

    constructor(gameId : String,
                binding: FragmentGameDetailsBinding,
                refresh : Boolean,
                setScreenshotUI: (List<JSONObject>) -> Unit,
                setSimilarGamesUI: (List<GameDetails>) -> Unit,
                setDLCsUI: (List<GameDetails>) -> Unit,
                setPlatformsUI: (List<PlatformDetails>) -> Unit,
                setGenresUI: (List<JSONObject>)-> Unit,
                onSuccess: () -> Unit) : this(binding) {

        viewModelScope.launch(Dispatchers.Main) {
            // withContext serve per cambiare il contesto di esecuzione
            // quindi se prima era in main thread ora è in IO thread
            // ha le stesse funzionalità di async e await
            game = withContext(Dispatchers.IO) {
                gameRepository.getGameDetails(gameId, refresh)
            }
            platformDetails = withContext(Dispatchers.IO) {
                 gameRepository.getPlatformsInfo(getIdsFromListJSONObject(game.platforms), refresh)
            }
            val fav : DataSnapshot
            // await aspetta il valore di fav prima di eseguire il resto, è usabile sui task

            isGameFavourite = MutableLiveData<Boolean>(game.isFavourite)
            user.isGameFavourite(gameId).addOnSuccessListener {
                if (it != null) {
                    isGameFavourite.postValue(it.exists())
                    if(it.exists())
                        (binding.fGameDetailsBtnFavorite as MaterialButton).icon =
                            binding.root.context.getDrawable(R.drawable.ic_favorite)
                }
            }.addOnFailureListener {
                Toast.makeText(
                    binding.root.context,
                    binding.root.context.getString(R.string.invalid_operation_must_logged),
                    Toast.LENGTH_SHORT
                ).show()
            }

            // queste funzioni servono a dividere il ruolo di viewModel e view
            setScreenshotUI.invoke(game.screenshots)
            setSimilarGamesUI.invoke(game.similarGames)
            setDLCsUI.invoke(game.dlcs)
            setPlatformsUI.invoke(platformDetails)
            setGenresUI.invoke(game.genres)
            onSuccess.invoke()
        }
    }

    private fun getIdsFromListJSONObject(items : List<JSONObject>) : List<String> {
        val result = arrayListOf<String>()
        for (item in items){
            result.add(item.getString("id"))
        }
        return result
    }

    companion object{
        @RequiresApi(Build.VERSION_CODES.S)
        @BindingAdapter("bind:blurredImageUrl")
        @JvmStatic
        fun loadBlurredImage(view: ImageView, imageUrl: String?) {
            Picasso.get()
                .load(imageUrl)
                .into(view)

            view.setRenderEffect(RenderEffect.createBlurEffect(30F, 30F, Shader.TileMode.MIRROR))
        }


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

    private fun getPlatforms() : List<String> {
        val platformsStrings = arrayListOf<String>()
        game.platforms.forEach {
            val platform = it.getString("name")
            platformsStrings.add(platform)
        }
        return platformsStrings
    }

    fun setFavouriteStatus(){
        LoadingSpinner.showLoadingDialog(binding.root.context)
        if(!isGameFavourite.value!!){
            // Aggiungere ai preferiti
            game.setGameToFavourite().addOnSuccessListener {
                isGameFavourite.postValue(true)
                (binding.fGameDetailsBtnFavorite as MaterialButton).icon =
                    binding.root.context.getDrawable(R.drawable.ic_favorite)
                LoadingSpinner.dismiss()
                Toast.makeText(binding.root.context, binding.root.context.getString(R.string.fav_success_add), Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                LoadingSpinner.dismiss()
                if (it is NotLoggedException){
                    Toast.makeText(binding.root.context, binding.root.context.getString(R.string.invalid_operation_must_logged), Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(binding.root.context, binding.root.context.getString(R.string.fav_error), Toast.LENGTH_SHORT).show()
                }
            }
        }else{
            // rimuovere dai preferiti

            game.removeGameFromFavourite().addOnSuccessListener {
                isGameFavourite.postValue(false)
                LoadingSpinner.dismiss()
                (binding.fGameDetailsBtnFavorite as MaterialButton).icon =
                    binding.root.context.getDrawable(R.drawable.ic_favorite_border)
                Toast.makeText(binding.root.context, binding.root.context.getString(R.string.fav_success_remove), Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                LoadingSpinner.dismiss()
                if (it is NotLoggedException){
                    Toast.makeText(binding.root.context, binding.root.context.getString(R.string.invalid_operation_must_logged), Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(binding.root.context, binding.root.context.getString(R.string.fav_error), Toast.LENGTH_SHORT).show()
                }
            }


        }
    }

}

