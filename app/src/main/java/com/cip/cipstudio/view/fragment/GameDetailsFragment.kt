package com.cip.cipstudio.view.fragment

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cip.cipstudio.R
import com.cip.cipstudio.adapters.GameScreenshotsRecyclerViewAdapter
import com.cip.cipstudio.adapters.GamesRecyclerViewAdapter
import com.cip.cipstudio.databinding.FragmentGameDetailsBinding
import com.cip.cipstudio.model.data.GameDetails
import com.cip.cipstudio.model.data.Loading
import com.cip.cipstudio.utils.IsFromFragmentEnum
import com.cip.cipstudio.viewmodel.GameDetailsViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import org.json.JSONObject

class GameDetailsFragment : Fragment() {
    private lateinit var gameDetailsViewModel: GameDetailsViewModel
    private lateinit var gameDetailsBinding: FragmentGameDetailsBinding

    private lateinit var originFragment : IsFromFragmentEnum

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        gameDetailsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_game_details, container, false)

        gameDetailsBinding.fGameDetailsClPageLayout.visibility = View.GONE

        gameDetailsBinding.loadingModel = Loading()

        initializeFragment()

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


    private fun initializeFragment() {
        val gameId = arguments?.get("game_id") as String
        originFragment = IsFromFragmentEnum.valueOf(arguments?.get("origin_fragment") as String)
        if (originFragment == IsFromFragmentEnum.MAIN_PAGE)
            originFragment = IsFromFragmentEnum.HOME

        gameDetailsViewModel = GameDetailsViewModel(
            gameId,
            gameDetailsBinding,
            { setScreenshots(it) },
            { setSimilarGames(it) },
            { setDlCs(it) },
            { setGenres(it) },
        ) {
            // onSuccess
            gameDetailsBinding.vm = gameDetailsViewModel
            gameDetailsBinding.fGameDetailsClPageLayout.visibility = View.VISIBLE
            gameDetailsBinding.loadingModel!!.isPageLoading.postValue(false)
            initializeShowMore()

        }
    }

    private fun setScreenshots(screenshotList: List<JSONObject>) {
        val screenshotsRecyclerView = gameDetailsBinding.fGameDetailsRvScreenshots
        val manager = LinearLayoutManager(context)
        manager.orientation = RecyclerView.HORIZONTAL
        val rvGameScreenshotsAdapter = GameScreenshotsRecyclerViewAdapter(requireContext(), screenshotList )
        screenshotsRecyclerView.layoutManager = manager
        screenshotsRecyclerView.setItemViewCacheSize(50)
        screenshotsRecyclerView.itemAnimator = null
        screenshotsRecyclerView.adapter = rvGameScreenshotsAdapter
    }

    private fun setSimilarGames(similarGamesList: List<GameDetails>) {
        val similarGamesRecyclerView = gameDetailsBinding.fGameDetailsRvSimilarGames
        initRecyclerView(similarGamesList, similarGamesRecyclerView)
    }

    private fun setDlCs(dlcsList: List<GameDetails>) {
        val dlcsRecyclerView = gameDetailsBinding.fGameDetailsRvDlcs
        initRecyclerView(dlcsList, dlcsRecyclerView)
    }

    private fun initRecyclerView(listGame: List<GameDetails>, recyclerView: RecyclerView) {
        val manager = LinearLayoutManager(context)
        manager.orientation = RecyclerView.HORIZONTAL
        val rvGamesAdapter = GamesRecyclerViewAdapter(requireContext(), listGame, originFragment)
        recyclerView.layoutManager = manager
        recyclerView.setItemViewCacheSize(50)
        recyclerView.itemAnimator = null
        recyclerView.adapter = rvGamesAdapter
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

}