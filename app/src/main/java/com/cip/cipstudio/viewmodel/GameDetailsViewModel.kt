package com.cip.cipstudio.viewmodel

import android.graphics.drawable.Drawable
import android.widget.ImageView
import android.widget.Toast
import androidx.databinding.BindingAdapter
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cip.cipstudio.R
import com.cip.cipstudio.databinding.FragmentGameDetailsBinding
import com.cip.cipstudio.model.data.GameDetails
import com.cip.cipstudio.repository.IGDBRepositoryRemote
import com.cip.cipstudio.repository.MyFirebaseRepository
import com.cip.cipstudio.view.widgets.LoadingSpinner
import com.google.android.material.button.MaterialButton
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.json.JSONObject

class GameDetailsViewModel(private val binding: FragmentGameDetailsBinding
) : ViewModel() {


    private lateinit var game: GameDetails
    private val TAG = "GameDetailsVM"
    lateinit var isGameFavourite : MutableLiveData<Boolean>

    private val firebaseRepository = MyFirebaseRepository.getInstance()
    private val gameRepository = IGDBRepositoryRemote

    constructor(gameId : String,
                binding: FragmentGameDetailsBinding,
                setScreenshotUI: (List<JSONObject>) -> Unit,
                setSimilarGamesUI: (List<GameDetails>) -> Unit,
                setDLCsUI: (List<GameDetails>) -> Unit,
                setGenresUI: (List<JSONObject>)-> Unit,
                onSuccess: () -> Unit) : this(binding) {

        viewModelScope.launch(Dispatchers.Main) {
            // withContext serve per cambiare il contesto di esecuzione
            // quindi se prima era in main thread ora è in IO thread
            // ha le stesse funzionalità di async e await
            game = withContext(Dispatchers.IO) {
                gameRepository.getGameDetails(gameId)
            }

            // await aspetta il valore di fav prima di eseguire il resto, è usabile sui task
            val fav = firebaseRepository.isGameFavourite(gameId).await()
            isGameFavourite = MutableLiveData<Boolean>(game.isFavourite)
            if (fav != null) {
                isGameFavourite.postValue(fav.exists())
                if(fav.exists())
                    (binding.fGameDetailsBtnFavorite as MaterialButton).icon =
                        binding.root.context.getDrawable(R.drawable.ic_favorite)

            }

            // queste funzioni servono a dividere il ruolo di viewModel e view
            setScreenshotUI.invoke(game.screenshots)
            setSimilarGamesUI.invoke(game.similarGames)
            setDLCsUI.invoke(game.dlcs)
            setGenresUI.invoke(game.genres)
            onSuccess.invoke()
        }
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
                Toast.makeText(binding.root.context, binding.root.context.getString(R.string.fav_error), Toast.LENGTH_SHORT).show()
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
                Toast.makeText(binding.root.context, binding.root.context.getString(R.string.fav_error), Toast.LENGTH_SHORT).show()
            }


        }
    }

}

