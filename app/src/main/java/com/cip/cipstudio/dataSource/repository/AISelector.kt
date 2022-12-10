package com.cip.cipstudio.dataSource.repository

import android.util.Log
import com.cip.cipstudio.model.data.AIModel
import org.json.JSONObject


object AISelector {
    var weightedItems : ArrayList<AIModel> = arrayListOf<AIModel>()

    fun <T> addItemsToWeightedList(items : List<T>) {
        val listAI = toAIModelList(items)
        for (model in listAI) {
            if (containsAiModel(model)) {
                weightedItems.forEach {
                    if (it.genreId == model.genreId) {
                        it.weight++
                    }
                }
            } else {
                weightedItems.add(model)
            }
        }
        weightedItems.forEach {
            if(!containsAiModel(listAI as ArrayList<AIModel>,it)){
                it.weight--
            }
        }
        weightedItems.sortByDescending {
            it.weight
        }
    }

    private fun containsAiModel(list : ArrayList<AIModel>,model : AIModel) : Boolean{
        for (item in list){
            if(item == model)
                return true;
        }
        return false;
    }

    private fun containsAiModel(model : AIModel) : Boolean{
        for (item in weightedItems){
            if(item == model)
                return true;
        }
        return false;
    }

    private fun <T> toAIModelList(items: List<T>) : List<AIModel>{
        var result : ArrayList<AIModel> = arrayListOf()
        items.forEach {
            val item = AIModel(genreId = (it as JSONObject).getString("id"),)
            result.add(item)
        }
        return result
    }
}