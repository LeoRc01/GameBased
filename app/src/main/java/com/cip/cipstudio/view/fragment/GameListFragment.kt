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

    private val filterCriteria = OperatorCriteria(OperatorEnum.AND)

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
        gameListBinding.fGameListBtnFilter.setOnClickListener {
            gameListBinding.drawerLayout.openDrawer(GravityCompat.END)
        }


        initializeGames()
        initializeDrawer()

        return gameListBinding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initializeDrawer() {
        gameListBinding.drawerLayout.addDrawerListener(object : androidx.drawerlayout.widget.DrawerLayout.DrawerListener {
            override fun onDrawerStateChanged(newState: Int) {
            }

            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
            }

            override fun onDrawerClosed(drawerView: View) {
                offset = 0
                filterCriteria.clearCriteria()

                val categoryListChecked = gameListBinding.fGameListFlFilter.fFilterCgFilterByCategory.checkedChipIds.map { it.toString() }
                val criteriaCategory : Criteria = FieldCriteria(FilterFieldEnum.CATEGORY, categoryListChecked)
                filterCriteria.addCriteria(criteriaCategory)

                val genreListChecked = gameListBinding.fGameListFlFilter.fFilterCgFilterByGenres.checkedChipIds.map { it.toString() }
                val criteriaGenre : Criteria = FieldCriteria(FilterFieldEnum.GENRES, genreListChecked)
                filterCriteria.addCriteria(criteriaGenre)


                val platformsListChecked = gameListBinding.fGameListFlFilter.fFilterCgFilterByPlatform.checkedChipIds.map { it.toString() }
                val criteriaPlatforms : Criteria = FieldCriteria(FilterFieldEnum.PLATFORMS, platformsListChecked)
                filterCriteria.addCriteria(criteriaPlatforms)

                val playerPerspectivesListChecked = gameListBinding.fGameListFlFilter.fFilterCgFilterByPlayerPerspectives.checkedChipIds.map { it.toString() }
                val criteriaPlayerPerspectives : Criteria = FieldCriteria(FilterFieldEnum.PLAYER_PERSPECTIVE, playerPerspectivesListChecked)
                filterCriteria.addCriteria(criteriaPlayerPerspectives)

                val gameModesListChecked = gameListBinding.fGameListFlFilter.fFilterCgFilterByGameModes.checkedChipIds.map { it.toString() }
                val criteriaGameModes : Criteria = FieldCriteria(FilterFieldEnum.GAME_MODE, gameModesListChecked)
                filterCriteria.addCriteria(criteriaGameModes)

                val themesListChecked = gameListBinding.fGameListFlFilter.fFilterCgFilterByTheme.checkedChipIds.map { it.toString() }
                val criteriaThemes : Criteria = FieldCriteria(FilterFieldEnum.THEMES, themesListChecked)
                filterCriteria.addCriteria(criteriaThemes)

                when(gameType){
                    GameTypeEnum.BEST_RATED, GameTypeEnum.WORST_RATED, GameTypeEnum.LOVED_BY_CRITICS, GameTypeEnum.MOST_RATED, GameTypeEnum.MOST_POPULAR -> {
                        val yearsSelected = gameListBinding.fGameListFlFilter.fFilterSldFilterByReleaseDate.values
                        val criteriaYears = RangeCriteria(FilterFieldEnum.RELEASE_DATE_YEAR)
                        if (yearsSelected.first().toInt() != yearMin){
                            criteriaYears.setMin(yearsSelected.first())
                        }
                        if (yearsSelected.last().toInt() != yearMax){
                            criteriaYears.setMax(yearsSelected.last())
                        }
                        filterCriteria.addCriteria(criteriaYears)
                    }
                    GameTypeEnum.UPCOMING, GameTypeEnum.RECENTLY_RELEASED, GameTypeEnum.MOST_HYPED -> {
                        val userRating = gameListBinding.fGameListFlFilter.fFilterSldFilterByUserRating.value
                        val criteriaUserRating = RangeCriteria(FilterFieldEnum.USER_RATING)
                        if (userRating.toInt() != 0){
                            criteriaUserRating.setMin(userRating)
                        }
                        filterCriteria.addCriteria(criteriaUserRating)

                        val criticsRating = gameListBinding.fGameListFlFilter.fFilterSldFilterByCriticsRating.value
                        val criteriaCriticsRating = RangeCriteria(FilterFieldEnum.CRITICS_RATING)
                        if (criticsRating.toInt() != 0){
                            criteriaCriticsRating.setMin(criticsRating)
                        }
                        filterCriteria.addCriteria(criteriaCriticsRating)

                    }
                }

                initializeGames()
            }

            override fun onDrawerOpened(drawerView: View) {

            }
        })

        initializeGenres()
        initializePlatforms()
        initializePlayerPerspectives()
        initializeGameModes()
        initializeThemes()
        initializeCategory()
        when(gameType){
            GameTypeEnum.BEST_RATED, GameTypeEnum.WORST_RATED, GameTypeEnum.LOVED_BY_CRITICS, GameTypeEnum.MOST_RATED, GameTypeEnum.MOST_POPULAR -> {
                gameListBinding.fGameListFlFilter.fFilterTvFilterByRating.visibility = View.GONE
                gameListBinding.fGameListFlFilter.fFilterLlRating.visibility = View.GONE
                gameListBinding.fGameListFlFilter.fFilterDividerReleaseDateRating.visibility = View.GONE
                initializeReleaseYear()
            }
            GameTypeEnum.UPCOMING, GameTypeEnum.RECENTLY_RELEASED, GameTypeEnum.MOST_HYPED -> {
                gameListBinding.fGameListFlFilter.fFilterTvFilterByReleaseDate.visibility = View.GONE
                gameListBinding.fGameListFlFilter.fFilterLlReleaseDate.visibility = View.GONE
                gameListBinding.fGameListFlFilter.fFilterDividerThemeReleaseDate.visibility = View.GONE
                initializeRating()
            }
        }
        gameListBinding.fGameListFlFilter.fFilterTvFilterByStatus.visibility = View.GONE
        gameListBinding.fGameListFlFilter.fFilterDividerStatusPlatform.visibility = View.GONE
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initializeCategory() {
        initializeFilter(gameListBinding.fGameListFlFilter.fFilterTvFilterByCategory,
            gameListBinding.fGameListFlFilter.fFilterCgFilterByCategory,
            GameListViewModel::getCategory)
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
        initializeFilter(gameListBinding.fGameListFlFilter.fFilterTvFilterByPlatform,
            gameListBinding.fGameListFlFilter.fFilterCgFilterByPlatform,
            GameListViewModel::getPlatforms)

        gameListBinding.fGameListFlFilter.fFilterTvFilterByPlatform.setOnClickListener {
            if(gameListBinding.fGameListFlFilter.fFilterCgFilterByPlatform.visibility == View.VISIBLE){
                gameListBinding.fGameListFlFilter.fFilterCgFilterByPlatform.visibility = View.GONE
                gameListBinding.fGameListFlFilter.fFilterRlFilterByPlatform.visibility = View.GONE
                gameListBinding.fGameListFlFilter.fFilterTvFilterByPlatform.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down, 0)
            }else{
                gameListBinding.fGameListFlFilter.fFilterCgFilterByPlatform.visibility = View.VISIBLE
                gameListBinding.fGameListFlFilter.fFilterRlFilterByPlatform.visibility = View.VISIBLE
                gameListBinding.fGameListFlFilter.fFilterTvFilterByPlatform.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_up, 0)
            }
        }

        gameListBinding.fGameListFlFilter.fFilterTvLoadMorePlatforms.setOnClickListener {
            gameListBinding.fGameListFlFilter.fFilterCpLoadingPlatformsIndicator.visibility = View.VISIBLE
            gameListBinding.fGameListFlFilter.fFilterTvLoadMorePlatforms.visibility = View.GONE
            gameListViewModel.getPlatforms { list ->
                list.forEach {
                    val chip = createChip(it.id, it.name, gameListBinding.fGameListFlFilter.fFilterCgFilterByPlatform)
                    gameListBinding.fGameListFlFilter.fFilterCgFilterByPlatform.addView(chip)
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
    private fun initializeGameModes(){
        initializeFilter(gameListBinding.fGameListFlFilter.fFilterTvFilterByGameMode,
            gameListBinding.fGameListFlFilter.fFilterCgFilterByGameModes,
            GameListViewModel::getGameModes)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initializeThemes(){
        initializeFilter(gameListBinding.fGameListFlFilter.fFilterTvFilterByTheme,
            gameListBinding.fGameListFlFilter.fFilterCgFilterByTheme,
            GameListViewModel::getThemes)
    }

    private fun initializeReleaseYear(){
        gameListBinding.fGameListFlFilter.fFilterTvFilterByReleaseDate.setOnClickListener {
            if(gameListBinding.fGameListFlFilter.fFilterLlReleaseDate.visibility == View.VISIBLE){
                gameListBinding.fGameListFlFilter.fFilterLlReleaseDate.visibility = View.GONE
                gameListBinding.fGameListFlFilter.fFilterTvFilterByReleaseDate.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down, 0)
            }else{
                gameListBinding.fGameListFlFilter.fFilterLlReleaseDate.visibility = View.VISIBLE
                gameListBinding.fGameListFlFilter.fFilterTvFilterByReleaseDate.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_up, 0)
            }
        }

        gameListViewModel.getYears {
            gameListBinding.fGameListFlFilter.fFilterSldFilterByReleaseDate.valueFrom = it.first()
            yearMin = it.first().toInt()
            gameListBinding.fGameListFlFilter.fFilterSldFilterByReleaseDate.valueTo = it.last()
            yearMax = it.last().toInt()
            gameListBinding.fGameListFlFilter.fFilterSldFilterByReleaseDate.values = listOf<Float>(it.first(), it.last())
        }
    }

    private fun initializeRating(){
        gameListBinding.fGameListFlFilter.fFilterTvFilterByRating.setOnClickListener {
            if(gameListBinding.fGameListFlFilter.fFilterLlRating.visibility == View.VISIBLE){
                gameListBinding.fGameListFlFilter.fFilterLlRating.visibility = View.GONE
                gameListBinding.fGameListFlFilter.fFilterTvFilterByRating.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down, 0)
            }else{
                gameListBinding.fGameListFlFilter.fFilterLlRating.visibility = View.VISIBLE
                gameListBinding.fGameListFlFilter.fFilterTvFilterByRating.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_up, 0)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChip(id : String, name : String, chipGroup: ChipGroup) : Chip {
        val chipButton = layoutInflater.inflate(R.layout.genre_chip_filter, chipGroup, false) as Chip
        chipButton.id = id.toInt()
        chipButton.text = name
        chipButton.isClickable = true
        chipButton.typeface = resources.getFont(R.font.montserrat_regular)
        return chipButton
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun initializeFilter(textView: TextView, chipGroup: ChipGroup, getFilter: GameListViewModel.((List<*>) -> Unit) -> Unit) {
        gameListViewModel.getFilter {
            chipGroup.removeAllViews()
            it.forEach { chipObject ->
                val id: String
                val name: String
                when (chipObject) {
                    is PlatformDetails -> {
                        id = chipObject.id
                        name = chipObject.name
                    }
                    is JSONObject -> {
                        id = chipObject.getString("id")
                        name = chipObject.getString("name")
                    }
                    else -> {
                        id = ""
                        name = ""
                    }
                }
                val chipButton = createChip(id, name, chipGroup)
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

    private fun checkIfFragmentAttached(operation: Context.() -> Unit) {
        if (isAdded && context != null) {
            operation(requireContext())
        }
    }
}