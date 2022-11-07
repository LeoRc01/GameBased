package com.cip.cipstudio.model.data

import com.cip.cipstudio.utils.Converter
import org.json.JSONArray
import org.json.JSONObject

class GameDetailsJson(val id: String,
                      val name: String) {
    lateinit var coverUrl: String
    lateinit var summary: String
    lateinit var first_release_date: String
    lateinit var rating: String
    lateinit var rating_count: String
    lateinit var total_rating: String
    lateinit var total_rating_count: String
    var screenshots: List<JSONObject> = ArrayList()
    var genres: List<JSONObject> = ArrayList()
    var platforms: List<JSONObject> = ArrayList()
    var similar_games: List<GameDetailsJson> = ArrayList()

    constructor(jsonGame: JSONObject) :
            this(id = jsonGame.getString("id"),
                name = jsonGame.getString("name")) {
        coverUrl = jsonGame.getString("cover")
        summary = jsonGame.getString("summary")
        first_release_date = jsonGame.getString("first_release_date")
        rating = jsonGame.getString("rating")
        rating_count = jsonGame.getString("rating_count")
        total_rating = jsonGame.getString("total_rating")
        total_rating_count = jsonGame.getString("total_rating_count")
        screenshots = Converter.fromJsonObjectFieldToArrayList(jsonGame, "screenshots")
        genres = Converter.fromJsonObjectFieldToArrayList(jsonGame, "genres")
        platforms = Converter.fromJsonObjectFieldToArrayList(jsonGame, "platforms")
        similar_games = Converter.fromJsonObjectFieldToArrayList(jsonGame, "similar_games")
            .map { jsonObject -> GameDetailsJson(jsonObject) }
    }



}