package com.cip.cipstudio.dataSource.filter.criteria

import com.api.igdb.apicalypse.Sort

// guardare "sorts" in res/values/arrays.xml
enum class SortEnum {
    AlphabeticalAZ {
        override fun nameValue(): String {
            return "Alphabetical (a-z)"
        }
        override fun getFieldIGDBName(): String {
            return "name"
        }
        override fun getSortType(): Sort {
            return Sort.ASCENDING
        }
        override fun getConditon(): String {
            return "${getFieldIGDBName()} != null"
        }
    },
    AlphabeticalZA {
        override fun nameValue(): String {
            return "Alphabetical (z-a)"
        }
        override fun getFieldIGDBName(): String {
            return "name"
        }
        override fun getSortType(): Sort {
            return Sort.DESCENDING
        }
        override fun getConditon(): String {
            return "${getFieldIGDBName()} != null"
        }
    },
    TopRated {
        override fun nameValue(): String {
            return "Top rated"
        }
        override fun getFieldIGDBName(): String {
            return "total_rating"
        }
        override fun getSortType(): Sort {
            return Sort.DESCENDING
        }
        override fun getConditon(): String {
            return "${getFieldIGDBName()} != null & ${getFieldIGDBName()} > 0"
        }
    },
    WorstRated {
        override fun nameValue(): String {
            return "Worst rated"
        }
        override fun getFieldIGDBName(): String {
            return "total_rating"
        }
        override fun getSortType(): Sort {
            return Sort.ASCENDING
        }
        override fun getConditon(): String {
            return "${getFieldIGDBName()} != null & ${getFieldIGDBName()} > 0"
        }
    },
    MostRecent {
        override fun nameValue(): String {
            return "Most recent"
        }
        override fun getFieldIGDBName(): String {
            return "first_release_date"
        }
        override fun getSortType(): Sort {
            return Sort.DESCENDING
        }
        override fun getConditon(): String {
            return "${getFieldIGDBName()} != null & ${getFieldIGDBName()} > 0"
        }
    },
    LeastRecent {
        override fun nameValue(): String {
            return "Least recent"
        }
        override fun getFieldIGDBName(): String {
            return "first_release_date"
        }
        override fun getSortType(): Sort {
            return Sort.ASCENDING
        }
        override fun getConditon(): String {
            return "${getFieldIGDBName()} != null & ${getFieldIGDBName()} > 0"
        }
    },
    MostPopular {
        override fun nameValue(): String {
            return "Most popular"
        }
        override fun getFieldIGDBName(): String {
            return "total_rating_count"
        }
        override fun getSortType(): Sort {
            return Sort.DESCENDING
        }
        override fun getConditon(): String {
            return "${getFieldIGDBName()} != null & ${getFieldIGDBName()} > 0"
        }
    },
    LeastPopular {
        override fun nameValue(): String {
            return "Least popular"
        }
        override fun getFieldIGDBName(): String {
            return "total_rating_count"
        }
        override fun getSortType(): Sort {
            return Sort.ASCENDING
        }
        override fun getConditon(): String {
            return "${getFieldIGDBName()} != null & ${getFieldIGDBName()} > 0"
        }
    },
    Default {
        override fun nameValue(): String {
            return "Default"
        }
        override fun getFieldIGDBName(): String {
            return ""
        }
        override fun getSortType(): Sort {
            return Sort.ASCENDING
        }
        override fun getConditon(): String {
            return ""
        }
    };

    abstract fun nameValue(): String
    abstract fun getFieldIGDBName(): String
    abstract fun getSortType(): Sort
    abstract fun getConditon(): String
}