package com.cip.cipstudio.dataSource.filter.criteria

import android.util.Log
import java.sql.Timestamp
import java.text.SimpleDateFormat

class RangeCriteria(private val field: FilterField): Criteria {
    var min: Int? = null
    var max: Int? = null

    constructor(
        field: FilterField,
        min: Float,
        max:Float
    ) : this(field){
        this.min = min.toInt()
        this.max = max.toInt()
    }

    override fun buildQuery(): String {
        lateinit var minQuery : String
        lateinit var maxQuery : String
        if(min == max){
            minQuery = if (min != null) "${field.getFilterFieldIGDBName()} >= ${toTimestamp(min!!)}" else ""
            maxQuery = if (max != null) "${field.getFilterFieldIGDBName()} <= ${toTimestamp(max!!+1)}" else ""
        }else{
            minQuery = if (min != null) "${field.getFilterFieldIGDBName()} >= ${toTimestamp(min!!)}" else ""
            maxQuery = if (max != null) "${field.getFilterFieldIGDBName()} <= ${toTimestamp(max!!)}" else ""
        }
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