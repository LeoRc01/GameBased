package com.cip.cipstudio.adapters

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cip.cipstudio.R
import com.cip.cipstudio.model.data.Game
import com.squareup.picasso.Picasso


class GamesRecyclerViewAdapter
    (val context : Context, var games : ArrayList<Game>) :
    RecyclerView.Adapter<GamesRecyclerViewAdapter.ViewHolder>() {

    fun importItems(_games : ArrayList<Game>){
        games = _games
        notifyDataSetChanged()
    }

    /**
    * Provide a reference to the type of views that you are using
    * (custom ViewHolder).
    */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNoPreview: TextView
        val ivGameCover : ImageView

        init {
            // Define click listener for the ViewHolder's View.
            tvNoPreview = view.findViewById(R.id.tvNoPreview)
            ivGameCover = view.findViewById(R.id.ivGameCover)
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
        //viewHolder.textView.text = games[position].name
        games[position].getCover(){
            val uiHandler = Handler(Looper.getMainLooper())
            uiHandler.post(Runnable {
                if(it!="NO_COVER")
                    Picasso.get().load("https:${it}").into(viewHolder.ivGameCover)
                else
                    viewHolder.tvNoPreview.visibility = View.VISIBLE
            })

        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = games.size



}