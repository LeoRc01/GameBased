package com.cip.cipstudio

object StateInstanceSaver {
    private val stateInstanceSaved = mutableMapOf<String, java.io.Serializable?>()

    fun saveState(key: String, value: java.io.Serializable) {
        stateInstanceSaved[key] = value
    }

    fun restoreState(key: String): java.io.Serializable? {
        return stateInstanceSaved[key]
    }

    fun deleteState(key: String) {
        stateInstanceSaved[key] = null
    }

    fun clearState() {
        stateInstanceSaved.clear()
    }


}