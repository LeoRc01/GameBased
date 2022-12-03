package com.cip.cipstudio.view.fragment

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.widget.SearchView
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cip.cipstudio.R
import com.cip.cipstudio.adapters.GamesBigRecyclerViewAdapter
import com.cip.cipstudio.databinding.FragmentSearchBinding
import com.cip.cipstudio.utils.ActionGameDetailsEnum
import com.cip.cipstudio.utils.AuthTypeErrorEnum
import com.cip.cipstudio.utils.GameTypeEnum
import com.cip.cipstudio.view.MainActivity
import com.cip.cipstudio.viewmodel.SearchViewModel
import com.facebook.shimmer.ShimmerFrameLayout
import kotlinx.coroutines.*

class SearchFragment : Fragment() {
    /*private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
    }*/
    private lateinit var searchBinding: FragmentSearchBinding
    private lateinit var searchViewModel: SearchViewModel
    private val TAG = "SearchFragment"
    private lateinit var sharedPreferences: SharedPreferences

    private var offset : Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        searchBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false)

        searchViewModel = SearchViewModel(searchBinding)

        initializeSearchView()

        searchBinding.executePendingBindings()
        searchBinding.lifecycleOwner = this

        // roba che fa avviata la gui

        sharedPreferences = searchBinding.root.context.getSharedPreferences(
            getString(R.string.setting_preferences),
            AppCompatActivity.MODE_PRIVATE
        )



        return searchBinding.root
    }


    private fun initializeSearchView() {

        searchBinding.fSearchSearchBox.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {

                // roba che fa avviata la ricerca
                initializeSearchResultsList(query)
                searchBinding.fSearchBg.visibility = View.GONE

                return false
            }

        })

    }

    private fun initializeSearchResultsList(query: String) {
        // SearchResults
        initializeRecyclerView(
            query,
            searchBinding.fSearchResults,
            searchBinding.fSearchShimmerLayoutResults
        )
    }

    // senza shimmer, non posso usare la stessa funzione, da vedere dopo
    /*private fun initializeSearchHistoryList() {
        // SearchHistory
        initializeRecyclerView(
            searchBinding.fSearchHistoryList,
        )
    }*/

    private fun initializeRecyclerView(
        query: String,
        recyclerView: RecyclerView,
        shimmerLayout: ShimmerFrameLayout
    ) {
        shimmerLayout.visibility = View.VISIBLE
        shimmerLayout.startShimmer()
        val linearLayoutManager = LinearLayoutManager(requireContext())
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL

        val adapter = GamesBigRecyclerViewAdapter(
            requireContext(),
            ArrayList(),
            ActionGameDetailsEnum.SEARCH
        )

        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = adapter
        recyclerView.itemAnimator = null
        recyclerView.setItemViewCacheSize(50)

        // chiamata iniziale

        searchViewModel.addGameResults(offset, query) {
            adapter.addItems(it)
            shimmerLayout.stopShimmer()
            shimmerLayout.visibility = View.GONE
        }

        offset = 0

        // setta l'onscroll listener

        searchBinding.fSearchResults.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1) && searchViewModel.isPageLoading.value == false) {
                    offset++
                    Log.i(TAG, "OFFSET")
                    Log.i(TAG, offset.toString())
                    searchViewModel.addGameResults(offset, query) { games ->
                        (searchBinding.fSearchResults.adapter as GamesBigRecyclerViewAdapter).addItems(games)
                        Log.i(TAG, games.toString())
                    }
                    Log.i(TAG, "onScrollStateChanged")

                }
            }
        })
    }

}