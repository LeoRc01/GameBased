package com.cip.cipstudio.view.fragment

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
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
import com.cip.cipstudio.adapters.GamesRecyclerViewAdapter
import com.cip.cipstudio.databinding.FragmentSearchBinding
import com.cip.cipstudio.utils.ActionGameDetailsEnum
import com.cip.cipstudio.utils.AuthTypeErrorEnum
import com.cip.cipstudio.utils.GameTypeEnum
import com.cip.cipstudio.view.MainActivity
import com.cip.cipstudio.view.widgets.LoadingSpinner
import com.cip.cipstudio.viewmodel.SearchViewModel
import com.facebook.shimmer.ShimmerFrameLayout
import kotlinx.coroutines.*

class SearchFragment : Fragment() {
    private val coroutineExceptionHandler = CoroutineExceptionHandler{ _, throwable ->
        throwable.printStackTrace()
    }
    private lateinit var searchBinding: FragmentSearchBinding
    private lateinit var searchViewModel: SearchViewModel
    private val TAG = "SearchFragment"
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        searchBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false)

        searchViewModel = SearchViewModel(searchBinding)
        searchBinding.vm = searchViewModel
        searchBinding.lifecycleOwner = this

        // roba che fa avviata la gui

        sharedPreferences = searchBinding.root.context.getSharedPreferences(
            getString(R.string.setting_preferences),
            AppCompatActivity.MODE_PRIVATE)

        initializeSearchView()
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

                LoadingSpinner.showLoadingDialog(requireContext())
                initializeSearchResultsList()
                LoadingSpinner.dismiss()

                /*searchViewModel.search(onSuccess = {

                },
                    onFailure = {

                    }
                )*/

                return false
            }

        })

    }

    private fun initializeSearchResultsList(refresh: Boolean = false) {
        // SearchResults
        initializeRecyclerView(
            searchBinding.fSearchResults,
            searchBinding.fSearchShimmerLayoutResults,
            refresh
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
        recyclerView: RecyclerView,
        shimmerLayout: ShimmerFrameLayout,
        refresh: Boolean
    ) {
        shimmerLayout.startShimmer()
        val linearLayoutManager = LinearLayoutManager(requireContext())
        linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL

        val adapter = GamesRecyclerViewAdapter(
            ArrayList(),
            ActionGameDetailsEnum.MAIN_PAGE
        )

        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = adapter
        recyclerView.itemAnimator = null
        recyclerView.setItemViewCacheSize(50)

        searchViewModel.initializeRecyclerView(refresh) {
            adapter.importItems(it)
            shimmerLayout.stopShimmer()
            shimmerLayout.visibility = View.GONE
        }
    }


        /*

        searchBinding.fLoginBtnLogin.setOnClickListener {
            LoadingSpinner.showLoadingDialog(requireContext())
            loginBinding.fLoginLayoutEmail.error = ""
            loginBinding.fLoginLayoutPwd.error = ""
            loginViewModel
                .login(onSuccess = {
                    LoadingSpinner.dismiss()
                    val intent = Intent(requireContext(), MainActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                },
                    onFailure = {
                        LoadingSpinner.dismiss()
                        when(it.getErrorType()){
                            AuthTypeErrorEnum.EMAIL -> {
                                loginBinding.fLoginLayoutEmail.error = getString(it.getErrorId())
                            }
                            AuthTypeErrorEnum.PASSWORD -> {
                                loginBinding.fLoginLayoutPwd.error = getString(it.getErrorId())
                            }
                            AuthTypeErrorEnum.UNKNOWN -> {
                                Toast.makeText(context, getString(it.getErrorId()), Toast.LENGTH_SHORT).show()
                            }
                            else -> {
                                Toast.makeText(context, getString(R.string.internal_error), Toast.LENGTH_SHORT).show()
                            }
                        }
                    })
        }
    }

    private fun search () {

        LoadingSpinner.showLoadingDialog(requireContext())

        getGames {
            LoadingSpinner.dismiss()
        }
    }

    // runBlocking is a coroutine scope that runs a new
    // coroutine and blocks the current thread interruptibly until its completion.
    // TODO: informarsi meglio sulle coroutine
    // TODO: capire errore InvocationTargetException

    // InvocationTargetException:
    // nel mio caso c'era la chiamata IGDWrappermio.getPlatform qua dentro
    private fun getGames(onSuccess: () -> Unit) {

        val bundle = bundleOf()
        bundle.putString("game_id", "2058")

        findNavController().navigate(R.id.action_searchScreen_to_game_details_search, bundle)

        onSuccess.invoke()
    }

         */
}