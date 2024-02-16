package com.cip.cipstudio.adapters

import android.content.res.Configuration
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.cip.cipstudio.R
import com.squareup.picasso.Picasso
import org.json.JSONObject

class GameScreenshotsRecyclerViewAdapter
    (var screenshots : List<JSONObject>, val action : Int, val orientation : Int) :
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

        view.findViewById<ImageView>(R.id.ivGameScreenshot).layoutParams =
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                if(orientation == Configuration.ORIENTATION_LANDSCAPE) 1200 else 600,
            )

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val url = screenshots[position].getString("url").replace("t_thumb", "t_original")
        Picasso.get().load("https:$url").into(holder.ivGameScreenshot)
        holder.ivGameScreenshot.setOnClickListener {
            val bundle = bundleOf()
            bundle.putString("imageUrl", "https:$url")
            holder.itemView.findNavController().navigate(action, bundle)
        }
    }

    override fun getItemCount(): Int {
        return screenshots.size
    }
}