package com.cip.cipstudio.adapters

import android.content.Context
import android.graphics.RenderEffect
import android.graphics.Shader
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
import com.cip.cipstudio.dataSource.repository.HistoryRepository
import com.cip.cipstudio.dataSource.repository.historyRepositoryImpl.HistoryRepositoryLocal
import com.cip.cipstudio.model.User
import com.cip.cipstudio.model.data.GameDetails
import com.cip.cipstudio.utils.ActionGameDetailsEnum
import com.squareup.picasso.Picasso
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class GamesBigGridViewAdapter(val context : Context,
                              val games : List<GameDetails>,
                              private val navController: NavController,
                              private val actionAdapter: ActionGameDetailsEnum = ActionGameDetailsEnum.HISTORY) : BaseAdapter() {
    private var layoutInflater: LayoutInflater? = null
    private lateinit var tvGameNameBigCover : TextView
    private lateinit var ivBlurBackground : ImageView
    private lateinit var ivGameCoverForeground : ImageView
    private lateinit var tvGameGenres : TextView
    private lateinit var tvGameReleaseDate : TextView
    private lateinit var historyDB : HistoryRepository

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
            convertView = layoutInflater!!.inflate(R.layout.game_big_item, null)
        }

        tvGameNameBigCover = convertView!!.findViewById(R.id.i_recently_viewed_game_tv_GameNameBigCover)
        ivBlurBackground = convertView!!.findViewById(R.id.i_recently_viewed_game_iv_blur_background)
        ivGameCoverForeground = convertView!!.findViewById(R.id.i_recently_viewed_game_iv_game_cover_foreground)


        tvGameNameBigCover.text = games[position].name

        historyDB = HistoryRepositoryLocal(context)

        games[position].coverUrl.let {
            if(it.isNotEmpty() && it != "null") {
                Picasso.get().load(it).into(ivGameCoverForeground)
                Picasso.get().load(it).into(ivBlurBackground)
            }
            else {
                ivGameCoverForeground.setImageDrawable(context.getDrawable(R.drawable.ic_image_not_supported))
                ivGameCoverForeground.setBackgroundColor(context.getColor(R.color.primary_color))
                ivBlurBackground.setImageDrawable(context.getDrawable(R.drawable.ic_launcher_foreground))
                ivBlurBackground.setBackgroundColor(context.getColor(R.color.primary_color))
            }
            ivBlurBackground.setRenderEffect(RenderEffect.createBlurEffect(30F, 30F, Shader.TileMode.MIRROR))
            ivGameCoverForeground.setOnClickListener {
                val bundle = bundleOf()
                bundle.putString("game_id", games[position].id)
                GlobalScope.launch {
                    User.addGamesToRecentlyViewed(games[position].id, historyDB)
                }
                navController.navigate(actionAdapter.getAction(), bundle)
            }
        }


        return convertView
    }
}