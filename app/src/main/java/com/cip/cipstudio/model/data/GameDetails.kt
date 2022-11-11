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
    var rating: String = ""
    var ratingCount: String = ""
    var totalRating: String = ""
    var totalRatingCount: String = ""
    var screenshots: List<JSONObject> = ArrayList()
    var genres: List<JSONObject> = ArrayList()
    var platforms: List<JSONObject> = ArrayList()
    var similarGames: List<GameDetails> = ArrayList()
    var isFavourite: Boolean = false

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
        screenshots = jsonGame.getArrayListOrEmpty("screenshots")
        genres = jsonGame.getArrayListOrEmpty("genres")
        platforms = jsonGame.getArrayListOrEmpty("platforms")
        similarGames = jsonGame.getGameDetailsArrayListOrEmpty("similar_games")
    }

    fun setCoverUrl(cover: JSONObject) {
        coverUrl = cover.getStringOrEmpty("url")
        if (!coverUrl.isEmpty()) {
            coverUrl = coverUrl.replace("t_thumb", "t_cover_big")
            coverUrl = "https:$coverUrl"
        }
    }

    // TODO: trovare qualcosa di meglio per il format della data
    fun setFirstReleaseDate(timestamp: Long) {
        val date = Date(timestamp * 1000)
        val simpleDateFormat = java.text.SimpleDateFormat("dd MMM yyyy")
        this.releaseDate = simpleDateFormat.format(date)
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
            return this.getString(field)
        } else {
            return ""
        }
    }

    private fun JSONObject.getDoubleOrEmpty(field: String) : String {
        if (this.has(field)) {
            return this.getDouble(field).roundToInt().toString()
        } else {
            return "0"
        }
    }

    private fun JSONObject.getArrayListOrEmpty(field: String) : List<JSONObject> {
        return Converter.fromJsonObjectToArrayList(this, field)
    }

    private fun JSONObject.getGameDetailsArrayListOrEmpty(field: String) : List<GameDetails> {
        return Converter.fromJsonObjectToGameDetailsArrayList(this, field)
    }

    private fun JSONObject.getIntOrZero(field: String) : String {
        if (this.has(field)) {
            return this.getInt(field).toString()
        } else {
            return "0"
        }
    }



}