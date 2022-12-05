package com.cip.cipstudio.view.fragment


import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.RadioButton
import androidx.core.view.GravityCompat
import androidx.core.view.marginTop
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.cip.cipstudio.R
import com.cip.cipstudio.adapters.FavouriteGridViewAdapter
import com.cip.cipstudio.dataSource.filter.criteria.*
import com.cip.cipstudio.databinding.FragmentGameListBinding
import com.cip.cipstudio.utils.ActionGameDetailsEnum
import com.cip.cipstudio.utils.GameTypeEnum
import com.cip.cipstudio.viewmodel.GameListViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable


class GameListFragment : Fragment() {

    private lateinit var gameListViewModel: GameListViewModel
    private lateinit var gameListBinding: FragmentGameListBinding
    private val TAG = "GameListFragment"

    private val filterCriteria = OperatorCriteria(Operator.AND)

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
        initializeGenres()

        gameListBinding.drawerLayout.addDrawerListener(object : androidx.drawerlayout.widget.DrawerLayout.DrawerListener {
            override fun onDrawerStateChanged(newState: Int) {
            }

            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
            }

            override fun onDrawerClosed(drawerView: View) {
                filterCriteria.clearCriteria()
                val listChecked = gameListBinding.fGameListFlFilter.fFilterCgFilterByGenres.checkedChipIds.map { it.toString() }
                val criteriaGenre : Criteria = FieldCriteria(FilterField.GENRES, listChecked)
                filterCriteria.addCriteria(criteriaGenre)
                initializeGames()
            }

            override fun onDrawerOpened(drawerView: View) {
            }
        })

        gameListBinding.fGameListFlFilter.fFilterTvFilterByPlatform.setOnClickListener {
            if (gameListBinding.fGameListFlFilter.fFilterCgFilterByPlatform.visibility == View.VISIBLE) {
                gameListBinding.fGameListFlFilter.fFilterCgFilterByPlatform.visibility = View.GONE
                gameListBinding.fGameListFlFilter.fFilterTvFilterByPlatform.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down, 0)
            } else {
                gameListBinding.fGameListFlFilter.fFilterCgFilterByPlatform.visibility = View.VISIBLE
                gameListBinding.fGameListFlFilter.fFilterTvFilterByPlatform.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_up, 0)
            }
        }

        gameListBinding.fGameListFlFilter.fFilterTvFilterByGenre.setOnClickListener {
            if (gameListBinding.fGameListFlFilter.fFilterCgFilterByGenres.visibility == View.VISIBLE) {
                gameListBinding.fGameListFlFilter.fFilterCgFilterByGenres.visibility = View.GONE
                gameListBinding.fGameListFlFilter.fFilterTvFilterByGenre.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down, 0)
            } else {
                gameListBinding.fGameListFlFilter.fFilterCgFilterByGenres.visibility = View.VISIBLE
                gameListBinding.fGameListFlFilter.fFilterTvFilterByGenre.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_up, 0)
            }
        }


        return gameListBinding.root
    }

    private fun initializeGames() {
        gameListViewModel.getGames(gameType, filterCriteria){

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
                        gameListViewModel.getGames(gameType, filterCriteria, offset){ games ->

                            (gameListBinding.fGameListGvGames.adapter as FavouriteGridViewAdapter)
                                .addMoreGames(games)
                        }


                    }
                }

                override fun onScrollStateChanged(view: AbsListView?, scrollState: Int) {}
            })

        }
    }

    private fun initializeGenres(){
        gameListViewModel.getGenres {
            gameListBinding.fGameListFlFilter.fFilterCgFilterByGenres.removeAllViews()
            it.forEach { jsonObject ->
                val chipButton = Chip(requireContext(), null, R.layout.genre_chip_filter)
                chipButton.id = jsonObject.getInt("id")
                chipButton.text = jsonObject.getString("name")
                chipButton.isClickable = true
                val chipDrawable = ChipDrawable.createFromAttributes(
                    requireContext(),
                    null,
                    0,
                    com.google.android.material.R.style.Widget_Material3_Chip_Filter
                )
                chipButton.setChipDrawable(chipDrawable)
                gameListBinding.fGameListFlFilter.fFilterCgFilterByGenres.addView(chipButton)
            }
        }
    }


}