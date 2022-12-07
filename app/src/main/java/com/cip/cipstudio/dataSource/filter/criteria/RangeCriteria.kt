package com.cip.cipstudio.dataSource.filter.criteria

import android.util.Log
import java.sql.Timestamp
import java.text.SimpleDateFormat

class RangeCriteria(private val field: FilterField): Criteria {
    var min: Long? = null
    var max: Long? = null

    constructor(
        field: FilterField,
        min: Float,
        max: Float
    ) : this(field) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault())
        val minDate = dateFormat.parse("${min.toInt()}-01-01 00:00:00")
        val maxDate = dateFormat.parse("${max.toInt()}-12-31 23:59:59")
        this.min = minDate.time / 1000
        this.max = maxDate.time / 1000
    }

    override fun buildQuery(): String {
        val minQuery = if (min != null) "${field.getFilterFieldIGDBName()} >= $min" else ""
        val maxQuery = if (max != null) "${field.getFilterFieldIGDBName()} <= $max" else ""
        val queryTemp =  listOf(minQuery, maxQuery).filter { it.isNotEmpty() }.joinToString(" & ")
        return if (queryTemp.isNotEmpty()) "$queryTemp & ${field.getFieldControl()}" else ""
    }

    override fun isEmpty(): Boolean {
        return min == null && max == null
    }

    private fun toTimestamp(value : Int) : Long{
        val date = SimpleDateFormat("dd-MM-yyyy").parse("01-01-$value")
        return date.time/1000
    }
}