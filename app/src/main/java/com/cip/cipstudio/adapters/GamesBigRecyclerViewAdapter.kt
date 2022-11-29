
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
import androidx.cardview.widget.CardView
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.cip.cipstudio.R
import com.cip.cipstudio.dataSource.repository.HistoryRepository
import com.cip.cipstudio.model.data.GameDetails
import com.cip.cipstudio.dataSource.repository.historyRepositoryImpl.HistoryRepositoryLocal
import com.cip.cipstudio.model.User
import com.cip.cipstudio.utils.ActionGameDetailsEnum
import com.squareup.picasso.Picasso
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class GamesBigRecyclerViewAdapter (val context : Context,
                                var games : List<GameDetails>,
                                private val actionGameDetails: ActionGameDetailsEnum = ActionGameDetailsEnum.SEARCH_PAGE
                                ) : RecyclerView.Adapter<GamesBigRecyclerViewAdapter.ViewHolder>() {

    private val TAG = "GamesRecyclerViewAdapt"
    private lateinit var historyDB: HistoryRepository

    fun importItems(gamesDetailsJson : List<GameDetails>){
        games  = gamesDetailsJson
        notifyDataSetChanged()
    }

    fun addItems(gamesDetailsJson : List<GameDetails>){
        games += gamesDetailsJson
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
        val card : CardView

        init {
            ivBlurBackground = view.findViewById(R.id.i_recently_viewed_game_iv_blur_background)
            ivGameCoverForeground = view.findViewById(R.id.i_recently_viewed_game_iv_game_cover_foreground)
            tvGameNameBigCover = view.findViewById(R.id.i_recently_viewed_game_tv_GameNameBigCover)
            card = view.findViewById(R.id.i_recently_viewed_game_cover)
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

        historyDB = HistoryRepositoryLocal(viewHolder.itemView.context)

        viewHolder.tvGameNameBigCover.text = games[position].name
        games[position].coverUrl.let {
            if(!it.isEmpty() && it != "null") {
                Picasso.get().load(it).into(viewHolder.ivGameCoverForeground)
                Picasso.get().load(it).into(viewHolder.ivBlurBackground)
                viewHolder.ivBlurBackground.setRenderEffect(RenderEffect.createBlurEffect(30F, 30F, Shader.TileMode.MIRROR))
                viewHolder.card.setOnClickListener {
                    val bundle = bundleOf()
                    bundle.putString("game_id", games[position].id)
                    GlobalScope.launch {
                        User.addGamesToRecentlyViewed(games[position].id, historyDB)
                    }
                    viewHolder.itemView.findNavController().navigate(actionGameDetails.getAction(), bundle)
                }
            }
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = games.size

}