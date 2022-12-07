package com.cip.cipstudio.dataSource.filter.criteria

interface Criteria {
    fun buildQuery(): String

    fun concatCriteria(): String {
        val query = buildQuery()
        return if (query.isNotEmpty()) " & ($query)" else ""
    }

    fun isEmpty() : Boolean
}