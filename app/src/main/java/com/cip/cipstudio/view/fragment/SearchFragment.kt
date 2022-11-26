package com.cip.cipstudio.view.fragment

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.cip.cipstudio.R
import com.cip.cipstudio.databinding.FragmentSearchBinding
import com.cip.cipstudio.utils.ActionGameDetailsEnum
import com.cip.cipstudio.view.widgets.LoadingSpinner
import com.cip.cipstudio.viewmodel.SearchViewModel
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

        sharedPreferences = searchBinding.root.context.getSharedPreferences(
            getString(R.string.setting_preferences),
            AppCompatActivity.MODE_PRIVATE)

        searchBinding.fSearchButton.setOnClickListener {
            search()
        }

        searchBinding.fSearchBtnTestGuest.setOnClickListener {
            if (sharedPreferences.contains(getString(R.string.guest_settings)))
                sharedPreferences.edit().remove(getString(R.string.guest_settings)).apply()
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

        findNavController().navigate(R.id.action_searchScreen_to_game_details_search, bundle)

        onSuccess.invoke()
    }
}