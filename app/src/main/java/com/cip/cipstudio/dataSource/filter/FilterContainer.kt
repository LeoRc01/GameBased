package com.cip.cipstudio.dataSource.filter

import com.cip.cipstudio.dataSource.filter.criteria.*
import java.io.Serializable

class FilterContainer: Serializable {
    var sorting: CharSequence? = null
    var categoryList: List<Int>? = null
    var statusList: List<Int>? = null
    var platformList: List<Int>? = null
    var genreList: List<Int>? = null
    var playerPerspectiveList: List<Int>? = null
    var gameModesList: List<Int>? = null
    var themeList: List<Int>? = null
    var releaseDateMin: Float? = null
    var releaseDateMax: Float? = null
    var userRating: Float? = null
    var criticsRating: Float? = null

    fun getFilterCriteria() : Criteria {
        val criteria = OperatorCriteria(OperatorEnum.AND)
        if (categoryList != null && categoryList!!.isNotEmpty())
            criteria.addCriteria(FieldCriteria(FilterFieldEnum.CATEGORY, categoryList!!.map { it.toString() }))
        if (statusList != null && statusList!!.isNotEmpty())
            criteria.addCriteria(FieldCriteria(FilterFieldEnum.STATUS, statusList!!.map { it.toString() }))
        if (platformList != null && platformList!!.isNotEmpty())
            criteria.addCriteria(FieldCriteria(FilterFieldEnum.PLATFORMS, platformList!!.map { it.toString() }))
        if (genreList != null && genreList!!.isNotEmpty())
            criteria.addCriteria(FieldCriteria(FilterFieldEnum.GENRES, genreList!!.map { it.toString() }))
        if (playerPerspectiveList != null && playerPerspectiveList!!.isNotEmpty())
            criteria.addCriteria(FieldCriteria(FilterFieldEnum.PLAYER_PERSPECTIVE, playerPerspectiveList!!.map { it.toString() }))
        if (gameModesList != null && gameModesList!!.isNotEmpty())
            criteria.addCriteria(FieldCriteria(FilterFieldEnum.GAME_MODE, gameModesList!!.map { it.toString() }))
        if (themeList != null && themeList!!.isNotEmpty())
            criteria.addCriteria(FieldCriteria(FilterFieldEnum.THEMES, themeList!!.map { it.toString() }))
        val criteriaDataRelease = RangeCriteria(FilterFieldEnum.RELEASE_DATE_YEAR)
        if (releaseDateMin != null)
            criteriaDataRelease.setMin(releaseDateMin!!)
        if (releaseDateMax != null)
            criteriaDataRelease.setMax(releaseDateMax!!)
        criteria.addCriteria(criteriaDataRelease)
        if (userRating != null) {
            val criteriaUserRating = RangeCriteria(FilterFieldEnum.USER_RATING)
            criteriaUserRating.setMin(userRating!!)
            criteria.addCriteria(criteriaUserRating)
        }
        if (criticsRating != null) {
            val criteriaCriticsRating = RangeCriteria(FilterFieldEnum.CRITICS_RATING)
            criteriaCriticsRating.setMin(criticsRating!!)
            criteria.addCriteria(criteriaCriticsRating)
        }
        return criteria
    }

    fun getSortCriteria() : SortCriteria {
        val sort = when (sorting.toString()) {
                     SortEnum.AlphabeticalAZ.nameValue() -> SortEnum.AlphabeticalAZ
                     SortEnum.AlphabeticalZA.nameValue() -> SortEnum.AlphabeticalZA
                        SortEnum.MostPopular.nameValue() -> SortEnum.MostPopular
                        SortEnum.LeastPopular.nameValue() -> SortEnum.LeastPopular
                        SortEnum.MostRecent.nameValue() -> SortEnum.MostRecent
                        SortEnum.LeastRecent.nameValue() -> SortEnum.LeastRecent
                        SortEnum.TopRated.nameValue() -> SortEnum.TopRated
                        SortEnum.WorstRated.nameValue() -> SortEnum.WorstRated
                        else -> SortEnum.Default
                 }
        return SortCriteria(sort)
    }

    fun isEmpty() : Boolean {
        return getFilterCriteria().isEmpty() && getSortCriteria().isEmpty()
    }
}