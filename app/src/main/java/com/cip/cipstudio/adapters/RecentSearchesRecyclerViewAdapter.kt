package com.cip.cipstudio.adapters

import android.content.Context
import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.cip.cipstudio.R
import com.cip.cipstudio.dataSource.repository.RecentSearchesRepository
import com.cip.cipstudio.model.data.GameDetails
import com.cip.cipstudio.dataSource.repository.historyRepositoryImpl.RecentSearchesRepositoryLocal
import com.cip.cipstudio.model.User
import com.cip.cipstudio.utils.ActionGameDetailsEnum
import com.cip.cipstudio.view.fragment.SearchFragment
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class RecentSearchesRecyclerViewAdapter (val context : Context,
                                         private val queries : ArrayList<String>,
                                         val searchFunction: (String) -> Unit
) : RecyclerView.Adapter<RecentSearchesRecyclerViewAdapter.ViewHolder>() {

    private val TAG = "RecentRecyclerViewAdapt"
    private lateinit var searchDB: RecentSearchesRepository

    fun addItems(queriesJson : ArrayList<String>){
        queries += queriesJson
        notifyItemInserted(queries.size - 1)
    }

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvQuery : TextView
        val btnDelete : Button

        init {
            tvQuery = view.findViewById(R.id.i_search_history_query)
            btnDelete = view.findViewById(R.id.i_search_history_delete_query)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.search_history_item, viewGroup, false)
        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        searchDB = RecentSearchesRepositoryLocal(viewHolder.itemView.context)

        viewHolder.tvQuery.text = queries[position]

        viewHolder.tvQuery.setOnClickListener {
            searchFunction(queries[position])
        }

        viewHolder.btnDelete.setOnClickListener {
            val query = queries[position]

            GlobalScope.launch {
                User.delete(query, searchDB)
            }
            queries.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, queries.size);

        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = queries.size

}