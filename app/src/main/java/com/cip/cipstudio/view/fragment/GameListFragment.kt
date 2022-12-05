package com.cip.cipstudio.view.fragment


import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.cip.cipstudio.R
import com.cip.cipstudio.adapters.FavouriteGridViewAdapter
import com.cip.cipstudio.databinding.FragmentGameListBinding
import com.cip.cipstudio.utils.ActionGameDetailsEnum
import com.cip.cipstudio.utils.GameTypeEnum
import com.cip.cipstudio.viewmodel.GameListViewModel


class GameListFragment : Fragment() {

    private lateinit var gameListViewModel: GameListViewModel
    private lateinit var gameListBinding: FragmentGameListBinding

    private var offset : Int = 0

    private lateinit var gameType: GameTypeEnum

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        gameType = GameTypeEnum.valueOf(arguments?.getString("gameType")!!)
        gameListBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_game_list, container, false)
        gameListViewModel = GameListViewModel()
        gameListBinding.vm = gameListViewModel
        gameListBinding.title = getString(gameType.getName())
        gameListBinding.fGameListBtnFilter.setOnClickListener {
            gameListBinding.drawerLayout.openDrawer(GravityCompat.END)
        }

        initializeGames()

        gameListBinding.fGameListFlFilter.fFilterTvFilterByPlatform.setOnClickListener {
            if (gameListBinding.fGameListFlFilter.fFilterCgFilterByPlatform.visibility == View.VISIBLE) {
                gameListBinding.fGameListFlFilter.fFilterCgFilterByPlatform.visibility = View.GONE
                gameListBinding.fGameListFlFilter.fFilterTvFilterByPlatform.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down, 0)
            } else {
                gameListBinding.fGameListFlFilter.fFilterCgFilterByPlatform.visibility = View.VISIBLE
                gameListBinding.fGameListFlFilter.fFilterTvFilterByPlatform.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_up, 0)
            }
        }


        return gameListBinding.root
    }

    private fun initializeGames() {
        gameListViewModel.getGames(
            gameType){

            gameListBinding.fGameListBtnBack.backButton.setOnClickListener {
                findNavController().popBackStack()
            }

            val gvAdapter = FavouriteGridViewAdapter(requireContext(),
                it,
                gameListBinding.root.findNavController(),
                ActionGameDetailsEnum.GAME_LIST)
            gameListBinding.fGameListGvGames.adapter = gvAdapter

            gameListBinding.fGameListGvGames.setOnScrollListener(object : AbsListView.OnScrollListener {
                override fun onScroll(
                    view: AbsListView?,
                    firstVisibleItem: Int,
                    visibleItemCount: Int,
                    totalItemCount: Int
                ) {
                    if (firstVisibleItem + visibleItemCount >= totalItemCount - 2 && gameListViewModel.isPageLoading.value == false) {
                        offset++
                        gameListViewModel.getGames(gameType, offset){ games ->

                            (gameListBinding.fGameListGvGames.adapter as FavouriteGridViewAdapter)
                                .addMoreGames(games)
                        }


                    }
                }

                override fun onScrollStateChanged(view: AbsListView?, scrollState: Int) {}
            })

        }
    }


}