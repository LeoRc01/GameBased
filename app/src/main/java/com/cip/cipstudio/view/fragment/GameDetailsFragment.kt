package com.cip.cipstudio.view.fragment


import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import com.cip.cipstudio.model.data.PlatformDetails
import com.cip.cipstudio.utils.ActionGameDetailsEnum
import com.cip.cipstudio.viewmodel.GameDetailsViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import org.json.JSONObject


class GameDetailsFragment : Fragment() {
    private val TAG = "GameDetailsFragment"

    private lateinit var gameDetailsViewModel: GameDetailsViewModel
    private lateinit var gameDetailsBinding: FragmentGameDetailsBinding

    private lateinit var originFragment : ActionGameDetailsEnum

    private var showMore = true

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        gameDetailsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_game_details, container, false)

        gameDetailsBinding.fGameDetailsClPageLayout.visibility = View.GONE

        gameDetailsBinding.loadingModel = Loading()

        gameDetailsBinding.fGameDetailsSrlSwipeRefresh.setOnRefreshListener {
            Log.i(TAG, "Refreshing game details page")
            initializeFragment(true)
            Handler(Looper.getMainLooper())
                .postDelayed( {
                    gameDetailsBinding.fGameDetailsSrlSwipeRefresh.isRefreshing = false
                }, 2000)
        }

        initializeFragment()

        gameDetailsBinding.lifecycleOwner = this
        return gameDetailsBinding.root
    }



    @RequiresApi(Build.VERSION_CODES.M)
    private fun hideShowMore() {
        val params = gameDetailsBinding.fGameDetailsTvGameDetailsDescription.layoutParams
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT
        gameDetailsBinding.fGameDetailsTvGameDetailsDescription.layoutParams = params
        gameDetailsBinding.fGameDetailsTvShowMoreDescription.visibility = View.GONE
        gameDetailsBinding.fGameDetailsTvGameDetailsDescription.foreground = null
        showMore = false
    }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun initializeFragment(refresh : Boolean = false) {
        val gameId = arguments?.get("game_id") as String

        gameDetailsViewModel = GameDetailsViewModel(
            gameId,
            gameDetailsBinding,
            refresh,
            { setScreenshots(it) },
            { setSimilarGames(it) },
            { setDlCs(it) },
            { setPlatforms(it) },
            { setGenres(it) },
        ) {
            // onSuccess
            gameDetailsBinding.vm = gameDetailsViewModel
            gameDetailsBinding.fGameDetailsClPageLayout.visibility = View.VISIBLE
            gameDetailsBinding.loadingModel!!.isPageLoading.postValue(false)
            gameDetailsBinding.fGameDetailsTvShowMoreDescription.setOnClickListener {
                hideShowMore()
            }
        }
    }

    private fun setScreenshots(screenshotList: List<JSONObject>) {
        val screenshotsRecyclerView = gameDetailsBinding.fGameDetailsRvScreenshots
        val manager = LinearLayoutManager(context)
        manager.orientation = RecyclerView.HORIZONTAL
        val rvGameScreenshotsAdapter = GameScreenshotsRecyclerViewAdapter(screenshotList,
            R.id.action_gameDetailsFragment_to_gameScreenshotDialog)
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
        val rvGamesAdapter = GamesRecyclerViewAdapter(listGame)
        recyclerView.layoutManager = manager
        recyclerView.setItemViewCacheSize(50)
        recyclerView.itemAnimator = null
        recyclerView.adapter = rvGamesAdapter
    }

    private fun setGenres(genres: List<JSONObject>) {
        gameDetailsBinding.fGameDetailsGlGridGenreLayout.removeAllViews()
        for (genre in genres) {
            checkIfFragmentAttached {
                val chip = Chip(requireContext(), null, R.layout.genre_chip)
                val chipDrawable = ChipDrawable.createFromAttributes(
                    requireContext(),
                    null,
                    0,
                    R.style.genre_chip
                )
                chip.setChipDrawable(chipDrawable)
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(15, 0, 15, 0)
                chip.layoutParams = params
                chip.text = genre.getString("name")
                chip.typeface= ResourcesCompat.getFont(requireContext(), R.font.montserrat_regular)
                chip.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                gameDetailsBinding.fGameDetailsGlGridGenreLayout
                    .addView(chip)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun setPlatforms(platforms: List<PlatformDetails>) {
        gameDetailsBinding.fGameDetailsGlGridPlatformsLayout.removeAllViews()
        for (platform in platforms) {
            checkIfFragmentAttached {
                val text = TextView(requireContext(), null, R.layout.platform_item)
                text.setTextColor(requireContext().getColor(R.color.primary_color))
                val typeface = ResourcesCompat.getFont(requireContext(), R.font.montserrat_regular)
                text.setTypeface(typeface)
                val content = SpannableString(platform.name)
                content.setSpan(UnderlineSpan(), 0, platform.name.length, 0)
                text.text = content
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(0, 0, 15, 0)
                text.layoutParams = params
                text.setOnClickListener {
                    val bundle = bundleOf()
                    bundle.putSerializable("platform", platform)
                    findNavController().navigate(R.id.action_gameDetailsFragment_to_platformDetailsDialog, bundle)
                }

                gameDetailsBinding.fGameDetailsGlGridPlatformsLayout.addView(
                    text
                )
            }
        }
    }

    fun checkIfFragmentAttached(operation: Context.() -> Unit) {
        if (isAdded && context != null) {
            operation(requireContext())
        }
    }
}