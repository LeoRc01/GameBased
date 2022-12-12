package com.cip.cipstudio.dataSource.filter.criteria

class OperatorCriteria(private val operatorEnum : OperatorEnum): Criteria {
    private var criteriaList : MutableList<Criteria> = ArrayList()

    constructor(operatorEnum : OperatorEnum, criteriaList: List<Criteria>) : this(operatorEnum) {
        this.criteriaList = criteriaList as MutableList<Criteria>
    }

    fun addCriteria(criteria: Criteria) {
        if(!criteria.isEmpty())
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
        return criteriaList.joinToString(" ${operatorEnum.getOperator()} ") { it.buildQuery() }
    }

    override fun isEmpty(): Boolean {
        return criteriaList.isEmpty()
    }
}
