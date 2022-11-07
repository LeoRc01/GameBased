package com.cip.cipstudio.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.cip.cipstudio.R
import com.cip.cipstudio.model.data.Game
import com.cip.cipstudio.model.data.GameDetailsJson
import com.cip.cipstudio.view.MainActivity
import com.cip.cipstudio.view.fragment.GameDetailsFragment
import com.squareup.picasso.Picasso
import org.json.JSONArray
import org.json.JSONObject


class GamesRecyclerViewAdapter (val context : Context,
                                var games : List<GameDetailsJson>,
                                private val action: Int) :
    RecyclerView.Adapter<GamesRecyclerViewAdapter.ViewHolder>() {

    private val TAG = "GamesRecyclerViewAdapt"

    fun importItems(gamesDetailsJson : List<GameDetailsJson>){
        games  = gamesDetailsJson
        notifyDataSetChanged()
    }

    /**
    * Provide a reference to the type of views that you are using
    * (custom ViewHolder).
    */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val tvGameName : TextView
        val ivGameCover : ImageView
        val ivNoPreview : ImageView

        init {
            // Define click listener for the ViewHolder's View.

            tvGameName = view.findViewById(R.id.tvGameName)
            ivGameCover = view.findViewById(R.id.ivGameCover)
            ivNoPreview = view.findViewById(R.id.ivNoPreview)

        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.game_item, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element

        viewHolder.tvGameName.text = games[position].name
        games[position].coverUrl.let {
            if(it!="null") {
                val url = it.replace("thumb", "cover_big")
                Picasso.get().load("https:${url}").into(viewHolder.ivGameCover)
                viewHolder.ivGameCover.setOnClickListener {
                    val bundle = bundleOf("game_id" to games[position].id)
                    bundle.putString("game_cover", url)
                    viewHolder.itemView.findNavController().navigate(action, bundle)
                }
            } else {
                viewHolder.ivGameCover.visibility = View.GONE
                viewHolder.ivNoPreview.visibility = View.VISIBLE
            }
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = games.size



}