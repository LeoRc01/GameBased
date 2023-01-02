package com.cip.cipstudio.adapters

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import com.cip.cipstudio.R
import com.cip.cipstudio.dataSource.repository.historyRepository.HistoryRepositoryLocal
import com.cip.cipstudio.model.User
import com.cip.cipstudio.model.data.GameDetails
import com.cip.cipstudio.utils.ActionGameDetailsEnum
import com.squareup.picasso.Picasso
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class FavouriteGridViewAdapter(val context : Context,
                               val games : ArrayList<GameDetails>,
                               private val navController: NavController,
                               private val actionAdapter: ActionGameDetailsEnum = ActionGameDetailsEnum.FAVOURITE_PAGE,
                               private val saveToHistory: Boolean = false) : BaseAdapter() {

    private var layoutInflater: LayoutInflater? = null
    private lateinit var tvGameTitle : TextView
    private lateinit var ivGameCover : ImageView


    fun addMoreGames(items : List<GameDetails>){
        games.addAll(items)
        notifyDataSetChanged()
    }

    override fun getCount(): Int {
        return games.size
    }

    override fun getItem(position: Int): Any {
        return games[position];
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun getView(position: Int, convertViewParam: View?, parent: ViewGroup?): View {
        var convertView = convertViewParam

        if (layoutInflater == null) {
            layoutInflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        }

        if (convertView == null) {
            convertView = layoutInflater!!.inflate(R.layout.game_item, null)
        }

        tvGameTitle = convertView!!.findViewById(R.id.i_game_tv_game_name)
        ivGameCover = convertView!!.findViewById(R.id.i_game_iv_game_cover)

        tvGameTitle.text = games[position].name

        val historyDB = HistoryRepositoryLocal(context)

        ivGameCover.setOnClickListener {
            if (saveToHistory) {
                GlobalScope.launch {
                    User.addGamesToRecentlyViewed(games[position].id, historyDB)
                }
            }
            val bundle = bundleOf()
            bundle.putString("game_id", games[position].id)
            navController.navigate(actionAdapter.getAction(), bundle)
        }

        games[position].coverUrl.let {
            if(!it.isEmpty() && it != "null") {
                Picasso.get().load(it).into(ivGameCover)
            }
            else{
                val noPreview = convertView.findViewById<ImageView>(R.id.i_game_iv_no_preview)
                noPreview.visibility = View.VISIBLE
            }

        }

        return convertView
    }
}