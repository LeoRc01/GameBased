package com.cip.cipstudio.utils

import com.cip.cipstudio.model.data.PlatformDetails
import org.json.JSONObject

object Costant {

    val platformDefault : ArrayList<PlatformDetails> = arrayListOf(
            PlatformDetails("6", "PC (Microsoft Windows)"),
            PlatformDetails("130", "Nintendo Switch"),
            PlatformDetails("48", "PlayStation 4"),
            PlatformDetails("167", "PlayStation 5"),
            PlatformDetails("169", "Xbox Series X|S"),
            PlatformDetails("49", "Xbox One"),
            PlatformDetails("9", "PlayStation 3"),
            PlatformDetails("14", "Mac"),
            PlatformDetails("3", "Linux"),
            PlatformDetails("12", "Xbox 360"),
            PlatformDetails("20", "Nintendo DS"))

        val categoryDefault : ArrayList<JSONObject> = arrayListOf(
                JSONObject().put("id", "0").put("name", "Main game"),
                JSONObject().put("id", "1").put("name", "DLC/Addon"),
                JSONObject().put("id", "2").put("name", "Expansion"),
                JSONObject().put("id", "3").put("name", "Bundle"),
                JSONObject().put("id", "4").put("name", "Stand-alone expansion"),
                JSONObject().put("id", "5").put("name", "Mod"),
                JSONObject().put("id", "6").put("name", "Episode"),
                JSONObject().put("id", "7").put("name", "Season"),
                JSONObject().put("id", "8").put("name", "Remake"),
                JSONObject().put("id", "9").put("name", "Remaster"),
                JSONObject().put("id", "10").put("name", "Expanded edition"),
                JSONObject().put("id", "11").put("name", "Port"),
                JSONObject().put("id", "12").put("name", "Fork"),
                JSONObject().put("id", "13").put("name", "Pack"),
                JSONObject().put("id", "14").put("name", "Update"),
        )

        val statusDefault : ArrayList<JSONObject> = arrayListOf(
                JSONObject().put("id", "0").put("name", "Released"),
                JSONObject().put("id", "2").put("name", "Alpha"),
                JSONObject().put("id", "3").put("name", "Beta"),
                JSONObject().put("id", "4").put("name", "Early access"),
                JSONObject().put("id", "5").put("name", "Offline"),
                JSONObject().put("id", "6").put("name", "Cancelled"),
                JSONObject().put("id", "7").put("name", "Rumored"),
                JSONObject().put("id", "8").put("name", "Delisted"),

        )
}