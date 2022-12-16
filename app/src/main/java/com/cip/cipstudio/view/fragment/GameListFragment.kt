package com.cip.cipstudio.view.fragment


import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.cip.cipstudio.R
import com.cip.cipstudio.utils.StateInstanceSaver
import com.cip.cipstudio.adapters.FavouriteGridViewAdapter
import com.cip.cipstudio.dataSource.filter.Filter
import com.cip.cipstudio.databinding.FragmentGameListBinding
import com.cip.cipstudio.utils.ActionGameDetailsEnum
import com.cip.cipstudio.utils.GameTypeEnum
import com.cip.cipstudio.viewmodel.GameListViewModel


class GameListFragment : Fragment() {

    private lateinit var gameListViewModel: GameListViewModel
    private lateinit var gameListBinding: FragmentGameListBinding
    private val TAG = "GameListFragment"

    private lateinit var filter: Filter

    private var offset : Int = 0
    private val tagOffset = "offset"
    private val tagPosition = "position"

    private lateinit var gameType: GameTypeEnum

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        gameType = GameTypeEnum.valueOf(arguments?.getString("gameType")!!)
        gameListBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_game_list, container, false)
        gameListViewModel = GameListViewModel()
        gameListBinding.vm = gameListViewModel
        gameListBinding.title = getString(gameType.getName())
        gameListBinding.lifecycleOwner = this

        filter = Filter(gameListBinding.fGameListFlFilter,
                        gameListViewModel.viewModelScope,
                        gameListViewModel.isPageLoading,
                        layoutInflater,
                        resources,
                        gameType,
                        gameListBinding.drawerLayout)

        return gameListBinding.root
    }



    override fun onResume() {
        super.onResume()
        offset = 0
        val mapInstanceStateSaved = StateInstanceSaver.restoreState(TAG)
        filter.initializeFilters(mapInstanceStateSaved)
        val offsetStart = if (mapInstanceStateSaved != null && mapInstanceStateSaved.containsKey(tagOffset))
                mapInstanceStateSaved[tagOffset] as Int
            else
                0
        val positionStart = if (mapInstanceStateSaved != null && mapInstanceStateSaved.containsKey(tagPosition))
                mapInstanceStateSaved[tagPosition] as Int
            else
                0


        initializeDrawer()
        initializeGames(offsetStart, positionStart)

        gameListBinding.fGameListBtnFilter.setOnClickListener {
            gameListBinding.drawerLayout.openDrawer(GravityCompat.END)
        }
    }

    override fun onPause() {
        super.onPause()
        val mapInstanceStateToSave = filter.getMap()
        (mapInstanceStateToSave as HashMap<String, Any>)[tagOffset] = offset
        (mapInstanceStateToSave as HashMap<String, Any>)[tagPosition] = gameListBinding.fGameListGvGames.lastVisiblePosition
        StateInstanceSaver.saveState(TAG, mapInstanceStateToSave)

    }

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


    private fun initializeGames(startOffset: Int = 0, startPosition: Int = 0) {
        gameListBinding.fGameListLoading.visibility = View.VISIBLE
        gameListBinding.fGameListGvGames.visibility = View.GONE
        gameListBinding.fGameListLoadingNoGamesFound.root.visibility = View.GONE
        gameListBinding.fGameListBtnBack.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        gameListViewModel.getGames(gameType, filter.getFilterCriteria()){
            gameListBinding.fGameListLoading.visibility = View.GONE
            gameListBinding.fGameListGvGames.visibility = View.VISIBLE


            if(it.isEmpty()){
                gameListBinding.fGameListLoadingNoGamesFound.root.visibility = View.VISIBLE
            }
            checkIfFragmentAttached {
                val gvAdapter = FavouriteGridViewAdapter(
                    requireContext(),
                    it,
                    gameListBinding.root.findNavController(),
                    ActionGameDetailsEnum.GAME_LIST,
                    saveToHistory = true
                )
                gameListBinding.fGameListGvGames.adapter = gvAdapter
            }
            if (gameType != GameTypeEnum.FOR_YOU) {
                if (startOffset != 0 ) {
                    addStartGames(startOffset, startPosition)
                }
                else {
                    gameListBinding.fGameListGvGames.smoothScrollToPosition(startPosition)
                }
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

    private fun addStartGames(startOffset: Int, startPosition: Int) {
        if (startOffset >= offset) {
            offset++
            gameListViewModel.getGames(gameType, filter.getFilterCriteria(), offset){ games ->
                (gameListBinding.fGameListGvGames.adapter as FavouriteGridViewAdapter)
                    .addMoreGames(games)
                addStartGames(startOffset, startPosition)
            }
        }
        else {
            gameListBinding.fGameListGvGames.smoothScrollToPosition(startPosition)
        }
    }

    private fun checkIfFragmentAttached(operation: Context.() -> Unit) {
        if (isAdded && context != null) {
            operation(requireContext())
        }
    }
}