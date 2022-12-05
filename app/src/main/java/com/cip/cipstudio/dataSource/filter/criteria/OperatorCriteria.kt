package com.cip.cipstudio.dataSource.filter.criteria

class OperatorCriteria(private val operator : Operator): Criteria {
    private var criteriaList : MutableList<Criteria> = ArrayList()

    constructor(operator : Operator, criteriaList: List<Criteria>) : this(operator) {
        this.criteriaList = criteriaList as MutableList<Criteria>
    }

    fun addCriteria(criteria: Criteria) {
        criteriaList += criteria
    }

    fun clearCriteria() {
        criteriaList.clear()
    }

    override fun buildQuery(): String {
        if (criteriaList.isEmpty()) {
            return ""
        }
        if (criteriaList.size == 1) {
            return criteriaList[0].buildQuery()
        }
        return criteriaList.joinToString(" ${operator.getOperator()} ") { it.buildQuery() }
    }
}
