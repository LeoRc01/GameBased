package com.cip.cipstudio.model.data

import com.cip.cipstudio.repository.MyFirebaseRepository
import com.cip.cipstudio.utils.Converter
import com.google.android.gms.tasks.Task
import org.json.JSONObject
import java.sql.Date
import kotlin.math.roundToInt

class GameDetails(val id: String,
                  val name: String) {
    var coverUrl: String = ""
    var summary: String = ""
    var releaseDate: String = ""
    var rating: String = "0"
    var ratingCount: String = "0"
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
        rating = jsonGame.getDoubleOrEmpty("rating")
        ratingCount = jsonGame.getIntOrZero("rating_count")
        totalRating = jsonGame.getDoubleOrEmpty("total_rating")
        totalRatingCount = jsonGame.getIntOrZero("total_rating_count")
        screenshots = jsonGame.getListOrEmpty("screenshots")
        genres = jsonGame.getListOrEmpty("genres")
        platforms = jsonGame.getListOrEmpty("platforms")
        similarGames = jsonGame.getGameDetailsListOrEmpty("similar_games")
        setInvolvedCompany(jsonGame)
    }

    fun setCoverUrl(cover: JSONObject) {
        coverUrl = cover.getStringOrEmpty("url")
        if (!coverUrl.isEmpty()) {
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

    fun setGameToFavourite() : Task<Void> {
        return MyFirebaseRepository.getInstance().setGameToFavourite(id).addOnSuccessListener {
            isFavourite = true
        }
    }

    fun removeGameFromFavourite() : Task<Void> {
        return MyFirebaseRepository.getInstance().removeGameFromFavourite(id).addOnSuccessListener {
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
        }
    }

    private fun getInvolvedCompanies(involvedCompanyType: InvolvedCompanyType) : String {
        val involvedCompanies = when (involvedCompanyType) {
            InvolvedCompanyType.DEVELOPER -> involvedCompaniesDevelopers
            InvolvedCompanyType.PUBLISHER -> involvedCompaniesPublishers
            InvolvedCompanyType.SUPPORTING -> involvedCompaniesSupporting
        }
        val developers : ArrayList<String> = arrayListOf()
        involvedCompanies.forEach {
            val company = it.getJSONObject("company")
            val name = company.getString("name")
            developers.add(name)
        }
        return developers.joinToString(", ")
    }

    fun getDevelopers() : String {
        return getInvolvedCompanies(InvolvedCompanyType.DEVELOPER)
    }

    fun getPublishers() : String {
        return getInvolvedCompanies(InvolvedCompanyType.PUBLISHER)
    }

    fun getSupporters() : String {
        return getInvolvedCompanies(InvolvedCompanyType.SUPPORTING)
    }
}

enum class InvolvedCompanyType {
    DEVELOPER,
    PUBLISHER,
    SUPPORTING
}
