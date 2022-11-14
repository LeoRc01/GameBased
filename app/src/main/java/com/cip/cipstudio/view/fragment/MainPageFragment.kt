package com.cip.cipstudio.view.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cip.cipstudio.R
import com.cip.cipstudio.adapters.GamesRecyclerViewAdapter
import com.cip.cipstudio.model.data.GameDetails
import com.cip.cipstudio.repository.IGDBRepositoryRemote
import com.cip.cipstudio.utils.GameTypeEnum
import com.cip.cipstudio.viewmodel.MainPageViewModel
import com.google.android.material.progressindicator.CircularProgressIndicator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainPageFragment : Fragment() {

    private lateinit var viewModel: MainPageViewModel
    private val gameRepository = IGDBRepositoryRemote
    private val BASE_PAYLOAD =
        "fields *; where rating_count > 0 & total_rating_count > 0 & aggregated_rating_count > 0;"

    private lateinit var mostRatedGamesRecyclerView: RecyclerView
    private lateinit var mostHypedGamesRecyclerView: RecyclerView
    private lateinit var mostRatedGamesRecyclerViewAdapter: GamesRecyclerViewAdapter
    private lateinit var mostHypedGamesRecyclerViewAdapter: GamesRecyclerViewAdapter

    private var myView : View? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_main_page, container, false)
        viewModel = MainPageViewModel(requireContext())

        // Most rated games
        initializeRecyclerView(
            R.id.f_mainPage_rv_mostRatedGames,
            GameTypeEnum.MOST_RATED,
            R.id.f_mainPage_ls_mostRatedGames,
            view
        )

        // Most hyped games
        initializeRecyclerView(
            R.id.f_mainPage_rv_mostHypedGames,
            GameTypeEnum.MOST_HYPED,
            R.id.f_mainPage_ls_mostHypedGames,
            view
        )

        return view
    }

    private fun initializeRecyclerView(
        recyclerViewId: Int,
        gameTypeEnum: GameTypeEnum,
        circularProgressIndicatorId: Int,
        view : View
    ) {
        val linearLayoutManager = LinearLayoutManager(requireContext())
        linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL

        val adapter = GamesRecyclerViewAdapter(
            requireContext(),
            ArrayList(),
            R.id.action_menu_home_to_gameDetailsFragment2
        )

        val recyclerView = view.findViewById<RecyclerView>(recyclerViewId)
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = adapter
        recyclerView.itemAnimator = null
        recyclerView.setItemViewCacheSize(50)

        viewModel.initializeRecyclerView(gameTypeEnum) {
            adapter.importItems(it)
            view.findViewById<CircularProgressIndicator>(circularProgressIndicatorId)
                .visibility = View.GONE
        }
    }
}