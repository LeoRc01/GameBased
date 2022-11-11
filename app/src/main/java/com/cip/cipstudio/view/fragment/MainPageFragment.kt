package com.cip.cipstudio.view.fragment

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
import com.cip.cipstudio.viewmodel.MainPageViewModel
import com.google.android.material.progressindicator.CircularProgressIndicator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainPageFragment : Fragment() {

    private lateinit var viewModel: MainPageViewModel
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
        if (myView == null) {
            myView = inflater.inflate(R.layout.fragment_main_page, container, false)
            viewModel = MainPageViewModel(requireContext())
            mostRatedGamesRecyclerView = myView!!.findViewById(R.id.f_mainPage_rv_mostRatedGames)
            mostHypedGamesRecyclerView = myView!!.findViewById(R.id.f_mainPage_rv_mostHypedGames)
            mostRatedGamesRecyclerViewAdapter =
                GamesRecyclerViewAdapter(
                    requireContext(), ArrayList(),
                    R.id.action_menu_home_to_gameDetailsFragment2
                )
            mostHypedGamesRecyclerViewAdapter =
                GamesRecyclerViewAdapter(
                    requireContext(), ArrayList(),
                    R.id.action_menu_home_to_gameDetailsFragment2
                )
            initializeMostHypedGamesRecyclerView()
            initializeMostRatedGamesRecyclerView()
        }

        (myView!!.parent as ViewGroup?)?.removeView(myView)
        return myView
    }

    private fun initializeRecyclerView(
        recyclerView: RecyclerView,
        adapter: GamesRecyclerViewAdapter,
        getGame: suspend () -> List<GameDetails>,
        updateUI: (List<GameDetails>) -> Unit
    ) {
        val linearLayoutManager = LinearLayoutManager(requireContext())
        linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = adapter
        recyclerView.itemAnimator = null
        recyclerView.setItemViewCacheSize(50)
        viewModel.initializeRecyclerView(recyclerView, adapter, getGame, updateUI)
    }

    private fun initializeMostRatedGamesRecyclerView() {
            initializeRecyclerView(
                mostRatedGamesRecyclerView,
                mostRatedGamesRecyclerViewAdapter,
                { IGDBRepositoryRemote.getGamesMostRated()}
            ) {
                lifecycleScope.launch(Dispatchers.Main)  {
                    mostRatedGamesRecyclerViewAdapter.importItems(it)
                    myView?.findViewById<CircularProgressIndicator>(R.id.f_mainPage_ls_mostRatedGames)
                        ?.visibility = View.GONE
                }
            }
        }

    private fun initializeMostHypedGamesRecyclerView() {
        initializeRecyclerView(
            mostHypedGamesRecyclerView,
            mostHypedGamesRecyclerViewAdapter,
            { IGDBRepositoryRemote.getGamesMostHyped()}
        ) {
            lifecycleScope.launch(Dispatchers.Main) {
                mostHypedGamesRecyclerViewAdapter.importItems(it)
                myView?.findViewById<CircularProgressIndicator>(R.id.f_mainPage_ls_mostHypedGames)
                    ?.visibility = View.GONE
            }
        }
    }
}