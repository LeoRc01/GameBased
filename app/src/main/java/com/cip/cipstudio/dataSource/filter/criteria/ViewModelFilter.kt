package com.cip.cipstudio.dataSource.filter.criteria

import com.cip.cipstudio.model.data.PlatformDetails
import org.json.JSONObject

interface ViewModelFilter {
    fun getCategory(updateUI : (ArrayList<JSONObject>)->Unit)
    fun getPlatforms(updateUI : (List<PlatformDetails>)->Unit)
    fun getGenres(updateUI : (ArrayList<JSONObject>)->Unit)
    fun getThemes(updateUI : (ArrayList<JSONObject>)->Unit)
    fun getGameModes(updateUI : (ArrayList<JSONObject>)->Unit)
    fun getYears(updateUI : (List<Float>)->Unit)
    fun getPlayerPerspectives(updateUI : (ArrayList<JSONObject>)->Unit)

}