package com.cip.cipstudio.view.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cip.cipstudio.R
import com.cip.cipstudio.repository.IGDBWrappermio
import kotlinx.coroutines.*
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

        val l = getProva()
        Log.w(TAG, "onCreateView: $l" )
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    // runBlocking is a coroutine scope that runs a new
    // coroutine and blocks the current thread interruptibly until its completion.
    // TODO: informarsi meglio sulle coroutine
    // TODO: capire errore InvocationTargetException
    // TODO: provare a togliere il suspend, magari è quello il problema per l'errore InvocationTargetException

    // InvocationTargetException:
    // nel mio caso c'era la chiamata IGDWrappermio.getPlatform qua dentro
    private fun getGames() = runBlocking {
        val games = IGDBWrappermio.games().forEach {
            val name = getPlatform(it).getOrNull(0)?.name
            Log.d(TAG, "getPlatform name: ${name}")
        }
        Log.i(TAG, "Games: $games")
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