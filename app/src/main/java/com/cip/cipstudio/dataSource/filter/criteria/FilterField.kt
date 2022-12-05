package com.cip.cipstudio.dataSource.filter.criteria

enum class FilterField {
    CATEGORY {
        override fun getFilterFieldIGDBName(): String = "category"
        override fun getFieldControl(): String = "category != null"
    },
    STATUS{
        override fun getFilterFieldIGDBName(): String = "status"
        override fun getFieldControl(): String = "status != null"
    },
    RELEASE_DATE_YEAR{
        override fun getFilterFieldIGDBName(): String = "release_dates.y"
        override fun getFieldControl(): String = "release_dates != null & release_dates.y != null"
    },
    GENRES{
        override fun getFilterFieldIGDBName(): String = "genres"
        override fun getFieldControl(): String = "genres != null"
    },
    PLATFORMS{
        override fun getFilterFieldIGDBName(): String = "platforms"
        override fun getFieldControl(): String = "platforms != null"
    },
    RATING{
        override fun getFilterFieldIGDBName(): String = "rating"
        override fun getFieldControl(): String = "rating != null & rating > 0"
    },
    RATING_COUNT{
        override fun getFilterFieldIGDBName(): String = "rating_count"
        override fun getFieldControl(): String = "rating_count != null & rating_count > 0"
    },
    CRITICS_RATING{
        override fun getFilterFieldIGDBName(): String = "aggregated_rating"
        override fun getFieldControl(): String = "aggregated_rating != null & aggregated_rating > 0"
    },
    CRITICS_RATING_COUNT{
        override fun getFilterFieldIGDBName(): String = "aggregated_rating_count"
        override fun getFieldControl(): String = "aggregated_rating_count != null & aggregated_rating_count > 0"
    },
    GAME_MODE{
        override fun getFilterFieldIGDBName(): String = "game_modes"
        override fun getFieldControl(): String = "game_modes != null"
    },
    PLAYER_PERSPECTIVE{
        override fun getFilterFieldIGDBName(): String = "player_perspectives"
        override fun getFieldControl(): String = "player_perspectives != null"
    },
    THEMES{
        override fun getFilterFieldIGDBName(): String = "themes"
        override fun getFieldControl(): String = "themes != null"
    };


    abstract fun getFilterFieldIGDBName(): String
    abstract fun getFieldControl(): String
}