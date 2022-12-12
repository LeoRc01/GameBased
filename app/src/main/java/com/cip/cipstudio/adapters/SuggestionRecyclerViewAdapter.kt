package com.cip.cipstudio.adapters

import android.content.Context
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.cip.cipstudio.R
import com.cip.cipstudio.dataSource.repository.RecentSearchesRepository
import com.cip.cipstudio.dataSource.repository.historyRepositoryImpl.RecentSearchesRepositoryLocal
import com.cip.cipstudio.model.User
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class SuggestionRecyclerViewAdapter (val context : Context,
                                     val searchFunction: (String) -> Unit
) : RecyclerView.Adapter<SuggestionRecyclerViewAdapter.ViewHolder>() {

    private val queries : ArrayList<String> = arrayListOf()
    private val TAG = "RecentRecyclerViewAdapt"
    private lateinit var searchDB: RecentSearchesRepository

    private var isSuggestion: ArrayList<Boolean> = arrayListOf()

    fun addItems(queriesJson : List<String>, suggestion: Boolean = false){
        queries += queriesJson
        notifyItemInserted(queries.size - 1)

        for(query in queries)
            isSuggestion += suggestion

    }

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvQuery : TextView
        val btnDelete : TextView

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

        Log.i(TAG, isSuggestion.toString())

        if(isSuggestion[position]) {
            viewHolder.btnDelete.visibility = GONE

            viewHolder.tvQuery.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_search, 0, 0, 0)

            val primaryColor = ContextCompat.getColor(viewHolder.tvQuery.context, R.color.primary_color)

            viewHolder.tvQuery.compoundDrawables[0]?.setTint(primaryColor)

        }
        else {
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

    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = queries.size

}