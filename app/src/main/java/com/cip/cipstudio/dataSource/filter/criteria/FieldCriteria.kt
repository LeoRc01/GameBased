package com.cip.cipstudio.dataSource.filter.criteria

class FieldCriteria(private val field: FilterField): Criteria {
    private var values: List<String> = ArrayList()

    constructor(field: FilterField, values: List<String>) : this(field) {
        this.values = values
    }

    override fun buildQuery(): String {
        return if (values.size > 0)
            "${field.getFilterFieldIGDBName()} = (${values.joinToString(",")}) & ${field.getFieldControl()}"
        else
            ""
    }
}
