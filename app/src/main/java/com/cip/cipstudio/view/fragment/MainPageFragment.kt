package com.cip.cipstudio.view.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cip.cipstudio.R
import com.cip.cipstudio.adapters.GamesRecyclerViewAdapter
import com.cip.cipstudio.databinding.FragmentMainPageBinding
import com.cip.cipstudio.model.data.Loading
import com.cip.cipstudio.utils.GameTypeEnum
import com.cip.cipstudio.utils.ActionGameDetailsEnum
import com.cip.cipstudio.viewmodel.MainPageViewModel
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.progressindicator.CircularProgressIndicator

class MainPageFragment : Fragment() {
    private val TAG = "MainPageFragment"

    private lateinit var mainPageViewModel: MainPageViewModel
    private lateinit var mainPageBinding: FragmentMainPageBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainPageBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_main_page, container, false)
        mainPageViewModel = MainPageViewModel()




        mainPageBinding.fMainPageSrlSwipeRefresh.setOnRefreshListener {
            Log.i(TAG, "Refreshing")
            initializeFragment(true)

            Handler(Looper.getMainLooper())
                .postDelayed( {
                    mainPageBinding.fMainPageSrlSwipeRefresh.isRefreshing = false
                }, 2000)
        }

        initializeFragment()

        return mainPageBinding.root
    }

    private fun initializeFragment(refresh: Boolean = false) {
        // Most rated games
        initializeRecyclerView(
            mainPageBinding.fMainPageRvMostRatedGames,
            GameTypeEnum.MOST_RATED,
            mainPageBinding.fMainPageShimmerLayoutMostRatedGames,
            refresh
        )

        // Most hyped games
        initializeRecyclerView(
            mainPageBinding.fMainPageRvMostHypedGames,
            GameTypeEnum.MOST_HYPED,
            mainPageBinding.fMainPageShimmerLayoutMostHypedGames,
            refresh
        )

        // Most popular games
        initializeRecyclerView(
            mainPageBinding.fMainPageRvMostPopularGames,
            GameTypeEnum.MOST_POPULAR,
            mainPageBinding.fMainPageShimmerLayoutMostPopularGames,
            refresh
        )

        // Recently released games
        initializeRecyclerView(
            mainPageBinding.fMainPageRvRecentlyReleasedGames,
            GameTypeEnum.RECENTLY_RELEASED,
            mainPageBinding.fMainPageShimmerLayoutRecentlyReleasedGames,
            refresh
        )

        // Upcoming games
        initializeRecyclerView(
            mainPageBinding.fMainPageRvUpcomingGames,
            GameTypeEnum.UPCOMING,
            mainPageBinding.fMainPageShimmerLayoutUpcomingGames,
            refresh
        )

        // Worst rated games
        initializeRecyclerView(
            mainPageBinding.fMainPageRvWorstRatedGames,
            GameTypeEnum.WORST_RATED,
            mainPageBinding.fMainPageShimmerLayoutWorstRatedGames,
            refresh
        )

        // Loved by critics games
        initializeRecyclerView(
            mainPageBinding.fMainPageRvLovedByCriticsGames,
            GameTypeEnum.LOVED_BY_CRITICS,
            mainPageBinding.fMainPageShimmerLayoutLovedByCriticsGames,
            refresh
        )

        // Best rated games
        initializeRecyclerView(
            mainPageBinding.fMainPageRvBestRatedGames,
            GameTypeEnum.BEST_RATED,
            mainPageBinding.fMainPageShimmerLayoutBestRatedGames,
            refresh
        )
    }

    private fun initializeRecyclerView(
        recyclerView: RecyclerView,
        gameTypeEnum: GameTypeEnum,
        shimmerLayout: ShimmerFrameLayout,
        refresh: Boolean
    ) {
        shimmerLayout.startShimmer()
        shimmerLayout.visibility = View.VISIBLE
        val linearLayoutManager = LinearLayoutManager(requireContext())
        linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL

        val adapter = GamesRecyclerViewAdapter(
            ArrayList(),
            ActionGameDetailsEnum.MAIN_PAGE
        )

        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = adapter
        recyclerView.itemAnimator = null
        recyclerView.setItemViewCacheSize(50)

        mainPageViewModel.initializeRecyclerView(gameTypeEnum, refresh) {
            adapter.importItems(it)
            shimmerLayout.stopShimmer()
            shimmerLayout.visibility = View.GONE
        }
    }
}