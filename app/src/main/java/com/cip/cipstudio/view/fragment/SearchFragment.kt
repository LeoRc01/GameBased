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
import com.api.igdb.request.IGDBWrapper
import com.cip.cipstudio.R
import com.cip.cipstudio.repository.IGDBRepositoryRemote
import com.cip.cipstudio.repository.IGDBWrappermio
import com.cip.cipstudio.view.widgets.LoadingSpinner
import kotlinx.coroutines.*
import org.json.JSONArray
import proto.Game
import proto.Platform

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
        var games = JSONArray()
        //games = IGDBWrappermio.getGames()

        val a = lifecycleScope.launch(Dispatchers.Main) {
           games = IGDBRepositoryRemote.getGamesMostHyped()

        }
        lifecycleScope.launch(Dispatchers.Main) {
            a.join()
            Log.i(TAG, "getGames: ${games.length()}")
            view.findViewById<TextView>(R.id.f_search_tv).text = games.toString()
            onSuccess.invoke()
        }

    }

    private fun getPlatform(game : Game) = runBlocking {
        if (game.platformsList.isEmpty())
            return@runBlocking emptyList<Platform>()
        return@runBlocking IGDBWrappermio.platformsGames(game.platformsList[0].id.toString())
    }

    // InvocationTargetException:
    // nel mio caso c'era un log dentro a questa funzione e quello dava problemi
    private fun getProva() = runBlocking {
        return@runBlocking IGDBWrappermio.prova()
    }
}