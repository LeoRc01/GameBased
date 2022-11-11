package com.cip.cipstudio.view.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import com.cip.cipstudio.R
import com.cip.cipstudio.databinding.FragmentSearchBinding
import com.cip.cipstudio.model.data.GameDetails
import com.cip.cipstudio.model.data.Loading
import com.cip.cipstudio.repository.IGDBRepositoryRemote
import com.cip.cipstudio.view.widgets.LoadingSpinner
import com.cip.cipstudio.viewmodel.FavouriteViewModel
import com.cip.cipstudio.viewmodel.SearchViewModel
import kotlinx.coroutines.*

class SearchFragment : Fragment() {
    private val coroutineExceptionHandler = CoroutineExceptionHandler{ _, throwable ->
        throwable.printStackTrace()
    }
    private lateinit var searchBinding: FragmentSearchBinding
    private lateinit var searchViewModel: SearchViewModel
    private val TAG = "SearchFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        searchBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false)

        searchViewModel = SearchViewModel(searchBinding)
        searchBinding.vm = searchViewModel
        searchBinding.lifecycleOwner = this

        return searchBinding.root
    }

    private fun search (view: View) {
        LoadingSpinner.showLoadingDialog(requireContext())

        getGames(view){
            LoadingSpinner.dismiss()
        }
    }

    // runBlocking is a coroutine scope that runs a new
    // coroutine and blocks the current thread interruptibly until its completion.
    // TODO: informarsi meglio sulle coroutine
    // TODO: capire errore InvocationTargetException

    // InvocationTargetException:
    // nel mio caso c'era la chiamata IGDWrappermio.getPlatform qua dentro
    private fun getGames(view : View, onSuccess: () -> Unit) {
        var game : GameDetails ?= null


        val a = lifecycleScope.launch(Dispatchers.Main) {
            game = IGDBRepositoryRemote.getGameDetails("143")
        }
        lifecycleScope.launch(Dispatchers.Main) {
            a.join()
            Log.i(TAG, "getGames: ${game?.id}")
            view.findViewById<TextView>(R.id.f_search_tv).text =
                "${game?.name}, ${game?.id}, ${game?.summary}, ${game?.coverUrl}"
            onSuccess.invoke()
        }

    }
}