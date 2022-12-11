package com.cip.cipstudio.dataSource.filter

import android.graphics.Typeface
import android.os.Build
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import com.cip.cipstudio.R
import com.cip.cipstudio.dataSource.filter.criteria.*
import com.cip.cipstudio.databinding.ReusableFilterLayoutBinding
import com.cip.cipstudio.model.data.PlatformDetails
import com.cip.cipstudio.utils.GameTypeEnum
import com.cip.cipstudio.viewmodel.GameListViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import org.json.JSONObject

class Filter(private val binding : ReusableFilterLayoutBinding,
             private val viewModel: ViewModelFilter,
             private val layoutInflater: android.view.LayoutInflater,
             private val resources: android.content.res.Resources) {

    private var gameType: GameTypeEnum? = null
    private var filterContainer = FilterContainer()

    private var yearMin: Int = 1950
    private var yearMax: Int = 2021
    private var offsetPlatforms = -1

    constructor(binding : ReusableFilterLayoutBinding,
                viewModel: ViewModelFilter,
                layoutInflater: android.view.LayoutInflater,
                resources: android.content.res.Resources,
                gameListType: GameTypeEnum) : this(binding, viewModel, layoutInflater, resources) {
        this.gameType = gameListType
    }

    fun initializeFilters(filterContainerInitializer: FilterContainer? = null) {
        initializeCategory()
        initializePlatforms()
        initializeGenres()
        initializeThemes()
        initializeGameModes()
        initializePlayerPerspectives()
        if (gameType != null){
            when (gameType) {
                GameTypeEnum.BEST_RATED, GameTypeEnum.WORST_RATED, GameTypeEnum.LOVED_BY_CRITICS, GameTypeEnum.MOST_RATED, GameTypeEnum.MOST_POPULAR -> {
                    binding.fFilterTvFilterByRating.visibility = View.GONE
                    binding.fFilterLlRating.visibility = View.GONE
                    binding.fFilterDividerReleaseDateRating.visibility = View.GONE
                    initializeReleaseDate()
                }
                GameTypeEnum.UPCOMING, GameTypeEnum.RECENTLY_RELEASED, GameTypeEnum.MOST_HYPED -> {
                    binding.fFilterTvFilterByReleaseDate.visibility = View.GONE
                    binding.fFilterLlReleaseDate.visibility = View.GONE
                    binding.fFilterDividerThemeReleaseDate.visibility = View.GONE
                    initializeRating()
                }
                else -> {}
            }
            binding.fFilterTvFilterByStatus.visibility = View.GONE
            binding.fFilterDividerStatusPlatform.visibility = View.GONE
            binding.fFilterTvSortBy.visibility = View.GONE
            binding.fFilterDividerSortCategory.visibility = View.GONE
            binding.fFilterActvChangeSort.visibility = View.GONE
        }
        else {
            initializeReleaseDate()
            initializeRating()
        }
    }

    private fun createChip(id : String, name : String, chipGroup: ChipGroup) : Chip {
        val chipButton = layoutInflater.inflate(R.layout.genre_chip_filter, chipGroup, false) as Chip
        chipButton.id = id.toInt()
        chipButton.text = name
        chipButton.isClickable = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            chipButton.typeface = resources.getFont(R.font.montserrat_regular)
        }
        else {
            chipButton.typeface = Typeface.createFromAsset(resources.assets, "fonts/montserrat_regular.ttf")
        }
        return chipButton
    }

    private fun initializeTextViewSetOnClick(textView: TextView, child:View, vararg otherChildren: View) {
        textView.setOnClickListener {
            if (child.visibility == View.VISIBLE) {
                child.visibility = View.GONE
                textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_up, 0)
                for (c in otherChildren) {
                    c.visibility = View.GONE
                }
            }
            else {
                child.visibility = View.VISIBLE
                textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down, 0)
                for (c in otherChildren) {
                    c.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun initializeChipGroup(chipGroup: ChipGroup, getFilter: ViewModelFilter.((List<*>) -> Unit) -> Unit) {
        viewModel.getFilter {
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
        }
    }

    fun getFilterCriteria() : Criteria {
        return filterContainer.getFilterCriteria()
    }

    fun buildFilterContainer() {
        if (binding.fFilterActvChangeSort.visibility == View.VISIBLE) {
            filterContainer.sorting = binding.fFilterActvChangeSort.editText?.text
        }
        if (binding.fFilterTvFilterByCategory.visibility == View.VISIBLE) {
            filterContainer.categoryList = binding.fFilterCgFilterByCategory.checkedChipIds
        }
        if (binding.fFilterTvFilterByStatus.visibility == View.VISIBLE) {
            filterContainer.statusList = binding.fFilterCgFilterByStatus.checkedChipIds
        }
        if (binding.fFilterTvFilterByPlatform.visibility == View.VISIBLE) {
            filterContainer.platformList = binding.fFilterCgFilterByPlatform.checkedChipIds
        }
        if (binding.fFilterTvFilterByGenre.visibility == View.VISIBLE) {
            filterContainer.genreList = binding.fFilterCgFilterByGenres.checkedChipIds
        }
        if (binding.fFilterTvFilterByPlayerPerspective.visibility == View.VISIBLE) {
            filterContainer.playerPerspectiveList = binding.fFilterCgFilterByPlayerPerspectives.checkedChipIds
        }
        if (binding.fFilterTvFilterByGameMode.visibility == View.VISIBLE) {
            filterContainer.gameModesList = binding.fFilterCgFilterByGameModes.checkedChipIds
        }
        if (binding.fFilterTvFilterByTheme.visibility == View.VISIBLE) {
            filterContainer.themeList = binding.fFilterCgFilterByTheme.checkedChipIds
        }

        if (binding.fFilterTvFilterByReleaseDate.visibility == View.VISIBLE) {
            val min = binding.fFilterSldFilterByReleaseDate.values[0]
            val max = binding.fFilterSldFilterByReleaseDate.values[1]
            if (min.toInt() != yearMin) {
                filterContainer.releaseDateMin = min
            }
            if (max.toInt() != yearMax) {
                filterContainer.releaseDateMax = max
            }
        }
        if (binding.fFilterTvFilterByRating.visibility == View.VISIBLE) {
            val tempUserRating = binding.fFilterSldFilterByUserRating.value
            if (tempUserRating != 0f) {
                filterContainer.userRating = tempUserRating
            }

            val tempCriticsRating = binding.fFilterSldFilterByCriticsRating.value
            if (tempCriticsRating != 0f) {
                filterContainer.criticsRating = tempCriticsRating
            }
        }
    }

    fun initializeFromFilterContainer(filterContainerInitializer: FilterContainer? = null) {
        if (filterContainerInitializer == null) {
            filterContainer = FilterContainer()
        }
        else {
            filterContainer = filterContainerInitializer
            if (filterContainer.sorting != null) {
                binding.fFilterActvChangeSort.editText?.setText(filterContainer.sorting)
            }
            if (filterContainer.categoryList != null) {
                filterContainer.categoryList?.forEach {
                    binding.fFilterCgFilterByCategory.check(it)
                }
            }
            if (filterContainer.statusList != null) {
                filterContainer.statusList?.forEach {
                    binding.fFilterCgFilterByStatus.check(it)
                }
            }
            if (filterContainer.platformList != null) {
                filterContainer.platformList?.forEach {
                    binding.fFilterCgFilterByPlatform.check(it)
                }
            }
            if (filterContainer.genreList != null) {
                filterContainer.genreList?.forEach {
                    binding.fFilterCgFilterByGenres.check(it)
                }
            }
            if (filterContainer.playerPerspectiveList != null) {
                filterContainer.playerPerspectiveList?.forEach {
                    binding.fFilterCgFilterByPlayerPerspectives.check(it)
                }
            }
            if (filterContainer.gameModesList != null) {
                filterContainer.gameModesList?.forEach {
                    binding.fFilterCgFilterByGameModes.check(it)
                }
            }
            if (filterContainer.themeList != null) {
                filterContainer.themeList?.forEach {
                    binding.fFilterCgFilterByTheme.check(it)
                }
            }
            if (filterContainer.releaseDateMin != null) {
                binding.fFilterSldFilterByReleaseDate.values[0] = filterContainer.releaseDateMin!!
            }
            if (filterContainer.releaseDateMax != null) {
                binding.fFilterSldFilterByReleaseDate.values[1] = filterContainer.releaseDateMax!!
            }
            if (filterContainer.userRating != null) {
                binding.fFilterSldFilterByUserRating.value = filterContainer.userRating!!
            }
            if (filterContainer.criticsRating != null) {
                binding.fFilterSldFilterByCriticsRating.value = filterContainer.criticsRating!!
            }
        }
    }

    private fun initializeCategory() {
        initializeChipGroup(binding.fFilterCgFilterByCategory, ViewModelFilter::getCategory)
        initializeTextViewSetOnClick(binding.fFilterTvFilterByCategory, binding.fFilterCgFilterByCategory)
    }

    private fun initializeGenres(){
        initializeChipGroup(binding.fFilterCgFilterByGenres, ViewModelFilter::getGenres)
        initializeTextViewSetOnClick(binding.fFilterTvFilterByGenre, binding.fFilterCgFilterByGenres)
    }

    private fun initializePlayerPerspectives(){
        initializeChipGroup(binding.fFilterCgFilterByPlayerPerspectives, ViewModelFilter::getPlayerPerspectives)
        initializeTextViewSetOnClick(binding.fFilterTvFilterByPlayerPerspective, binding.fFilterCgFilterByPlayerPerspectives)
    }

    private fun initializeGameModes(){
        initializeChipGroup(binding.fFilterCgFilterByGameModes, ViewModelFilter::getGameModes)
        initializeTextViewSetOnClick(binding.fFilterTvFilterByGameMode, binding.fFilterCgFilterByGameModes)
    }

    private fun initializeThemes(){
        initializeChipGroup(binding.fFilterCgFilterByTheme, ViewModelFilter::getThemes)
        initializeTextViewSetOnClick(binding.fFilterTvFilterByTheme, binding.fFilterCgFilterByTheme)
    }

    private fun initializeReleaseDate() {
        initializeTextViewSetOnClick(binding.fFilterTvFilterByReleaseDate, binding.fFilterLlReleaseDate)
        viewModel.getYears {
            binding.fFilterSldFilterByReleaseDate.valueFrom = it.first()
            yearMin = it.first().toInt()
            binding.fFilterSldFilterByReleaseDate.valueTo = it.last()
            yearMax = it.last().toInt()
            binding.fFilterSldFilterByReleaseDate.values = listOf<Float>(it.first(), it.last())
        }
    }

    private fun initializePlatforms() {
        initializeChipGroup(binding.fFilterCgFilterByPlatform, ViewModelFilter::getPlatforms)
        initializeTextViewSetOnClick(binding.fFilterTvFilterByPlatform, binding.fFilterCgFilterByPlatform, binding.fFilterRlFilterByPlatform)

        binding.fFilterTvLoadMorePlatforms.setOnClickListener {
            binding.fFilterCpLoadingPlatformsIndicator.visibility = View.VISIBLE
            binding.fFilterTvLoadMorePlatforms.visibility = View.GONE
            viewModel.getPlatforms { list ->
                list.forEach {
                    val chip = createChip(it.id, it.name, binding.fFilterCgFilterByPlatform)
                    binding.fFilterCgFilterByPlatform.addView(chip)
                }
                offsetPlatforms++
                binding.fFilterCpLoadingPlatformsIndicator.visibility = View.GONE
                binding.fFilterTvLoadMorePlatforms.visibility = View.VISIBLE
            }

        }
    }

    private fun initializeRating() {
        initializeTextViewSetOnClick(binding.fFilterTvFilterByRating, binding.fFilterLlRating)
    }

    fun getContainer() : java.io.Serializable {
        return filterContainer
    }




}