package com.cip.cipstudio.view.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.activity.addCallback
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cip.cipstudio.R
import com.cip.cipstudio.adapters.GamesBigRecyclerViewAdapter
import com.cip.cipstudio.adapters.SuggestionRecyclerViewAdapter
import com.cip.cipstudio.dataSource.repository.recentSearchesRepository.RecentSearchesRepository
import com.cip.cipstudio.dataSource.repository.recentSearchesRepository.RecentSearchesRepositoryLocal
import com.cip.cipstudio.databinding.FragmentSearchBinding
import com.cip.cipstudio.model.User
import com.cip.cipstudio.utils.ActionGameDetailsEnum
import com.cip.cipstudio.viewmodel.SearchViewModel
import com.cip.cipstudio.dataSource.filter.Filter
import com.cip.cipstudio.utils.StateInstanceSaver
import kotlinx.coroutines.*

class SearchFragment : Fragment() {

    private lateinit var searchBinding: FragmentSearchBinding
    private lateinit var searchViewModel: SearchViewModel
    private val TAG = "SearchFragment"

    private lateinit var searchDB : RecentSearchesRepository
    private lateinit var filter: Filter

    private val tagIsSearch = "isSearch"
    private val tagOffsetResult = "OffsetResult"
    private val tagPositionResult = "PositionResult"

