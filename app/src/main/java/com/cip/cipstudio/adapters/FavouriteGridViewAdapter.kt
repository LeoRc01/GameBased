package com.cip.cipstudio.adapters

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import com.cip.cipstudio.R
import com.squareup.picasso.Picasso
import com.cip.cipstudio.model.data.Game

class FavouriteGridViewAdapter(val context : Context, val games : ArrayList<Game>) : BaseAdapter() {

    private var layoutInflater: LayoutInflater? = null
    private lateinit var tvGameTitle : TextView
    private lateinit var ivGameCover : ImageView


    override fun getCount(): Int {
        return games.size
    }

    override fun getItem(position: Int): Any {
        return games[position];
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var convertView = convertView
        // on blow line we are checking if layout inflater
        // is null, if it is null we are initializing it.
        if (layoutInflater == null) {
            layoutInflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        }
        // on the below line we are checking if convert view is null.
        // If it is null we are initializing it.
        if (convertView == null) {
            // on below line we are passing the layout file
            // which we have to inflate for each item of grid view.
            convertView = layoutInflater!!.inflate(R.layout.game_item, null)
        }

        tvGameTitle = convertView!!.findViewById(R.id.tvGameName)
        ivGameCover = convertView!!.findViewById(R.id.ivGameCover)

        tvGameTitle.text = games[position].name

        games[position].getCover{
            val uiHandler = Handler(Looper.getMainLooper())
            uiHandler.post(Runnable {
                if(it!="NO_COVER") {
                    Picasso.get().load("https:${it}").into(ivGameCover)
                    ivGameCover.setOnClickListener {

                        //val bundle = bundleOf("game" to games[position])

                        //convertView!!.findNavController().navigate(action, bundle)

                    }
                }
                else{
                    convertView!!.findViewById<ImageView>(R.id.ivNoPreview).visibility = View.VISIBLE
                }
            })
        }

        return convertView
    }
}