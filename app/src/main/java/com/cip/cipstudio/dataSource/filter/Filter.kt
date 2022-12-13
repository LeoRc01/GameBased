package com.cip.cipstudio.dataSource.filter

import android.graphics.Typeface
import android.os.Build
import android.view.View
import android.widget.TextView
import com.cip.cipstudio.R
import com.cip.cipstudio.dataSource.filter.criteria.*
import com.cip.cipstudio.databinding.ReusableFilterLayoutBinding
import com.cip.cipstudio.model.data.PlatformDetails
import com.cip.cipstudio.utils.GameTypeEnum
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
    var offsetPlatforms = 0
    private val tagContainer = "FilterContainer"
    private val tagOffsetPlatforms = "OffsetPlatforms"

    constructor(binding : ReusableFilterLayoutBinding,
                viewModel: ViewModelFilter,
                layoutInflater: android.view.LayoutInflater,
                resources: android.content.res.Resources,
                gameListType: GameTypeEnum) : this(binding, viewModel, layoutInflater, resources) {
        this.gameType = gameListType
    }

    fun initializeFilters(map: Map<String, Any>? = null) {
        var offset = 0
        if (map != null) {
            if (map.containsKey(tagContainer))
                filterContainer = map[tagContainer] as FilterContainer
            if (map.containsKey(tagOffsetPlatforms))
                offset = map[tagOffsetPlatforms] as Int
        }
        initializeCategory()
        initializePlatforms(offset)
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
                GameTypeEnum.FOR_YOU ->{
                    initializeRating()
                    initializeReleaseDate()
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
                textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down, 0)
                for (c in otherChildren) {
                    c.visibility = View.GONE
                }
            }
            else {
                child.visibility = View.VISIBLE
                textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_up, 0)
                for (c in otherChildren) {
                    c.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun initializeChipGroup(chipGroup: ChipGroup, list: List<Int>? = null, getFilter: ViewModelFilter.((List<*>) -> Unit) -> Unit) {
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
            if (list != null && list.isNotEmpty()) {
                list?.forEach {id ->
                    chipGroup.check(id)
                }
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

    private fun initializeCategory() {
        initializeChipGroup(binding.fFilterCgFilterByCategory, filterContainer.categoryList, ViewModelFilter::getCategory)
        initializeTextViewSetOnClick(binding.fFilterTvFilterByCategory, binding.fFilterCgFilterByCategory)
    }

    private fun initializeGenres(){
        initializeChipGroup(binding.fFilterCgFilterByGenres,filterContainer.genreList, ViewModelFilter::getGenres)
        initializeTextViewSetOnClick(binding.fFilterTvFilterByGenre, binding.fFilterCgFilterByGenres)
    }

    private fun initializePlayerPerspectives(){
        initializeChipGroup(binding.fFilterCgFilterByPlayerPerspectives, filterContainer.playerPerspectiveList, ViewModelFilter::getPlayerPerspectives)
        initializeTextViewSetOnClick(binding.fFilterTvFilterByPlayerPerspective, binding.fFilterCgFilterByPlayerPerspectives)
    }

    private fun initializeGameModes(){
        initializeChipGroup(binding.fFilterCgFilterByGameModes, filterContainer.gameModesList, ViewModelFilter::getGameModes)
        initializeTextViewSetOnClick(binding.fFilterTvFilterByGameMode, binding.fFilterCgFilterByGameModes)
    }

    private fun initializeThemes(){
        initializeChipGroup(binding.fFilterCgFilterByTheme, filterContainer.themeList, ViewModelFilter::getThemes)
        initializeTextViewSetOnClick(binding.fFilterTvFilterByTheme, binding.fFilterCgFilterByTheme)
    }

    private fun initializeReleaseDate() {
        initializeTextViewSetOnClick(binding.fFilterTvFilterByReleaseDate, binding.fFilterLlReleaseDate)
        viewModel.getYears {
            binding.fFilterSldFilterByReleaseDate.valueFrom = it.first()
            yearMin = it.first().toInt()
            binding.fFilterSldFilterByReleaseDate.valueTo = it.last()
            yearMax = it.last().toInt()
            val tempMin = if (filterContainer.releaseDateMin != null ) filterContainer.releaseDateMin else it.first()
            val tempMax = if (filterContainer.releaseDateMax != null ) filterContainer.releaseDateMax else it.last()
            binding.fFilterSldFilterByReleaseDate.values = listOf<Float>(tempMin!!, tempMax!!)
        }
    }

    private fun initializePlatforms(offset: Int = 0) {
        initializeChipGroup(binding.fFilterCgFilterByPlatform, null, ViewModelFilter::getPlatforms)
        initializeTextViewSetOnClick(binding.fFilterTvFilterByPlatform, binding.fFilterCgFilterByPlatform, binding.fFilterRlFilterByPlatform)
        initializeMorePlatforms(offset)

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

    private fun initializeMorePlatforms(offset: Int) {
        if (offset > 0) {
            viewModel.getPlatforms {
                it.forEach {
                    val chip = createChip(it.id, it.name, binding.fFilterCgFilterByPlatform)
                    binding.fFilterCgFilterByPlatform.addView(chip)
                }
                offsetPlatforms++
                if (offset > offsetPlatforms)
                    initializeMorePlatforms(offset)
                else {
                    if (filterContainer.platformList != null) {
                        filterContainer.platformList?.forEach { id ->
                            binding.fFilterCgFilterByPlatform.check(id)
                        }
                    }
                }
            }
        }
        else {
            if (filterContainer.platformList != null) {
                filterContainer.platformList?.forEach { id ->
                    binding.fFilterCgFilterByPlatform.check(id)
                }
            }
        }
    }

    private fun initializeRating() {
        initializeTextViewSetOnClick(binding.fFilterTvFilterByRating, binding.fFilterLlRating)
        if (filterContainer.userRating != null) {
            binding.fFilterSldFilterByUserRating.value = filterContainer.userRating!!
        }
        if (filterContainer.criticsRating != null) {
            binding.fFilterSldFilterByCriticsRating.value = filterContainer.criticsRating!!
        }
    }

    fun getMap() : Map<String, Any> {
        val map : HashMap<String, Any> = HashMap()
        map[tagContainer] = filterContainer
        map[tagOffsetPlatforms] = offsetPlatforms
        return map
    }




}