package com.cip.cipstudio.dataSource.repository

import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.tasks.await

object ItalianEnglishTranslator {
    private val options = TranslatorOptions.Builder()
        .setSourceLanguage(TranslateLanguage.ENGLISH)
        .setTargetLanguage(TranslateLanguage.ITALIAN)
        .build()
    private val englishItalianTranslator = Translation.getClient(options)

    suspend fun translate(text: String): String {
        var conditions = DownloadConditions.Builder()
            .requireWifi()
            .build()
        englishItalianTranslator.downloadModelIfNeeded(conditions).await()
        return englishItalianTranslator.translate(text).await()
    }


}