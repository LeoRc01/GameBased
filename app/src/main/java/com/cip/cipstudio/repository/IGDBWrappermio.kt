package com.cip.cipstudio.repository

import android.util.Log
import com.api.igdb.apicalypse.APICalypse
import com.api.igdb.apicalypse.Sort
import com.api.igdb.exceptions.RequestException
import com.api.igdb.request.IGDBWrapper
import com.api.igdb.request.TwitchAuthenticator
import com.api.igdb.request.games
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.google.protobuf.GeneratedMessageLite

object IGDBWrappermio {

    private val TAG= "IGDBWrapper"
    private val CLIENT_ID = "fyniflwh4wnvh1ww0i139mwolan1g5"
    private val CLIENT_SECRET = "4ot4bg1eqxvb2syuko6cewv5ccsn6s"

   suspend fun init() = withContext(Dispatchers.IO) {
       val token = TwitchAuthenticator.requestTwitchToken(CLIENT_ID, CLIENT_SECRET)
       Log.i(TAG, "Token: $token")
       IGDBWrapper.setCredentials(CLIENT_ID, token?.access_token.toString())
   }

    suspend fun games() = withContext(Dispatchers.IO) {
       val apicalypse = APICalypse().fields("*").sort("release_dates.date", Sort.DESCENDING)
       try{
           IGDBWrapper.games(apicalypse)
           Log.w(TAG, "games: ${IGDBWrapper.games(apicalypse)}")
       } catch(e: RequestException) {
           // Do something or error
       }
   }
}