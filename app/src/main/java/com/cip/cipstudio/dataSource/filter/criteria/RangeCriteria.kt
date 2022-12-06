package com.cip.cipstudio.dataSource.filter.criteria

class RangeCriteria(private val field: FilterField): Criteria {
    var min: Int? = null
    var max: Int? = null

    override fun buildQuery(): String {
        val minQuery = if (min != null) "${field.getFilterFieldIGDBName()} >= $min" else ""
        val maxQuery = if (max != null) "${field.getFilterFieldIGDBName()} <= $max" else ""
        val queryTemp =  listOf(minQuery, maxQuery).filter { it.isNotEmpty() }.joinToString(" & ")
        return if (queryTemp.isNotEmpty()) "$queryTemp & ${field.getFieldControl()}" else ""
    }

    override fun isEmpty(): Boolean {
        TODO("Not yet implemented")
    }
}