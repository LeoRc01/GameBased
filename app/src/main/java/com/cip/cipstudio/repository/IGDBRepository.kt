package com.cip.cipstudio.repository

import com.api.igdb.apicalypse.APICalypse
import proto.Cover
import proto.Game
import proto.Genre
import proto.Platform
import proto.Screenshot

interface IGDBRepository {

    fun getGames(payload: APICalypse) : List<Game>

    // sistemabile con un solo metodo usando le multyquery
    fun getGamePlatforms(game: Game) : List<Platform>

    fun getGameScreenshots(game: Game) : List<Screenshot>

    fun getGameGenres(game: Game) : List<Genre>

    fun getGameCover(game: Game) : Cover

    fun getPlatforms() : List<Platform>

    fun getGenres() : List<Genre>

}