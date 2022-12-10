package com.cip.cipstudio.model.data

data class AIModel(val genreId : String,
                    var weight : Int = 1) {
    override fun equals(other: Any?): Boolean {
        return (other as AIModel).genreId == genreId
    }


}