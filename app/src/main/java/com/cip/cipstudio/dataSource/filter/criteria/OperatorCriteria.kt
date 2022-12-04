package com.cip.cipstudio.dataSource.filter.criteria

class OperatorCriteria(private val operator : Operator): Criteria {
    private val listCriteria = mutableListOf<Criteria>()

    constructor(operator : Operator, criteriaLeft : Criteria, criteriaRight : Criteria) : this(operator) {
        listCriteria.add(criteriaLeft)
        listCriteria.add(criteriaRight)
    }

    override fun buildQuery(): String {
        if (listCriteria.size == 0) {
            return ""
        }
        if (listCriteria.size == 1) {
            return listCriteria[0].buildQuery()
        }
        return listCriteria.joinToString(" ${operator.getOperator()} ") { it.buildQuery() }
    }
}
