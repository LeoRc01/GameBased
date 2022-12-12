package com.cip.cipstudio.dataSource.filter.criteria

import java.text.SimpleDateFormat

class RangeCriteria(private val field: FilterFieldEnum): Criteria {
    private var min: Long? = null
    private var max: Long? = null
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault())

    fun setMin(min: Float) {
        this.min = if (field == FilterFieldEnum.RELEASE_DATE_YEAR)
                dateFormat.parse("${min.toInt()}-01-01 00:00:00")!!.time.div(1000)
            else
                min.toLong()
    }

    fun setMax(max: Float) {
        this.max = if (field == FilterFieldEnum.RELEASE_DATE_YEAR)
                dateFormat.parse("${max.toInt()}-12-31 23:59:59")!!.time.div(1000)
            else
                max.toLong()
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