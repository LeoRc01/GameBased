package com.cip.cipstudio.viewmodel

import android.app.Activity
import android.content.Context
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cip.cipstudio.R
import com.cip.cipstudio.model.data.Game
import com.cip.cipstudio.repository.IGDBRepository
import com.cip.cipstudio.view.widgets.LoadingSpinner
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.squareup.picasso.Picasso
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*


class GameDetailsViewModel(
    val context: Context,
    val game: Game,
    val tvGameDetailsTitle: TextView,
    val tvGameDetailsDescription: TextView,
    val tvGameDetailsReleaseDate: TextView,
    val tvUserRatingValue: TextView,
    val tvUserRatingCounter: TextView,
    val tvCriticsRatingValue: TextView,
    val tvCriticsRatingCounter: TextView,
    val tvGameDetailsPlatforms: TextView,
    val tvShowMoreDescription: TextView,
    val userRating: CircularProgressIndicator,
    val criticsRating: CircularProgressIndicator,
    val glGridGenreLayout : GridLayout,
    val pageLayout : LinearLayout,
    val ivGameDetailsCover: ImageView,
) : ViewModel() {

    val igdbRepository : IGDBRepository = IGDBRepository(generate = false)

    var isDescriptionExpanded : Boolean = false

    val platformsLiveData : MutableLiveData<String> by lazy{
        MutableLiveData<String>()
    }

    init {
        runBlocking {
            coroutineScope {
                pageLayout.visibility = View.GONE
                LoadingSpinner.showLoadingDialog(context)
                _setGenres{
                    runBlocking {
                        coroutineScope {
                            _setPlatforms{
                                (context as Activity).runOnUiThread {
                                    tvGameDetailsPlatforms.text = it
                                    _doRest()
                                    LoadingSpinner.dismiss()
                                    pageLayout.visibility = View.VISIBLE
                                }

                            }
                        }
                    }

                }


            }
        }
    }

    fun _doRest(){

        tvGameDetailsTitle.text = game.name
        tvGameDetailsDescription.text = game.description
        val dateFormat = SimpleDateFormat("dd/MM/yyyy")
        val date = dateFormat.format(Date(game.releaseDate.toLong() * 1000))
        tvGameDetailsReleaseDate.text = date

        userRating.progress = game.userRatingValue.toInt()
        tvUserRatingValue.text = game.userRatingValue.toInt().toString()
        tvUserRatingCounter.text = "User rating (${game.userRatingCount.toString()})"

        criticsRating.progress = game.criticsRatingValue.toInt()
        tvCriticsRatingValue.text = game.criticsRatingValue.toInt().toString()
        tvCriticsRatingCounter.text = "Critics rating (${game.criticsRatingCount.toString()})"


        tvShowMoreDescription.setOnClickListener {
            val params: ViewGroup.LayoutParams = tvGameDetailsDescription.getLayoutParams()
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            tvGameDetailsDescription.setLayoutParams(params);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                tvGameDetailsDescription.foreground = null
            }
            tvShowMoreDescription.visibility = View.GONE

        }
        Picasso.get().load("https:${game.cover_url}").into(ivGameDetailsCover)

    }

    suspend fun _setPlatforms(onSuccess: (String) -> Unit) {
        var platformsString = ""
        igdbRepository.getPlatforms(game.platformsId) { arr ->
            (0 until arr.length()).forEach {
                val _platform = arr.getJSONObject(it)
                platformsString = _platform.getString("name") + if (platformsString != "") " / " + platformsString else ""
            }
            onSuccess.invoke(platformsString)
        }
    }

    suspend fun _setGenres(onSuccess: ()->Unit){
        var genreStrings :  ArrayList<String> = arrayListOf()
        igdbRepository.getGenres(game.genreIds){ arr->
            (0 until arr.length()).forEach {
                val _genre = arr.getJSONObject(it)
                genreStrings.add(_genre.getString("name"))
                (context as Activity).runOnUiThread {
                    glGridGenreLayout.addView(_createChip(_genre.getString("name")))
                }
            }
            onSuccess.invoke()
        }
    }

    fun _createChip(label : String) : Chip{
        val chip = Chip(context, null, R.layout.genre_chip)
        val chipDrawable = ChipDrawable.createFromAttributes(
            context,
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

