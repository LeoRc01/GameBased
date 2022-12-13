package com.cip.cipstudio.view.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cip.cipstudio.R
import com.cip.cipstudio.adapters.GamesBigRecyclerViewAdapter
import com.cip.cipstudio.adapters.SuggestionRecyclerViewAdapter
import com.cip.cipstudio.dataSource.repository.RecentSearchesRepository
import com.cip.cipstudio.dataSource.repository.historyRepositoryImpl.RecentSearchesRepositoryLocal
import com.cip.cipstudio.databinding.FragmentSearchBinding
import com.cip.cipstudio.model.User
import com.cip.cipstudio.utils.ActionGameDetailsEnum
import com.cip.cipstudio.viewmodel.SearchViewModel
import com.facebook.shimmer.ShimmerFrameLayout
import kotlinx.coroutines.*

class SearchFragment : Fragment() {

    private lateinit var searchBinding: FragmentSearchBinding
    private lateinit var searchViewModel: SearchViewModel
    private val TAG = "SearchFragment"

    private lateinit var searchDB : RecentSearchesRepository

    private var resultsOffset : Int = 0
    private var recentOffset : Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        searchBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false)

        searchViewModel = SearchViewModel(searchBinding)

        searchBinding.executePendingBindings()
        searchBinding.lifecycleOwner = this


        showRecentSearches()


        initializeSearchView()

        return searchBinding.root

    }

    private fun setVisible(widgetId: String){
        searchBinding.fSearchBg.visibility = View.GONE
        searchBinding.fSearchHistory.visibility = View.GONE
        searchBinding.fSearchResults.visibility = View.GONE
        searchBinding.fSearchNotFound.visibility = View.GONE
        searchBinding.fSearchNoSuggestions.visibility = View.GONE

        when(widgetId) {
            "fSearchBg" -> { searchBinding.fSearchBg.visibility = View.VISIBLE }
            "fSearchHistory" -> { searchBinding.fSearchHistory.visibility = View.VISIBLE }
            "fSearchResults" -> { searchBinding.fSearchResults.visibility = View.VISIBLE }
            "fSearchNotFound" -> { searchBinding.fSearchNotFound.visibility = View.VISIBLE }
            "fSearchNoSuggestions" -> { searchBinding.fSearchNoSuggestions.visibility = View.VISIBLE }
            else -> { }
        }
    }


    private fun initializeSearchView() {

        searchBinding.fSearchSearchBox.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {

            override fun onQueryTextChange(newText: String): Boolean {
                if (newText.isNotBlank()){
                    setSuggestions(newText)
                }else{
                    showRecentSearches()
                }

                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                searchResults(query)
                return false
            }


        })

    }

    private fun showRecentSearches() {
        setVisible("fSearchHistory")
        val recyclerView = searchBinding.fSearchHistory
        val linearLayoutManager = LinearLayoutManager(requireContext())
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL

        val adapter = SuggestionRecyclerViewAdapter(
            requireContext(),
            ::searchRecent
        )


        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = adapter
        recyclerView.itemAnimator = null
        recyclerView.setItemViewCacheSize(50)

        searchDB = RecentSearchesRepositoryLocal(requireContext())

        recentOffset = 0


        searchViewModel.addRecentSearches(recentOffset, searchDB =  searchDB) { recentList ->
            adapter.addItems(recentList as ArrayList<String>)

            if (recentList.isEmpty())
                setVisible("fSearchBg")
        }

        searchBinding.fSearchResults.clearOnScrollListeners()

        searchBinding.fSearchHistory.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1) && searchViewModel.isPageLoading.value == false) {
                    recentOffset++
                    searchViewModel.addRecentSearches(recentOffset, "", searchDB) { queries ->
                        (searchBinding.fSearchHistory.adapter as SuggestionRecyclerViewAdapter).addItems(queries as ArrayList<String>)
                    }
                }
            }
        })
    }

    private fun searchResults(query: String) {

        setVisible("fSearchResults")

        searchDB = RecentSearchesRepositoryLocal(requireContext())

        lifecycleScope.launch {
            User.addSearchToRecentlySearched(query, searchDB)
        }

        initializeResultsRecyclerView(query)
    }

    private fun setSuggestions(newText: String = "") {

        setVisible("fSearchHistory")

        initializeRecentRecyclerView(newText)
    }

    private fun searchRecent(query: String) {

        // set the query as the search text and run a search with it
        searchBinding.fSearchSearchBox.setQuery(query, false)
        searchBinding.fSearchSearchBox.clearFocus()
        searchResults(query)

    }

    private fun initializeResultsRecyclerView(query: String) {
        val shimmerLayout = searchBinding.fSearchShimmerLayoutResults
        shimmerLayout.visibility = View.VISIBLE
        shimmerLayout.startShimmer()

        val adapter = GamesBigRecyclerViewAdapter(
            requireContext(),
            ArrayList(),
            ActionGameDetailsEnum.SEARCH
        )

        val linearLayoutManager = LinearLayoutManager(requireContext())
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL

        val recyclerView = searchBinding.fSearchResults
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = adapter
        recyclerView.itemAnimator = null
        recyclerView.setItemViewCacheSize(50)

        resultsOffset = 0

        searchViewModel.addGameResults(resultsOffset, query) {
            adapter.addItems(it)
            shimmerLayout.stopShimmer()
            shimmerLayout.visibility = View.GONE

            if(adapter.itemCount == 0)
                setVisible("fSearchNotFound")
        }

        searchBinding.fSearchResults.clearOnScrollListeners()

        searchBinding.fSearchResults.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1) && searchViewModel.isPageLoading.value == false) {
                    resultsOffset++
                    searchViewModel.addGameResults(resultsOffset, query) { games ->
                        (searchBinding.fSearchResults.adapter as GamesBigRecyclerViewAdapter).addItems(games)
                    }

                }
            }
        })

    }

    private fun initializeRecentRecyclerView(
        query: String
    ) {
        val recyclerView = searchBinding.fSearchHistory

        val adapter = SuggestionRecyclerViewAdapter(
            requireContext(),
            ::searchRecent
        )

        val linearLayoutManager = LinearLayoutManager(requireContext())
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL

        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = adapter
        recyclerView.itemAnimator = null
        recyclerView.setItemViewCacheSize(50)

        searchDB = RecentSearchesRepositoryLocal(requireContext())

        recentOffset = 0

        searchViewModel.addSearchSuggestions(recentOffset, query, searchDB) { suggestionList ->
            adapter.addItems(suggestionList, true)
            if(adapter.itemCount == 0)
                setVisible("fSearchNoSuggestions")

        }

        searchBinding.fSearchResults.clearOnScrollListeners()

        searchBinding.fSearchHistory.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1) && searchViewModel.isPageLoading.value == false) {
                    recentOffset++
                    searchViewModel.addRecentSearches(recentOffset, query, searchDB) { queries ->
                        (searchBinding.fSearchHistory.adapter as SuggestionRecyclerViewAdapter).addItems(queries as ArrayList<String>)
                    }

                }
            }
        })

    }

}