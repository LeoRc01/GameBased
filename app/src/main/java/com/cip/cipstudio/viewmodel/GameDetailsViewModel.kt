package com.cip.cipstudio.viewmodel

import android.content.Context
import android.os.Build
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.viewModelFactory
import com.cip.cipstudio.model.data.Game
import com.cip.cipstudio.repository.IGDBRepository
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
    val ivGameDetailsCover: ImageView,
) : ViewModel() {

    val igdbRepository : IGDBRepository = IGDBRepository(generate = false)

    var isDescriptionExpanded : Boolean = false

    val platformsLiveData : MutableLiveData<String> by lazy{
        MutableLiveData<String>()
    }

    init {
        tvGameDetailsTitle.text = game.name
        tvGameDetailsDescription.text = game.description
        val dateFormat = SimpleDateFormat("dd/MM/yyyy")
        val date = dateFormat.format(Date(game.releaseDate.toLong() * 1000))
        tvGameDetailsReleaseDate.text = date

        userRating.progress = game.userRatingValue.toInt()
        tvUserRatingValue.text = game.userRatingValue.toInt().toString()
        tvUserRatingCounter.text = "User rating (${ game.userRatingCount.toString() })"

        criticsRating.progress = game.criticsRatingValue.toInt()
        tvCriticsRatingValue.text = game.criticsRatingValue.toInt().toString()
        tvCriticsRatingCounter.text = "Critics rating (${ game.criticsRatingCount.toString() })"

        game.platformsId.forEach {
            igdbRepository.getPlatforms(it){
                platformsLiveData
                    .postValue(it +  if (platformsLiveData.value!=null) "/" + platformsLiveData.value else "")
            }
        }


        platformsLiveData.observeForever{
            tvGameDetailsPlatforms.text = it
        }

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

}

