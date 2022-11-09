package com.cip.cipstudio.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cip.cipstudio.R
import com.cip.cipstudio.model.data.Game
import com.squareup.picasso.Picasso
import org.json.JSONObject

class GameScreenshotsRecyclerViewAdapter
    (val context : Context, var screenshots : List<JSONObject>) :
    RecyclerView.Adapter<GameScreenshotsRecyclerViewAdapter.ViewHolder>()  {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivGameScreenshot : ImageView
        init {
            ivGameScreenshot = view.findViewById(R.id.ivGameScreenshot)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.game_screenshot_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val url = screenshots[position].getString("url").replace("t_thumb", "t_original")
        Picasso.get().load("https:$url").into(holder.ivGameScreenshot)
    }

    override fun getItemCount(): Int {
        return screenshots.size
    }
}