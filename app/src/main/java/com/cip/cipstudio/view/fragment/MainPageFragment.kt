package com.cip.cipstudio.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cip.cipstudio.R
import com.cip.cipstudio.adapters.GamesRecyclerViewAdapter
import com.cip.cipstudio.model.data.Game
import com.cip.cipstudio.repository.IGDBRepository
import com.cip.cipstudio.viewmodel.MainPageViewModel
import com.google.android.material.progressindicator.CircularProgressIndicator

class MainPageFragment : Fragment() {

    private lateinit var viewModel: MainPageViewModel
    private lateinit var gameRepository: IGDBRepository
    private val BASE_PAYLOAD =
        "fields *; where rating_count > 0 & total_rating_count > 0 & aggregated_rating_count > 0;"

    private lateinit var mostRatedGamesRecyclerView: RecyclerView
    private lateinit var mostHypedGamesRecyclerView: RecyclerView
    private lateinit var mostRatedGamesRecyclerViewAdapter: GamesRecyclerViewAdapter
    private lateinit var mostHypedGamesRecyclerViewAdapter: GamesRecyclerViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_main_page, container, false)
        viewModel = MainPageViewModel(requireContext())
        gameRepository = IGDBRepository()
        mostRatedGamesRecyclerView = view.findViewById(R.id.f_mainPage_rv_mostRatedGames)
        mostHypedGamesRecyclerView = view.findViewById(R.id.f_mainPage_rv_mostHypedGames)
        mostRatedGamesRecyclerViewAdapter =
            GamesRecyclerViewAdapter(requireContext(), ArrayList<Game>())
        mostHypedGamesRecyclerViewAdapter =
            GamesRecyclerViewAdapter(requireContext(), ArrayList<Game>())
        initializeMostHypedGamesRecyclerView()
        initializeMostRatedGamesRecyclerView()
        return view
    }

    private fun initializeRecyclerView(
        recyclerView: RecyclerView,
        adapter: GamesRecyclerViewAdapter,
        payload: String,
        updateUI: (ArrayList<Game>) -> Unit
    ) {
        val linearLayoutManager = LinearLayoutManager(requireContext())
        linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = adapter
        recyclerView.itemAnimator = null
        recyclerView.setItemViewCacheSize(50)
        viewModel.initializeRecyclerView(recyclerView, adapter, payload, updateUI)
    }

    private fun initializeMostRatedGamesRecyclerView() {
            initializeRecyclerView(
                mostRatedGamesRecyclerView,
                mostRatedGamesRecyclerViewAdapter,
                "$BASE_PAYLOAD sort total_rating desc;"
            ) {
                mostRatedGamesRecyclerViewAdapter.importItems(it)
                view?.findViewById<CircularProgressIndicator>(R.id.f_mainPage_ls_mostRatedGames)
                    ?.visibility = View.GONE
            }
        }

    private fun initializeMostHypedGamesRecyclerView() {
        initializeRecyclerView(
            mostHypedGamesRecyclerView,
            mostHypedGamesRecyclerViewAdapter,
            "$BASE_PAYLOAD sort hype desc;"
        ) {
            mostHypedGamesRecyclerViewAdapter.importItems(it)
            view?.findViewById<CircularProgressIndicator>(R.id.f_mainPage_ls_mostRatedGames)
                ?.visibility = View.GONE
        }
    }
}