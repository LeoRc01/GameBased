package com.cip.cipstudio.view.fragment


import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.RadioButton
import androidx.annotation.RequiresApi
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
import com.cip.cipstudio.model.data.PlatformDetails
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
        gameListBinding.fGameListBtnFilter.setOnClickListener {
            gameListBinding.drawerLayout.openDrawer(GravityCompat.END)
        }

        initializeGames()
        initializeGenres()
        initializePlatforms()

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
            if (gameListBinding.fGameListFlFilter.fFilterPlatformsLinearLayout.visibility == View.VISIBLE) {
                gameListBinding.fGameListFlFilter.fFilterPlatformsLinearLayout.visibility = View.GONE
                gameListBinding.fGameListFlFilter.fFilterTvFilterByPlatform.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down, 0)
            } else {
                gameListBinding.fGameListFlFilter.fFilterPlatformsLinearLayout.visibility = View.VISIBLE
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initializeGenres(){
        gameListViewModel.getGenres {
            gameListBinding.fGameListFlFilter.fFilterCgFilterByGenres.removeAllViews()
            it.forEach { jsonObject ->
                val chipButton = _createChip(jsonObject.getString("id"), jsonObject.getString("name"))
                gameListBinding.fGameListFlFilter.fFilterCgFilterByGenres.addView(chipButton)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initializePlatforms(){
        val platforms = arrayListOf<PlatformDetails>()
        var offset = 1;

        platforms.add(PlatformDetails("6", "PC (Microsoft Windows)"))
        platforms.add(PlatformDetails("130", "Nintendo Switch"))
        platforms.add(PlatformDetails("48", "PlayStation 4"))
        platforms.add(PlatformDetails("167", "PlayStation 5"))
        platforms.add(PlatformDetails("169", "Xbox Series X|S"))
        platforms.add(PlatformDetails("49", "Xbox One"))
        platforms.add(PlatformDetails("9", "PlayStation 3"))
        platforms.add(PlatformDetails("14", "Mac"))
        platforms.add(PlatformDetails("3", "Linux"))
        platforms.add(PlatformDetails("12", "Xbox 360"))
        platforms.add(PlatformDetails("20", "Nintendo DS"))

        gameListBinding.fGameListFlFilter.fFilterCgFilterByPlatform.removeAllViews()
        platforms.forEach { item ->
            val chipButton = _createChip(item.id, item.name)
            gameListBinding.fGameListFlFilter.fFilterCgFilterByPlatform.addView(chipButton)
        }

        gameListBinding.fGameListFlFilter.fFilterTvLoadMorePlatforms.setOnClickListener {
            gameListBinding.fGameListFlFilter.fFilterCpLoadingPlatformsIndicator.visibility = View.VISIBLE
            gameListBinding.fGameListFlFilter.fFilterTvLoadMorePlatforms.visibility = View.GONE
            gameListViewModel.getMorePlatforms(offset){ list ->
                list.forEach {
                    if(!platforms.contains(it)){
                        val chipButton = _createChip(it.id, it.name)
                        gameListBinding.fGameListFlFilter.fFilterCgFilterByPlatform.addView(chipButton)
                    }
                }
                offset++
                gameListBinding.fGameListFlFilter.fFilterCpLoadingPlatformsIndicator.visibility = View.GONE
                gameListBinding.fGameListFlFilter.fFilterTvLoadMorePlatforms.visibility = View.VISIBLE
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun _createChip(id : String, name : String) : Chip{
        val chipButton = Chip(requireContext(), null, R.layout.genre_chip_filter)
        chipButton.id = id.toInt()
        chipButton.text = name
        chipButton.isClickable = true
        val chipDrawable = ChipDrawable.createFromAttributes(
            requireContext(),
            null,
            0,
            com.google.android.material.R.style.Widget_Material3_Chip_Filter
        )
        chipButton.typeface = resources.getFont(R.font.montserrat_regular)
        chipButton.setChipDrawable(chipDrawable)
        return chipButton
    }


}