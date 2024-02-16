package com.cip.cipstudio.dataSource.repository

import com.cip.cipstudio.model.data.AIModel
import com.google.firebase.database.DataSnapshot
import org.json.JSONObject


object AISelector {
    var weightedItems : ArrayList<AIModel> = arrayListOf<AIModel>()
    private var hasFetched = false

    fun <T> addItemsToWeightedList(items : List<T>) {
        val listAI = toAIModelList(items)
        for (model in listAI) {
            if (containsAiModel(model)) {
                weightedItems.forEach {
                    if (it.genreId == model.genreId) {
                        if(it.weight < 5){
                            it.weight+=1
                            FirebaseRepository.updateFYPGenreWeight(it.genreId, it.weight)
                        }
                    }
                }
            } else {
                weightedItems.add(model)
                FirebaseRepository.addGenreToFYP(model.genreId)
            }
        }
        weightedItems.forEach {
            if(!containsAiModel(listAI as ArrayList<AIModel>,it)){
                if(it.weight>-5){
                    it.weight-=1
                    FirebaseRepository.updateFYPGenreWeight(it.genreId, it.weight)
                }
            }
        }
        weightedItems.sortByDescending {
            it.weight
        }
    }

    fun fetchDataFromFirebase(snapshot : DataSnapshot) {
        if(!hasFetched && snapshot.exists()) {
            for (item in snapshot.value as HashMap<String, HashMap<String, Int>>) {
                val model: AIModel = AIModel(item.key, item.value["weight"]!!)
                weightedItems.add(model)
            }
            weightedItems.sortByDescending {
                it.weight
            }

            hasFetched = true
        }

    }

    fun getOnlyPositiveWeightsModels() : ArrayList<AIModel>{
        var result = arrayListOf<AIModel>()
        for (model in weightedItems){
            if (model.weight>=0){
                result.add(model)
            }
        }
        result.sortByDescending {
            it.weight
        }
        return result
    }

    fun getUntilThreeHighestWeightModels() : ArrayList<AIModel>{
        var result = arrayListOf<AIModel>()
        var temp = weightedItems
        temp.sortByDescending {
            it.weight
        }
        if(weightedItems.size >= 3){
            result.add(temp[0])
            result.add(temp[1])
            result.add(temp[2])
        }else if(weightedItems.size == 2){
            result.add(temp[0])
            result.add(temp[1])
        }else if(weightedItems.size == 1){
            result.add(temp[0])
        }
        return result
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