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

class GameScreenshotsRecyclerViewAdapter
    (val context : Context, var screenshotUrls : ArrayList<String>) :
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
        Picasso.get().load("https:${screenshotUrls[position].replace("t_thumb", "t_original")}").into(holder.ivGameScreenshot)
    }

    override fun getItemCount(): Int {
        return screenshotUrls.size
    }
}