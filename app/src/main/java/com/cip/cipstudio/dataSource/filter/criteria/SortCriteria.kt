package com.cip.cipstudio.dataSource.filter.criteria

import com.api.igdb.apicalypse.Sort

class SortCriteria(private val sort: SortEnum) {

    fun getName() : String {
        return sort.getFieldIGDBName()
    }

    fun getValues() : String {
        return sort.nameValue()
    }

    fun getSortType() : Sort {
        return sort.getSortType()
    }

    fun isEmpty(): Boolean {
        return sort == SortEnum.Default
    }

    fun getCondition(): String {
        return sort.getConditon()
    }

}