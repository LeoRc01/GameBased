package com.cip.cipstudio.view.fragment

import android.os.Build
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cip.cipstudio.R
import com.cip.cipstudio.adapters.GameScreenshotsRecyclerViewAdapter
import com.cip.cipstudio.adapters.GamesRecyclerViewAdapter
import com.cip.cipstudio.databinding.FragmentGameDetailsBinding
import com.cip.cipstudio.model.data.GameDetails
import com.cip.cipstudio.model.data.Loading
import com.cip.cipstudio.utils.IsFromFragmentEnum
import com.cip.cipstudio.model.data.PlatformDetails
import com.cip.cipstudio.view.dialog.PlatformDetailsDialog
import com.cip.cipstudio.viewmodel.GameDetailsViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import org.json.JSONObject
import kotlin.properties.Delegates


class GameDetailsFragment : Fragment() {
    private lateinit var gameDetailsViewModel: GameDetailsViewModel
    private lateinit var gameDetailsBinding: FragmentGameDetailsBinding
    private val TAG : String = "GameDetailsFragment"
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


    @RequiresApi(Build.VERSION_CODES.M)
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
        val isFromFavourite = arguments?.get("isFromFavouriteScreen")
        val isFromSearchScreen = arguments?.get("isFromSearchScreen")
        var action = 0
        if(isFromFavourite != null && isFromFavourite as Boolean)
            action = R.id.action_gameDetailsFragment3_to_gameScreenshotDialog2
        else if(isFromSearchScreen != null && isFromSearchScreen as Boolean)
            action = R.id.action_gameDetailsFragment4_to_gameScreenshotDialog3
        else
            action = R.id.action_gameDetailsFragment2_to_gameScreenshotDialog
        val rvGameScreenshotsAdapter = GameScreenshotsRecyclerViewAdapter(requireContext(),
            screenshotList,
            action)
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
        chip.typeface= ResourcesCompat.getFont(requireContext(), R.font.montserrat_regular)
        chip.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
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

    @RequiresApi(Build.VERSION_CODES.M)
    private fun setPlatforms(platforms: List<PlatformDetails>) {
        for (platform in platforms){
            gameDetailsBinding.fGameDetailsGlGridPlatformsLayout.addView(
                _setPlatformsView(platform)
            )
        }
        //gameDetailsBinding.fGameDetailsTvGameDetailsPlatforms.text = platforms
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun _setPlatformsView(platform : PlatformDetails) : TextView{
        val text = TextView(requireContext(), null, R.layout.platform_item)
        text.setTextColor(requireContext().getColor(R.color.primary_color))

        val content = SpannableString(platform.name)
        content.setSpan(UnderlineSpan(), 0, platform.name.length, 0)
        text.text = content
        text.typeface= ResourcesCompat.getFont(requireContext(), R.font.montserrat_regular)
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(0, 0, 15, 0)
        text.layoutParams = params
        text.setOnClickListener {
            val view = layoutInflater.inflate(R.layout.platform_bottom_sheet, null)
            //val dialog = PlatformDetailsDialog(platform)
            //dialog.show(parentFragmentManager, "PLATFORM")
            val bundle = bundleOf()
            bundle.putSerializable("platform", platform)
            val isFromFavourite = arguments?.get("isFromFavouriteScreen")
            val isFromSearchScreen = arguments?.get("isFromSearchScreen")
            if(isFromFavourite != null && isFromFavourite as Boolean)
                findNavController().navigate(R.id.action_gameDetailsFragment3_to_platformDetailsDialog2, bundle)
            else if(isFromSearchScreen != null && isFromSearchScreen as Boolean)
                findNavController().navigate(R.id.action_gameDetailsFragment4_to_platformDetailsDialog3, bundle)
            else
                findNavController().navigate(R.id.action_gameDetailsFragment2_to_platformDetailsDialog, bundle)
        }
        return text
    }
}