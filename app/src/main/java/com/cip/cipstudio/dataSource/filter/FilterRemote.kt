package com.cip.cipstudio.dataSource.filter

import androidx.lifecycle.MutableLiveData
import com.cip.cipstudio.dataSource.filter.criteria.ViewModelFilter
import com.cip.cipstudio.dataSource.repository.IGDBRepository.IGDBRepositoryRemote
import com.cip.cipstudio.model.data.PlatformDetails
import com.cip.cipstudio.utils.Costant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class FilterRemote(private val coroutineScope: CoroutineScope,
                   private val isPageLoading: MutableLiveData<Boolean>) : ViewModelFilter {
    private val TAG = "FilterRemote"

    private val gameRepository = IGDBRepositoryRemote
    private var offsetPlatform = -1
    private val platformDefault : ArrayList<PlatformDetails> = Costant.platformDefault
    private val platformDefaultIds = platformDefault.map { it.id }

    override fun getPlatforms(
        updateUI : (List<PlatformDetails>) -> Unit
    ){
        isPageLoading.postValue(true)
        if (offsetPlatform == -1) {
            updateUI.invoke(platformDefault)
            isPageLoading.postValue(false)
        }
        else {
            coroutineScope.launch(Dispatchers.Main) {
                val platforms = withContext(Dispatchers.IO) {
                    gameRepository.getPlatforms(offsetPlatform, platformDefaultIds)
                }
                updateUI.invoke(platforms)
                isPageLoading.postValue(false)
            }
        }
        offsetPlatform++
    }

    override fun getGenres(updateUI : (ArrayList<JSONObject>)->Unit) {
        isPageLoading.postValue(true)
        coroutineScope.launch(Dispatchers.Main) {
            val genres = withContext(Dispatchers.IO) {
                gameRepository.getGenres()
            }
            updateUI.invoke(genres)
            isPageLoading.postValue(false)
        }
    }

    override fun getPlayerPerspectives(updateUI : (ArrayList<JSONObject>)->Unit) {
        isPageLoading.postValue(true)
        coroutineScope.launch(Dispatchers.Main) {
            val playerPerspectives = withContext(Dispatchers.IO) {
                gameRepository.getPlayerPerspectives()
            }
            updateUI.invoke(playerPerspectives)
            isPageLoading.postValue(false)
        }
    }

    override fun getGameModes(updateUI : (ArrayList<JSONObject>)->Unit) {
        isPageLoading.postValue(true)
        coroutineScope.launch(Dispatchers.Main) {
            val gameModes = withContext(Dispatchers.IO) {
                gameRepository.getGameModes()
            }
            updateUI.invoke(gameModes)
            isPageLoading.postValue(false)
        }
    }

    override fun getThemes(updateUI : (ArrayList<JSONObject>)->Unit) {
        isPageLoading.postValue(true)
        coroutineScope.launch(Dispatchers.Main) {
            val themes = withContext(Dispatchers.IO) {
                gameRepository.getThemes()
            }
            updateUI.invoke(themes)
            isPageLoading.postValue(false)
        }
    }

    override fun getYears(updateUI : (List<Float>)->Unit) {
        isPageLoading.postValue(true)
        coroutineScope.launch(Dispatchers.Main) {
            val years = withContext(Dispatchers.IO) {
                gameRepository.getFirstAndLastYearsOfRelease()
            }
            updateUI.invoke(years)
            isPageLoading.postValue(false)
        }
    }

    override fun getCategory(updateUI : (ArrayList<JSONObject>)->Unit) {
        isPageLoading.postValue(true)
        updateUI.invoke(Costant.categoryDefault)
        isPageLoading.postValue(false)
    }

    override fun getStatus(updateUI: (ArrayList<JSONObject>) -> Unit) {
        isPageLoading.postValue(true)
        updateUI.invoke(Costant.statusDefault)
        isPageLoading.postValue(false)
    }

}