package com.cip.cipstudio.view.fragment


import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.cip.cipstudio.R
import com.cip.cipstudio.adapters.FavouriteGridViewAdapter
import com.cip.cipstudio.dataSource.filter.Filter
import com.cip.cipstudio.dataSource.filter.FilterContainer
import com.cip.cipstudio.dataSource.filter.criteria.*
import com.cip.cipstudio.databinding.FragmentGameListBinding
import com.cip.cipstudio.model.data.PlatformDetails
import com.cip.cipstudio.utils.ActionGameDetailsEnum
import com.cip.cipstudio.utils.GameTypeEnum
import com.cip.cipstudio.viewmodel.GameListViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import org.json.JSONObject


class GameListFragment : Fragment() {

    private lateinit var gameListViewModel: GameListViewModel
    private lateinit var gameListBinding: FragmentGameListBinding
    private val TAG = "GameListFragment"

    private lateinit var filter: Filter

    private var offset : Int = 0
    private var yearMin : Int = 0
    private var yearMax : Int = 0

    private lateinit var gameType: GameTypeEnum

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        gameType = GameTypeEnum.valueOf(arguments?.getString("gameType")!!)
        gameListBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_game_list, container, false)
        gameListViewModel = GameListViewModel()
        gameListBinding.vm = gameListViewModel
        gameListBinding.title = getString(gameType.getName())
        gameListBinding.lifecycleOwner = this

        filter = Filter(gameListBinding.fGameListFlFilter,
                        gameListViewModel,
                        layoutInflater,
                        resources,
                        gameType)

        initializeGames()
        val filterContainer = savedInstanceState
            ?.getSerializable("filter_container") as FilterContainer
        filter.initializeFilters(filterContainer)
        initializeDrawer()




        gameListBinding.fGameListBtnFilter.setOnClickListener {
            gameListBinding.drawerLayout.openDrawer(GravityCompat.END)
        }





        return gameListBinding.root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable("filter_container", filter.getContainer())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initializeDrawer() {
        gameListBinding.drawerLayout.addDrawerListener(object : androidx.drawerlayout.widget.DrawerLayout.DrawerListener {
            override fun onDrawerStateChanged(newState: Int) {
            }

            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
            }

            override fun onDrawerClosed(drawerView: View) {
                filter.buildFilterContainer()
                initializeGames()
            }

            override fun onDrawerOpened(drawerView: View) {

            }
        })
    }


    private fun initializeGames() {
        gameListBinding.fGameListLoading.visibility = View.VISIBLE
        gameListBinding.fGameListGvGames.visibility = View.GONE
        gameListBinding.fGameListLoadingNoGamesFound.root.visibility = View.GONE
        gameListViewModel.getGames(gameType, filter.getFilterCriteria()){
            gameListBinding.fGameListLoading.visibility = View.GONE
            gameListBinding.fGameListGvGames.visibility = View.VISIBLE
            gameListBinding.fGameListBtnBack.backButton.setOnClickListener {
                findNavController().popBackStack()
            }

            if(it.isEmpty()){
                gameListBinding.fGameListLoadingNoGamesFound.root.visibility = View.VISIBLE
            }
            checkIfFragmentAttached {
                val gvAdapter = FavouriteGridViewAdapter(
                    requireContext(),
                    it,
                    gameListBinding.root.findNavController(),
                    ActionGameDetailsEnum.GAME_LIST
                )
                gameListBinding.fGameListGvGames.adapter = gvAdapter
            }

            gameListBinding.fGameListGvGames.setOnScrollListener(object : AbsListView.OnScrollListener {
                override fun onScroll(
                    view: AbsListView?,
                    firstVisibleItem: Int,
                    visibleItemCount: Int,
                    totalItemCount: Int
                ) {
                    if (firstVisibleItem + visibleItemCount >= totalItemCount - 2 &&
                        gameListViewModel.isPageLoading.value == false &&
                        gameListViewModel.isMoreDataAvailable.value == true) {
                        offset++
                        gameListViewModel.getGames(gameType, filter.getFilterCriteria(), offset){ games ->

                            (gameListBinding.fGameListGvGames.adapter as FavouriteGridViewAdapter)
                                .addMoreGames(games)
                        }
                    }
                }

                override fun onScrollStateChanged(view: AbsListView?, scrollState: Int) {}
            })

        }
    }

    private fun checkIfFragmentAttached(operation: Context.() -> Unit) {
        if (isAdded && context != null) {
            operation(requireContext())
        }
    }
}