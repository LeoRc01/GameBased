package com.cip.cipstudio.dataSource.filter.criteria

interface Criteria {
    fun buildQuery(): String

    fun concatCriteria(): String {
        return '&' + this.buildQuery()
    }
}