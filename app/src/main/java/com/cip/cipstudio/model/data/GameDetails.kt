package com.cip.cipstudio.model.data

import com.cip.cipstudio.model.User
import com.cip.cipstudio.utils.Converter
import com.google.android.gms.tasks.Task
import org.json.JSONObject
import java.sql.Date
import kotlin.math.roundToInt

data class GameDetails(val id: String,
                  val name: String) {
    var coverUrl: String = ""
    var summary: String = ""
    var releaseDate: String = ""
    var criticsRating: String = "0"
    var criticsRatingCount: String = "0"
    var totalRating: String = "0"
    var totalRatingCount: String = "0"
    var screenshots: List<JSONObject> = ArrayList()
    var genres: List<JSONObject> = ArrayList()
    var platforms: List<JSONObject> = ArrayList()
    var similarGames: List<GameDetails> = ArrayList()
    var isFavourite: Boolean = false
    var involvedCompaniesDevelopers: List<JSONObject> = ArrayList()
    var involvedCompaniesPublishers: List<JSONObject> = ArrayList()
    var involvedCompaniesSupporting: List<JSONObject> = ArrayList()
    var involvedCompaniesPorting: List<JSONObject> = ArrayList()
    var franchise: JSONObject? = null
    var languageSupport: List<JSONObject> = ArrayList()
    var gameModes: List<JSONObject> = ArrayList()
    var playerPerspectives: List<JSONObject> = ArrayList()
    var collection: JSONObject? = null
    var parentGame: GameDetails? = null
    var dlcs: List<GameDetails> = ArrayList()


    var fields = HashSet<String>()

    constructor(jsonGame: JSONObject) :
            this(id = jsonGame.getString("id"),
                name = jsonGame.getString("name")) {
        if (jsonGame.has("cover")) {
            setCoverUrl(jsonGame.getJSONObject("cover"))
        }

        if (jsonGame.has("first_release_date")) {
            setFirstReleaseDate(jsonGame.getLong("first_release_date"))
        }

        summary = jsonGame.getStringOrEmpty("summary")
        criticsRating = jsonGame.getDoubleOrEmpty("aggregated_rating")
        criticsRatingCount = jsonGame.getIntOrZero("aggregated_rating_count")
        totalRating = jsonGame.getDoubleOrEmpty("total_rating")
        totalRatingCount = jsonGame.getIntOrZero("total_rating_count")
        screenshots = jsonGame.getListOrEmpty("screenshots")
        genres = jsonGame.getListOrEmpty("genres")
        platforms = jsonGame.getListOrEmpty("platforms")
        similarGames = jsonGame.getGameDetailsListOrEmpty("similar_games")
        setInvolvedCompany(jsonGame)
        gameModes = jsonGame.getListOrEmpty("game_modes")
        playerPerspectives = jsonGame.getListOrEmpty("player_perspectives")
        if (jsonGame.has("language_support")) {
            languageSupport = jsonGame
                .getJSONObject("language_support")
                .getListOrEmpty("language")
        }
        if (jsonGame.has("franchise")) {
            franchise = jsonGame.getJSONObject("franchise")
            fields.add("franchise")
        }
        if (jsonGame.has("collection")) {
            collection = jsonGame.getJSONObject("collection")
            fields.add("collection")
        }

        if (jsonGame.has("parent_game")) {
            parentGame = GameDetails(jsonGame.getJSONObject("parent_game"))
            fields.add("parent_game")
        }
        dlcs = jsonGame.getGameDetailsListOrEmpty("dlcs")

    }

    fun setCoverUrl(cover: JSONObject) {
        coverUrl = cover.getStringOrEmpty("url")
        if (coverUrl.isNotEmpty()) {
            coverUrl = coverUrl.replace("t_thumb", "t_cover_big")
            coverUrl = "https:$coverUrl"
            fields.add("cover")
        }
    }

    // TODO: trovare qualcosa di meglio per il format della data
    fun setFirstReleaseDate(timestamp: Long) {
        val date = Date(timestamp * 1000)
        val simpleDateFormat = java.text.SimpleDateFormat("dd MMM yyyy")
        this.releaseDate = simpleDateFormat.format(date)
        fields.add("first_release_date")
    }

    fun setGameToFavourite() : Task<*> {
        return User.setGameToFavourite(id).addOnSuccessListener {
            isFavourite = true
        }
    }

    fun removeGameFromFavourite() : Task<*> {
        return User.removeGameFromFavourite(id).addOnSuccessListener {
            isFavourite = false
        }
    }

    private fun JSONObject.getStringOrEmpty(field: String) : String {
        if (this.has(field)) {
            fields.add(field)
            return this.getString(field)
        } else {
            return ""
        }
    }

    private fun JSONObject.getDoubleOrEmpty(field: String) : String {
        if (this.has(field)) {
            fields.add(field)
            return this.getDouble(field).roundToInt().toString()
        } else {
            return "0"
        }
    }

    private fun JSONObject.getListOrEmpty(field: String) : List<JSONObject> {
        val list = Converter.fromJsonObjectToArrayList(this, field)
        if (list.isNotEmpty()) {
            fields.add(field)
        }
        return list
    }

    private fun JSONObject.getGameDetailsListOrEmpty(field: String) : List<GameDetails> {
        val list = Converter.fromJsonObjectToGameDetailsArrayList(this, field)
        if (list.isNotEmpty()) {
            fields.add(field)
        }
        return list
    }

    private fun JSONObject.getIntOrZero(field: String) : String {
        if (this.has(field)) {
            fields.add(field)
            return this.getInt(field).toString()
        } else {
            return "0"
        }
    }

    private fun setInvolvedCompany(json: JSONObject) {
        if (!json.has("involved_companies")) return

        val involvedCompany = json.getJSONArray("involved_companies")

        for (i in 0 until involvedCompany.length()) {
            val company = involvedCompany.getJSONObject(i)
            val developer = company.getBoolean("developer")
            val publisher = company.getBoolean("publisher")
            val supporting = company.getBoolean("supporting")
            val porting = company.getBoolean("porting")

            if (developer) {
                if (!fields.contains("developer")) {
                    fields.add("developer")
                }
                (involvedCompaniesDevelopers as ArrayList).add(company)
            }
            if (publisher) {
                if (!fields.contains("publisher")) {
                    fields.add("publisher")
                }
                (involvedCompaniesPublishers as ArrayList).add(company)
            }
            if (supporting) {
                if (!fields.contains("support")) {
                    fields.add("support")
                }
                (involvedCompaniesSupporting as ArrayList).add(company)
            }
            if (porting) {
                if (!fields.contains("porting")) {
                    fields.add("porting")
                }
                (involvedCompaniesPorting as ArrayList).add(company)
            }
        }
    }

    fun getDevelopers() : String {
        return getStringFromListOfJSONObject(involvedCompaniesDevelopers, "company", "name")
    }

    fun getPublishers() : String {
        return getStringFromListOfJSONObject(involvedCompaniesPublishers, "company", "name")
    }

    fun getSupporters() : String {
        return getStringFromListOfJSONObject(involvedCompaniesSupporting, "company", "name")
    }

    fun getPorters() : String {
        return getStringFromListOfJSONObject(involvedCompaniesPorting, "company", "name")
    }

    private fun getStringFromListOfJSONObject
                (list: List<JSONObject>,
                 nameObject: String,
                 nameField: String) : String {
        val result : ArrayList<String> = arrayListOf()
        list.forEach {
            val platform = it.getJSONObject(nameObject)
            val name = platform.getString(nameField)
            result.add(name)
        }
        return result.joinToString(", ")
    }

    private fun getStringFromListOfJSONObject
                (list: List<JSONObject>,
                 nameField: String = "name") : String {
        val result : ArrayList<String> = arrayListOf()
        list.forEach {
            val name = it.getString(nameField)
            result.add(name)
        }
        return result.joinToString(", ")
    }

    fun getPlatformsString() : String {
        return getStringFromListOfJSONObject(platforms)
    }

    fun getGameModesString() : String {
        return getStringFromListOfJSONObject(gameModes)
    }

    fun getPlayerPerspectivesString() : String {
        return getStringFromListOfJSONObject(playerPerspectives)
    }

    fun getLanguageString() : String {
        return getStringFromListOfJSONObject(languageSupport, "language", "name")
    }

    fun getFranchiseString() : String {
        if (franchise != null) {
            return franchise!!.getString("name")
        }
        return ""
    }

    fun getCollectionString() : String {
        if (collection != null) {
            return collection!!.getString("name")
        }
        return ""
    }

}

