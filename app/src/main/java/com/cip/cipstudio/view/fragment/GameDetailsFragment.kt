package com.cip.cipstudio.view.fragment

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cip.cipstudio.R
import com.cip.cipstudio.adapters.GameScreenshotsRecyclerViewAdapter
import com.cip.cipstudio.adapters.GamesRecyclerViewAdapter
import com.cip.cipstudio.databinding.FragmentGameDetailsBinding
import com.cip.cipstudio.model.data.GameDetails
import com.cip.cipstudio.model.data.Loading
import com.cip.cipstudio.repository.IGDBRepositoryRemote
import com.cip.cipstudio.view.widgets.LoadingSpinner
import com.cip.cipstudio.viewmodel.GameDetailsViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import proto.Screenshot

class GameDetailsFragment : Fragment() {
    private lateinit var gameDetailsViewModel: GameDetailsViewModel
    private lateinit var gameDetailsBinding: FragmentGameDetailsBinding

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        gameDetailsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_game_details, container, false)

        gameDetailsBinding.fGameDetailsClPageLayout.visibility = View.GONE
        //LoadingSpinner.showLoadingDialog(requireContext())
        gameDetailsBinding.loadingModel = Loading()

        initializeFragment(){

        }
        gameDetailsBinding.lifecycleOwner = this
        return gameDetailsBinding.root
    }



    private fun initializeShowMore() {
        gameDetailsBinding.fGameDetailsTvShowMoreDescription.setOnClickListener {
            val params = gameDetailsBinding.fGameDetailsTvGameDetailsDescription.layoutParams
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT
            gameDetailsBinding.fGameDetailsTvGameDetailsDescription.layoutParams = params
            gameDetailsBinding.fGameDetailsTvShowMoreDescription.visibility = View.GONE
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                gameDetailsBinding.fGameDetailsTvGameDetailsDescription.foreground = null
            }
        }
    }


    private fun initializeFragment(onSuccess: () -> Unit = {}) {
        val gameId = arguments?.get("game_id") as String
        gameDetailsViewModel = GameDetailsViewModel(
            gameId,
            gameDetailsBinding,
            { setScreenshots(it)},
            { setSimilarGames(it)},
            { setPlatforms(it)},
            { setGenres(it)},
        ) {
            // onSuccess
            gameDetailsBinding.vm = gameDetailsViewModel
            gameDetailsBinding.fGameDetailsClPageLayout.visibility = View.VISIBLE
            gameDetailsBinding.loadingModel!!.isPageLoading.postValue(false)
            initializeShowMore()

        }
    }

    private fun setScreenshots(screenshotList: List<JSONObject>) {
        val screenhotsRecyclerView = gameDetailsBinding.fGameDetailsRvScreenshots
        val manager = LinearLayoutManager(requireContext())
        manager.orientation = RecyclerView.HORIZONTAL
        val rvGameScreenshotsAdapter = GameScreenshotsRecyclerViewAdapter(requireContext(), screenshotList )
        screenhotsRecyclerView.layoutManager = manager
        screenhotsRecyclerView.setItemViewCacheSize(50)
        screenhotsRecyclerView.itemAnimator = null
        screenhotsRecyclerView.adapter = rvGameScreenshotsAdapter
    }

    private fun setSimilarGames(similarGamesList: List<GameDetails>) {
        val similarGamesRecyclerView = gameDetailsBinding.fGameDetailsRvSimilarGames
        val manager = LinearLayoutManager(requireContext())
        manager.orientation = RecyclerView.HORIZONTAL
        val rvSimilarGamesAdapter = GamesRecyclerViewAdapter(requireContext(), similarGamesList, R.id.action_gameDetailsFragment2_self)
        similarGamesRecyclerView.layoutManager = manager
        similarGamesRecyclerView.setItemViewCacheSize(50)
        similarGamesRecyclerView.itemAnimator = null
        similarGamesRecyclerView.adapter = rvSimilarGamesAdapter
    }

    private fun createChip(label: String):Chip {
        val chip = Chip(requireContext(), null, R.layout.genre_chip)
        val chipDrawable = ChipDrawable.createFromAttributes(
            requireContext(),
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

    private fun setGenres(genres: List<JSONObject>) {
        for (genre in genres) {
            gameDetailsBinding.fGameDetailsGlGridGenreLayout.addView(
                createChip(
                    genre.getString("name")
                )
            )
        }
    }

    private fun setPlatforms(platforms: String) {
        gameDetailsBinding.fGameDetailsTvGameDetailsPlatforms.text = platforms
    }

}