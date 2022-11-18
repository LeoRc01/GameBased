package com.cip.cipstudio.adapters

import android.content.Context
import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.cip.cipstudio.R
import com.cip.cipstudio.model.data.GameDetails
import com.cip.cipstudio.repository.MyFirebaseRepository
import com.cip.cipstudio.utils.IsFromFragmentEnum
import com.cip.cipstudio.view.MainActivity
import com.cip.cipstudio.view.fragment.SearchFragment
import com.squareup.picasso.Picasso


class GamesRecyclerViewAdapter (val context : Context,
                                var games : List<GameDetails>,
                                private val isFromFragment: IsFromFragmentEnum = IsFromFragmentEnum.MAIN_PAGE
                                ) :
    RecyclerView.Adapter<GamesRecyclerViewAdapter.ViewHolder>() {

    private val TAG = "GamesRecyclerViewAdapt"

    fun importItems(gamesDetailsJson : List<GameDetails>){
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
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.tvGameName.text = games[position].name
        games[position].coverUrl.let {
            if(!it.isEmpty() && it != "null") {
                Picasso.get().load(it).into(viewHolder.ivGameCover)
                viewHolder.ivGameCover.setOnClickListener {
                    val bundle = bundleOf()
                    bundle.putString("game_id", games[position].id)
                    bundle.putString("origin_fragment", isFromFragment.name)

                    MyFirebaseRepository.getInstance().addGamesToRecentlyViewed(games[position].id)
                    viewHolder.itemView.findNavController().navigate(isFromFragment.getFragmentAction(), bundle)
                }
            } else {
                viewHolder.ivNoPreview.visibility = View.VISIBLE
            }
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = games.size

}