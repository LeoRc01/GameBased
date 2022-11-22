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
import com.cip.cipstudio.utils.GameTypeEnum
import com.cip.cipstudio.utils.IsFromFragmentEnum
import com.cip.cipstudio.viewmodel.MainPageViewModel
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
            mainPageBinding.fMainPageLsMostRatedGames,
            refresh
        )

        // Most hyped games
        initializeRecyclerView(
            mainPageBinding.fMainPageRvMostHypedGames,
            GameTypeEnum.MOST_HYPED,
            mainPageBinding.fMainPageLsMostHypedGames,
            refresh
        )
    }

    private fun initializeRecyclerView(
        recyclerView: RecyclerView,
        gameTypeEnum: GameTypeEnum,
        circularProgressIndicator: CircularProgressIndicator,
        refresh: Boolean
    ) {
        circularProgressIndicator.visibility = View.VISIBLE
        val linearLayoutManager = LinearLayoutManager(requireContext())
        linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL

        val adapter = GamesRecyclerViewAdapter(
            requireContext(),
            ArrayList(),
            IsFromFragmentEnum.MAIN_PAGE
        )

        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = adapter
        recyclerView.itemAnimator = null
        recyclerView.setItemViewCacheSize(50)

        mainPageViewModel.initializeRecyclerView(gameTypeEnum, refresh) {
            adapter.importItems(it)
            circularProgressIndicator.visibility = View.GONE
        }
    }
}