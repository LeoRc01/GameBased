package com.cip.cipstudio.utils

import com.cip.cipstudio.R

enum class IsFromFragmentEnum {
    HOME {
        override fun getFragmentAction() = R.id.action_gameDetailsFragment2_self
    },
    SEARCH {
           override fun getFragmentAction() = R.id.action_gameDetailsFragment4_self
    },
    FAVORITES {
              override fun getFragmentAction() = R.id.action_gameDetailsFragment3_self
    },
    MAIN_PAGE {
        override fun getFragmentAction() = R.id.action_menu_home_to_gameDetailsFragment2
    },
    DIALOG {
        override fun getFragmentAction() = R.id.action_platformDetailsDialog_to_gameDetailsFragment2
    };


    abstract fun getFragmentAction() : Int
}