
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
        val tvGameGenres : TextView
        val tvGameReleaseDate : TextView
        val tvGameRating : TextView
        val ivStar : ImageView

        init {
            ivBlurBackground = view.findViewById(R.id.i_recently_viewed_game_iv_blur_background)
            ivGameCoverForeground = view.findViewById(R.id.i_recently_viewed_game_iv_game_cover_foreground)
            tvGameNameBigCover = view.findViewById(R.id.i_recently_viewed_game_tv_GameNameBigCover)
            tvGameGenres = view.findViewById(R.id.i_recently_viewed_game_tv_GameGenresBigCover)
            tvGameReleaseDate = view.findViewById(R.id.i_recently_viewed_game_tv_GameReleaseDateBigCover)
            tvGameRating = view.findViewById(R.id.i_recently_viewed_game_rating)
            card = view.findViewById(R.id.i_recently_viewed_game_cover)
            ivStar = view.findViewById(R.id.i_recently_viewed_game_star)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.game_big_item, viewGroup, false)
        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        historyDB = HistoryRepositoryLocal(viewHolder.itemView.context)

        viewHolder.tvGameNameBigCover.text = games[position].name

        viewHolder.tvGameGenres.text = (games[position].genres.map {
                                            it.getString("name")
                                        } as ArrayList<String>).joinToString(", ")
        viewHolder.tvGameReleaseDate.text = games[position].releaseDate

        val rating : Double = games[position].totalRating.toInt().toDouble() / 10
        if(rating.equals(0.0)){
            viewHolder.tvGameRating.text = "N/A"
            viewHolder.ivStar.setImageDrawable(context.getDrawable(R.drawable.ic_star_border))
        }else{
            viewHolder.tvGameRating.text = rating.toString()
        }


        games[position].coverUrl.let {
            if(!it.isEmpty() && it != "null") {
                Picasso.get().load(it).into(viewHolder.ivGameCoverForeground)
                Picasso.get().load(it).into(viewHolder.ivBlurBackground)
            }
            else {
                viewHolder.ivGameCoverForeground.setImageDrawable(context.getDrawable(R.drawable.ic_image_not_supported))
                viewHolder.ivGameCoverForeground.setBackgroundColor(context.getColor(R.color.primary_color))
                viewHolder.ivGameCoverForeground.scaleType = ImageView.ScaleType.CENTER
                viewHolder.ivBlurBackground.setImageDrawable(context.getDrawable(R.drawable.fading_primary))
                viewHolder.ivBlurBackground.setBackgroundColor(context.getColor(R.color.primary_color))
            }
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

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = games.size

}