package com.cip.cipstudio.view.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cip.cipstudio.R
import com.cip.cipstudio.utils.StateInstanceSaver
import com.cip.cipstudio.adapters.GamesRecyclerViewAdapter
import com.cip.cipstudio.dataSource.repository.AISelector
import com.cip.cipstudio.databinding.FragmentMainPageBinding
import com.cip.cipstudio.utils.GameTypeEnum
import com.cip.cipstudio.utils.ActionGameDetailsEnum
import com.cip.cipstudio.viewmodel.MainPageViewModel
import com.facebook.shimmer.ShimmerFrameLayout

class MainPageFragment : Fragment() {
    private val TAG = "MainPageFragment"

    private lateinit var mainPageViewModel: MainPageViewModel
    private lateinit var mainPageBinding: FragmentMainPageBinding
    private val tagPosition = "position"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainPageBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_main_page, container, false)
        mainPageViewModel = MainPageViewModel(){
            initializeFragment()
        }
        StateInstanceSaver.deleteState("GameListFragment")


        mainPageBinding.fMainPageSrlSwipeRefresh.setOnRefreshListener {
            Log.i(TAG, "Refreshing")
            initializeFragment(true)

            Handler(Looper.getMainLooper())
                .postDelayed( {
                    mainPageBinding.fMainPageSrlSwipeRefresh.isRefreshing = false
                }, 2000)
        }

        return mainPageBinding.root
    }

    override fun onResume() {
        super.onResume()
        val map = StateInstanceSaver.restoreState(TAG)
        if (map != null && map.containsKey(tagPosition)) {
            mainPageBinding.fMainPageScrollView.y = map[tagPosition] as Float
        }
    }

    override fun onPause() {
        super.onPause()
        val map : HashMap<String, Any> = HashMap()
        map[tagPosition] = mainPageBinding.fMainPageScrollView.y
        StateInstanceSaver.saveState(TAG, map)
    }

    private fun initializeFragment(refresh: Boolean = false) {

        // For you games
        if(AISelector.weightedItems.isNotEmpty()){
            mainPageBinding.fMainPageRvForYou.visibility = View.VISIBLE
            mainPageBinding.fMainPageTvForYou.visibility = View.VISIBLE
            mainPageBinding.fMainPageShimmerLayoutForYou.visibility = View.VISIBLE
            initializeRecyclerView(
                mainPageBinding.fMainPageRvForYou,
                GameTypeEnum.FOR_YOU,
                mainPageBinding.fMainPageShimmerLayoutForYou,
                refresh
            )
            mainPageBinding.fMainPageTvForYou.setOnClickListener {
                val bundle = bundleOf("gameType" to GameTypeEnum.FOR_YOU.name)
                findNavController().navigate(R.id.action_homeScreen_to_gameListFragment, bundle)
            }
        }else{
            mainPageBinding.fMainPageRvForYou.visibility = View.GONE
            mainPageBinding.fMainPageTvForYou.visibility = View.GONE
            mainPageBinding.fMainPageShimmerLayoutForYou.visibility = View.GONE
        }


        // Most rated games
        initializeRecyclerView(
            mainPageBinding.fMainPageRvMostRatedGames,
            GameTypeEnum.MOST_RATED,
            mainPageBinding.fMainPageShimmerLayoutMostRatedGames,
            refresh
        )
        mainPageBinding.fMainPageTvMostRatedGames.setOnClickListener {
            val bundle = bundleOf("gameType" to GameTypeEnum.MOST_RATED.name)
            findNavController().navigate(R.id.action_homeScreen_to_gameListFragment, bundle)
        }

        // Most hyped games
        initializeRecyclerView(
            mainPageBinding.fMainPageRvMostHypedGames,
            GameTypeEnum.MOST_HYPED,
            mainPageBinding.fMainPageShimmerLayoutMostHypedGames,
            refresh
        )
        mainPageBinding.fMainPageTvMostHypedGames.setOnClickListener {
            val bundle = bundleOf("gameType" to GameTypeEnum.MOST_HYPED.name)
            findNavController().navigate(R.id.action_homeScreen_to_gameListFragment, bundle)
        }

        // Most popular games
        initializeRecyclerView(
            mainPageBinding.fMainPageRvMostPopularGames,
            GameTypeEnum.MOST_POPULAR,
            mainPageBinding.fMainPageShimmerLayoutMostPopularGames,
            refresh
        )
        mainPageBinding.fMainPageTvMostPopularGames.setOnClickListener {
            val bundle = bundleOf("gameType" to GameTypeEnum.MOST_POPULAR.name)
            findNavController().navigate(R.id.action_homeScreen_to_gameListFragment, bundle)
        }

        // Recently released games
        initializeRecyclerView(
            mainPageBinding.fMainPageRvRecentlyReleasedGames,
            GameTypeEnum.RECENTLY_RELEASED,
            mainPageBinding.fMainPageShimmerLayoutRecentlyReleasedGames,
            refresh
        )
        mainPageBinding.fMainPageTvRecentlyReleasedGames.setOnClickListener {
            val bundle = bundleOf("gameType" to GameTypeEnum.RECENTLY_RELEASED.name)
            findNavController().navigate(R.id.action_homeScreen_to_gameListFragment, bundle)
        }

        // Upcoming games
        initializeRecyclerView(
            mainPageBinding.fMainPageRvUpcomingGames,
            GameTypeEnum.UPCOMING,
            mainPageBinding.fMainPageShimmerLayoutUpcomingGames,
            refresh
        )
        mainPageBinding.fMainPageTvUpcomingGames.setOnClickListener {
            val bundle = bundleOf("gameType" to GameTypeEnum.UPCOMING.name)
            findNavController().navigate(R.id.action_homeScreen_to_gameListFragment, bundle)
        }

        // Worst rated games
        initializeRecyclerView(
            mainPageBinding.fMainPageRvWorstRatedGames,
            GameTypeEnum.WORST_RATED,
            mainPageBinding.fMainPageShimmerLayoutWorstRatedGames,
            refresh
        )
        mainPageBinding.fMainPageTvWorstRatedGames.setOnClickListener {
            val bundle = bundleOf("gameType" to GameTypeEnum.WORST_RATED.name)
            findNavController().navigate(R.id.action_homeScreen_to_gameListFragment, bundle)
        }

        // Loved by critics games
        initializeRecyclerView(
            mainPageBinding.fMainPageRvLovedByCriticsGames,
            GameTypeEnum.LOVED_BY_CRITICS,
            mainPageBinding.fMainPageShimmerLayoutLovedByCriticsGames,
            refresh
        )
        mainPageBinding.fMainPageTvLovedByCriticsGames.setOnClickListener {
            val bundle = bundleOf("gameType" to GameTypeEnum.LOVED_BY_CRITICS.name)
            findNavController().navigate(R.id.action_homeScreen_to_gameListFragment, bundle)
        }

        // Best rated games
        initializeRecyclerView(
            mainPageBinding.fMainPageRvBestRatedGames,
            GameTypeEnum.BEST_RATED,
            mainPageBinding.fMainPageShimmerLayoutBestRatedGames,
            refresh
        )
        mainPageBinding.fMainPageTvBestRatedGames.setOnClickListener {
            val bundle = bundleOf("gameType" to GameTypeEnum.BEST_RATED.name)
            findNavController().navigate(R.id.action_homeScreen_to_gameListFragment, bundle)
        }
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