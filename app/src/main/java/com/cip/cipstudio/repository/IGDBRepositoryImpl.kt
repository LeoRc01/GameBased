package com.cip.cipstudio.repository

import com.api.igdb.apicalypse.APICalypse
import proto.*

object IGDBRepositoryImpl : IGDBRepository {
    override fun getGames(payload: APICalypse): List<Game> {
        TODO("Not yet implemented")
    }

    override fun getGamePlatforms(game: Game): List<Platform> {
        TODO("Not yet implemented")
    }

    override fun getGameScreenshots(game: Game): List<Screenshot> {
        TODO("Not yet implemented")
    }

    override fun getGameGenres(game: Game): List<Genre> {
        TODO("Not yet implemented")
    }

    override fun getGameCover(game: Game): Cover {
        TODO("Not yet implemented")
    }

    override fun getPlatforms(): List<Platform> {
        TODO("Not yet implemented")
    }

    override fun getGenres(): List<Genre> {
        TODO("Not yet implemented")
    }
}