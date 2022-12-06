package com.cip.cipstudio.view.fragment


import android.content.res.ColorStateList
import android.graphics.Color
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
import com.cip.cipstudio.dataSource.filter.criteria.*
import com.cip.cipstudio.databinding.FragmentGameListBinding
import com.cip.cipstudio.model.data.PlatformDetails
import com.cip.cipstudio.utils.ActionGameDetailsEnum
import com.cip.cipstudio.utils.GameTypeEnum
import com.cip.cipstudio.viewmodel.GameListViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.google.android.material.chip.ChipGroup
import org.json.JSONObject
import java.util.zip.Inflater


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
        gameListBinding.lifecycleOwner = this
        gameListBinding.fGameListBtnFilter.setOnClickListener {
            gameListBinding.drawerLayout.openDrawer(GravityCompat.END)
        }

        initializeGames()
        initializeGenres()
        initializePlatforms()
        initializePlayerPerspectives()

        gameListBinding.drawerLayout.addDrawerListener(object : androidx.drawerlayout.widget.DrawerLayout.DrawerListener {
            override fun onDrawerStateChanged(newState: Int) {
            }

            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
            }

            override fun onDrawerClosed(drawerView: View) {
                filterCriteria.clearCriteria()

                val genreListChecked = gameListBinding.fGameListFlFilter.fFilterCgFilterByGenres.checkedChipIds.map { it.toString() }
                val criteriaGenre : Criteria = FieldCriteria(FilterField.GENRES, genreListChecked)
                filterCriteria.addCriteria(criteriaGenre)


                val platformsListChecked = gameListBinding.fGameListFlFilter.fFilterCgFilterByPlatform.checkedChipIds.map { it.toString() }
                val criteriaPlatforms : Criteria = FieldCriteria(FilterField.PLATFORMS, platformsListChecked)
                filterCriteria.addCriteria(criteriaPlatforms)

                val playerPerspectivesListChecked = gameListBinding.fGameListFlFilter.fFilterCgFilterByPlayerPerspectives.checkedChipIds.map { it.toString() }
                val criteriaPlayerPerspectives : Criteria = FieldCriteria(FilterField.PLAYER_PERSPECTIVE, playerPerspectivesListChecked)
                filterCriteria.addCriteria(criteriaPlayerPerspectives)

                initializeGames()
            }

            override fun onDrawerOpened(drawerView: View) {
            }
        })

        gameListBinding.fGameListFlFilter
            .fFilterTvFilterByPlatform.setOnClickListener {
            if (gameListBinding.fGameListFlFilter.fFilterPlatformsLinearLayout.visibility == View.VISIBLE) {
                gameListBinding.fGameListFlFilter.fFilterPlatformsLinearLayout.visibility = View.GONE
                gameListBinding.fGameListFlFilter.fFilterTvFilterByPlatform.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down, 0)
            } else {
                gameListBinding.fGameListFlFilter.fFilterPlatformsLinearLayout.visibility = View.VISIBLE
                gameListBinding.fGameListFlFilter.fFilterTvFilterByPlatform.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_up, 0)
            }
        }


        return gameListBinding.root
    }

    private fun initializeGames() {
        gameListBinding.fGameListLoading.visibility = View.VISIBLE
        gameListBinding.fGameListGvGames.visibility = View.GONE
        gameListBinding.fGameListLoadingNoGamesFound.root.visibility = View.GONE
        gameListViewModel.getGames(gameType, filterCriteria){
            gameListBinding.fGameListLoading.visibility = View.GONE
            gameListBinding.fGameListGvGames.visibility = View.VISIBLE
            gameListBinding.fGameListBtnBack.backButton.setOnClickListener {
                findNavController().popBackStack()
            }

            if(it.isEmpty()){
                gameListBinding.fGameListLoadingNoGamesFound.root.visibility = View.VISIBLE
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


        platforms.forEach { item ->
            val chipButton = createChip(item.id, item.name, gameListBinding.fGameListFlFilter.fFilterCgFilterByPlatform)
            gameListBinding.fGameListFlFilter.fFilterCgFilterByPlatform.addView(chipButton)
        }

        gameListBinding.fGameListFlFilter.fFilterTvLoadMorePlatforms.setOnClickListener {
            gameListBinding.fGameListFlFilter.fFilterCpLoadingPlatformsIndicator.visibility = View.VISIBLE
            gameListBinding.fGameListFlFilter.fFilterTvLoadMorePlatforms.visibility = View.GONE
            gameListViewModel.getMorePlatforms(offset){ list ->
                list.forEach {
                    if(!platforms.contains(it)){
                        val chipButton = createChip(it.id, it.name, gameListBinding.fGameListFlFilter.fFilterCgFilterByPlatform)
                        platforms.add(it)
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
    private fun initializeGenres(){
        initializeFilter(gameListBinding.fGameListFlFilter.fFilterTvFilterByGenre,
            gameListBinding.fGameListFlFilter.fFilterCgFilterByGenres,
            GameListViewModel::getGenres)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initializePlayerPerspectives(){
        initializeFilter(gameListBinding.fGameListFlFilter.fFilterTvFilterByPlayerPerspective,
            gameListBinding.fGameListFlFilter.fFilterCgFilterByPlayerPerspectives,
            GameListViewModel::getPlayerPerspectives)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChip(id : String, name : String, chipGroup: ChipGroup) : Chip {
        val chipButton = layoutInflater.inflate(R.layout.genre_chip_filter, chipGroup, false) as Chip
        chipButton.id = id.toInt()
        chipButton.text = name
        chipButton.isClickable = true

        return chipButton
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun initializeFilter(textView: TextView, chipGroup: ChipGroup, getFilter: GameListViewModel.((ArrayList<JSONObject>) -> Unit) -> Unit) {
        gameListViewModel.getFilter {
            chipGroup.removeAllViews()
            it.forEach { jsonObject ->
                val chipButton = createChip(jsonObject.getString("id"), jsonObject.getString("name"), chipGroup)
                chipGroup.addView(chipButton)
            }
            textView.setOnClickListener {
                if (chipGroup.visibility == View.GONE) {
                    chipGroup.visibility = View.VISIBLE
                    textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_up, 0)
                } else {
                    chipGroup.visibility = View.GONE
                    textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down, 0)
                }
            }

        }
    }


}