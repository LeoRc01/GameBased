package com.cip.cipstudio.utils

object StateInstanceSaver {
    private val stateInstanceSaved = mutableMapOf<String, Map<String, Any>?>()

    fun saveState(key: String, value: Map<String, Any>?) {
        stateInstanceSaved[key] = value
    }

    fun restoreState(key: String): Map<String, Any>? {
        return stateInstanceSaved[key]
    }

    fun deleteState(key: String) {
        stateInstanceSaved[key] = null
    }

    fun clearState() {
        stateInstanceSaved.clear()
    }


}