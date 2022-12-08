package com.cip.cipstudio.dataSource.filter.criteria

enum class OperatorEnum {
    AND {
        override fun getOperator(): String {
            return "&"
        }
    },
    OR {
        override fun getOperator(): String {
            return "|"
        }
    };


    abstract fun getOperator(): String
}