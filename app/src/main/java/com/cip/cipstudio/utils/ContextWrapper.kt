package com.cip.cipstudio.utils

import android.content.Context
import java.util.*

class ContextWrapper {
    companion object {
        fun wrap(context: Context, language: String): Context {
            val locale = Locale(language)
            Locale.setDefault(locale)
            val config = context.resources.configuration
            config.setLocale(locale)
            return context.createConfigurationContext(config)
        }
    }
}