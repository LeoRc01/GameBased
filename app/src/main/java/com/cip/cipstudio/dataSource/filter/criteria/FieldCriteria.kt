package com.cip.cipstudio.dataSource.filter.criteria

class FieldCriteria(private val field: FilterField): Criteria {
    private val values: MutableList<String> = mutableListOf()

    fun addValue(value: String) {
        values.add(value)
    }


    override fun buildQuery(): String {
        return if (values.size == 0)
            "${field.getFilterFieldIGDBName()} = (${values.joinToString(", ")}) & ${field.getFieldControl()}"
        else
            ""
    }
}
