
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
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.cip.cipstudio.R
import com.cip.cipstudio.model.data.GameDetails
import com.cip.cipstudio.repository.MyFirebaseRepository
import com.cip.cipstudio.utils.IsFromFragmentEnum
import com.squareup.picasso.Picasso


class GamesBigRecyclerViewAdapter (val context : Context,
                                var games : List<GameDetails>,
                                private val action: Int,
                                private val isFromFragment: IsFromFragmentEnum
                                ) : RecyclerView.Adapter<GamesBigRecyclerViewAdapter.ViewHolder>() {

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
        val ivBlurBackground : ImageView
        val ivGameCoverForeground : ImageView
        val tvGameNameBigCover : TextView

        init {
            ivBlurBackground = view.findViewById(R.id.i_recently_viewed_game_iv_blur_background)
            ivGameCoverForeground = view.findViewById(R.id.i_recently_viewed_game_iv_game_cover_foreground)
            tvGameNameBigCover = view.findViewById(R.id.tvGameNameBigCover)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.recently_viewed_game_item, viewGroup, false)
        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element

        viewHolder.tvGameNameBigCover.text = games[position].name
        games[position].coverUrl.let {
            if(!it.isEmpty() && it != "null") {
                Picasso.get().load(it).into(viewHolder.ivGameCoverForeground)
                Picasso.get().load(it).into(viewHolder.ivBlurBackground)
                viewHolder.ivBlurBackground.setRenderEffect(RenderEffect.createBlurEffect(30F, 30F, Shader.TileMode.MIRROR))
                viewHolder.ivGameCoverForeground.setOnClickListener {
                    val bundle = bundleOf()
                    bundle.putString("game_id", games[position].id)
                    bundle.putString("origin_fragment", isFromFragment.name)
                    MyFirebaseRepository.getInstance().addGamesToRecentlyViewed(games[position].id)
                    viewHolder.itemView.findNavController().navigate(action, bundle)
                }
            }
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = games.size

}