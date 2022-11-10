package com.cip.cipstudio.view.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.cip.cipstudio.R
import com.cip.cipstudio.model.data.GameDetails
import com.cip.cipstudio.repository.IGDBRepositoryRemote
import com.cip.cipstudio.view.widgets.LoadingSpinner
import kotlinx.coroutines.*

class SearchFragment : Fragment() {
    private val coroutineExceptionHandler = CoroutineExceptionHandler{ _, throwable ->
        throwable.printStackTrace()
    }
    private val TAG = "SearchFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)
        view.findViewById<Button>(R.id.f_search_button).setOnClickListener { search(view) }


        // Inflate the layout for this fragment
        return view
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