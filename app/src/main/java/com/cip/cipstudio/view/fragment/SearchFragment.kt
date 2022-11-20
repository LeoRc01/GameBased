package com.cip.cipstudio.view.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.cip.cipstudio.R
import com.cip.cipstudio.databinding.FragmentSearchBinding
import com.cip.cipstudio.model.data.GameDetails
import com.cip.cipstudio.model.data.Loading
import com.cip.cipstudio.repository.IGDBRepositoryRemote
import com.cip.cipstudio.repository.MyFirebaseRepository
import com.cip.cipstudio.utils.IsFromFragmentEnum
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

        searchBinding.fSearchButton.setOnClickListener {
            search()
        }

        return searchBinding.root
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
        bundle.putString("origin_fragment", IsFromFragmentEnum.SEARCH.name)

        findNavController().navigate(R.id.action_search_to_gameDetailsFragment4, bundle)

        onSuccess.invoke()
    }
}