    private var resultsOffset : Int = 0
    private var recentOffset : Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        searchBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false)
        searchViewModel = SearchViewModel(searchBinding)
        searchBinding.executePendingBindings()
        searchBinding.lifecycleOwner = this

        filter = Filter(searchBinding.fSearchFlFilter,
                        searchViewModel.viewModelScope,
                        searchViewModel.isPageLoading,
                        layoutInflater,
                        resources,
                        searchBinding.drawerLayout)



        searchBinding.fSearchFilterButton.setOnClickListener {
            searchBinding.fSearchSearchBox.clearFocus()
            searchBinding.drawerLayout.openDrawer(GravityCompat.END)
        }



        return searchBinding.root

    }

    override fun onResume(){
        super.onResume()
        resultsOffset = 0
        searchBinding.fSearchSearchBox.clearFocus()
        val mapInstanceStateSaved = StateInstanceSaver.restoreState(TAG)
        filter.initializeFilters(mapInstanceStateSaved)
        val offsetStartResult = if (mapInstanceStateSaved != null && mapInstanceStateSaved.containsKey(tagOffsetResult))
            mapInstanceStateSaved[tagOffsetResult] as Int
            else
                0
        val positionStartResult = if (mapInstanceStateSaved != null && mapInstanceStateSaved.containsKey(tagPositionResult))
            mapInstanceStateSaved[tagPositionResult] as Int
            else
                0
        val isSearchStart = if (mapInstanceStateSaved != null && mapInstanceStateSaved.containsKey(tagIsSearch))
            mapInstanceStateSaved[tagIsSearch] as Boolean
            else
                false

        if (isSearchStart && searchBinding.fSearchSearchBox.query.isNotEmpty()){
            initializeResults(searchBinding.fSearchSearchBox.query.toString() ,offsetStartResult, positionStartResult)
        }
        else if (searchBinding.fSearchSearchBox.query.isNotEmpty())
            setSuggestions(searchBinding.fSearchSearchBox.query.toString())
        else
            showRecentSearches()

        initializeSearchView()
        initializeDrawer()
    }

    override fun onPause() {
        super.onPause()
        val map = filter.getMap() as HashMap<String, Any>
        map[tagOffsetResult] = resultsOffset
        val isSearch = searchBinding.fSearchResults.visibility == View.VISIBLE
        map[tagIsSearch] = isSearch
        if (isSearch)
            map[tagPositionResult] = (searchBinding.fSearchResults.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()
        StateInstanceSaver.saveState(TAG, map)
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
            else
                setVisible("fSearchHistory")
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

        initializeResults(query)
    }

    private fun setSuggestions(newText: String = "") {

        setVisible("fSearchHistory")

        getRecent(newText)
    }

    private fun searchRecent(query: String) {

        // set the query as the search text and run a search with it
        searchBinding.fSearchSearchBox.setQuery(query, false)
        searchBinding.fSearchSearchBox.clearFocus()
        searchResults(query)

    }

    private fun initializeResults(query: String, offsetStart: Int = 0, positionStartResult: Int = -1) {
        checkIfFragmentAttached {
            searchBinding.fSearchResults.visibility = View.GONE
            val shimmerLayout = searchBinding.fSearchShimmerLayoutResults
            if (query.isNotEmpty()) {
                Log.d(TAG, "initializeResults: $query")
                shimmerLayout.visibility = View.VISIBLE
                shimmerLayout.startShimmer()
            }

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

            searchViewModel.addGameResults(resultsOffset, query, filter) {
                adapter.addItems(it)

                if (adapter.itemCount == 0 && query.isNotEmpty()) {
                    shimmerLayout.stopShimmer()
                    shimmerLayout.visibility = View.GONE
                    setVisible("fSearchNotFound")
                }
                else
                    if (positionStartResult != -1)
                        initializeStartResult(offsetStart, positionStartResult)
                    else {
                        shimmerLayout.stopShimmer()
                        shimmerLayout.visibility = View.GONE
                        searchBinding.fSearchResults.visibility = View.VISIBLE
                    }
            }
        }

        searchBinding.fSearchResults.clearOnScrollListeners()

        searchBinding.fSearchResults.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1) && searchViewModel.isPageLoading.value == false) {
                    resultsOffset++
                    searchViewModel.addGameResults(resultsOffset, query, filter) { games ->
                        (searchBinding.fSearchResults.adapter as GamesBigRecyclerViewAdapter).addItems(games)
                    }

                }
            }
        })

    }

    private fun getRecent(
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

        searchViewModel.addSearchSuggestions(recentOffset, query, searchDB, filter.getFilterCriteria()) { suggestionList ->
            adapter.addItems(suggestionList, true)
            if(adapter.itemCount == 0)
                setVisible("fSearchNoSuggestions")

        }

        searchBinding.fSearchHistory.clearOnScrollListeners()

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

    private fun initializeDrawer() {
        searchBinding.drawerLayout.addDrawerListener(object : androidx.drawerlayout.widget.DrawerLayout.DrawerListener {
            override fun onDrawerStateChanged(newState: Int) {
            }

            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
            }

            override fun onDrawerClosed(drawerView: View) {
                filter.buildFilterContainer()
                searchBinding.fSearchNotFound.visibility = View.GONE
                if (searchBinding.fSearchSearchBox.query.isNotEmpty())
                    searchResults(searchBinding.fSearchSearchBox.query.toString())
            }

            override fun onDrawerOpened(drawerView: View) {

            }
        })

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (searchBinding.drawerLayout.isDrawerOpen(GravityCompat.END)) {
                searchBinding.drawerLayout.closeDrawer(GravityCompat.END)
            } else {
                isEnabled = false
                requireActivity().onBackPressed()
            }
        }
    }

    private fun initializeStartResult(offsetStartResult: Int, positionStartResult: Int) {
        if (offsetStartResult  != 0 && offsetStartResult >= resultsOffset) {
            resultsOffset++
            searchViewModel.addGameResults(resultsOffset, searchBinding.fSearchSearchBox.query.toString(), filter){ games ->
                (searchBinding.fSearchResults.adapter as GamesBigRecyclerViewAdapter)
                    .addItems(games)
                initializeStartResult(offsetStartResult, positionStartResult)
            }
        }
        else {

            if (searchBinding.fSearchSearchBox.query.isNotEmpty()) {
                searchBinding.fSearchShimmerLayoutResults.visibility = View.GONE
                searchBinding.fSearchShimmerLayoutResults.stopShimmer()
                setVisible("fSearchResults")
                searchBinding.fSearchResults.scrollToPosition(positionStartResult)
            }else{
                searchBinding.fSearchShimmerLayoutResults.visibility = View.GONE
                searchBinding.fSearchShimmerLayoutResults.stopShimmer()
                setVisible("fSearchHistory")
            }



        }
    }

    private fun checkIfFragmentAttached(operation: Context.() -> Unit) {
        if (isAdded && context != null) {
            operation(requireContext())
        }
    }

}