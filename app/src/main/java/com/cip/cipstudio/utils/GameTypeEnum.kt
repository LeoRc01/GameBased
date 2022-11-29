package com.cip.cipstudio.utils

import com.cip.cipstudio.R

enum class GameTypeEnum {
    MOST_HYPED {
        override fun getName() = R.string.most_hyped_games
              },
    MOST_RATED {
        override fun getName() = R.string.most_rated_games
              },
    MOST_POPULAR {
        override fun getName() = R.string.most_popular_games
              },
    RECENTLY_RELEASED {
        override fun getName() = R.string.recently_released
              },
    UPCOMING {
        override fun getName() = R.string.coming_soon
              },
    WORST_RATED {
        override fun getName() = R.string.worst_rated
              },
    LOVED_BY_CRITICS {
        override fun getName() = R.string.loved_by_critics
              },
    BEST_RATED {
        override fun getName() = R.string.best_rated
              };

    abstract fun getName() : Int
}