package com.cip.cipstudio.utils

import com.cip.cipstudio.R

enum class ActionGameDetailsEnum {
    MAIN_PAGE {
        override fun getAction() = R.id.action_homeScreen_to_game_details_home
    },
    SEARCH_PAGE {
        override fun getAction() = R.id.action_searchScreen_to_game_details_search
    },
    FAVOURITE_PAGE {
        override fun getAction() = R.id.action_favouriteScreen_to_game_details_favourite
    },
    DIALOG {
        override fun getAction() = R.id.action_platformDetailsDialog_to_gameDetailsFragment
    },
    COLLECTION {
        override fun getAction() = R.id.action_collectionDialogFragment_to_gameDetailsFragment
    },
    SELF {
        override fun getAction() = R.id.action_gameDetailsFragment_self
    },
    GAME_LIST {
        override fun getAction() = R.id.action_gameListFragment_to_game_details_home
    };


    abstract fun getAction() : Int
}