package com.cip.cipstudio.adapters

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import androidx.navigation.NavController
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


class GamesRecyclerViewAdapter (var games : List<GameDetails>,
                                private val actionToFragment: ActionGameDetailsEnum = ActionGameDetailsEnum.SELF,
                                private val navController: NavController? = null) :
    RecyclerView.Adapter<GamesRecyclerViewAdapter.ViewHolder>() {

    private val TAG = "GamesRecyclerViewAdapt"
    private lateinit var historyDB: HistoryRepository


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
            tvGameName = view.findViewById(R.id.i_game_tv_game_name)
            ivGameCover = view.findViewById(R.id.i_game_iv_game_cover)
            ivNoPreview = view.findViewById(R.id.i_game_iv_no_preview)
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
        historyDB = HistoryRepositoryLocal(viewHolder.itemView.context)

        viewHolder.tvGameName.text = games[position].name
        games[position].coverUrl.let {
            val clickItem : ImageView
            if(it.isNotEmpty() && it != "null") {
                Picasso.get().load(it).into(viewHolder.ivGameCover)
                clickItem = viewHolder.ivGameCover
            } else {
                viewHolder.ivNoPreview.visibility = View.VISIBLE
                clickItem = viewHolder.ivNoPreview
            }
            clickItem.setOnClickListener {
                val bundle = bundleOf()
                bundle.putString("game_id", games[position].id)

                GlobalScope.launch {
                    User.addGamesToRecentlyViewed(games[position].id, historyDB)
                }

                if (navController != null) {
                    navController.navigate(actionToFragment.getAction(), bundle)
                }
                else {
                    viewHolder.itemView.findNavController()
                        .navigate(actionToFragment.getAction(), bundle)
                }
            }
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = games.size

}