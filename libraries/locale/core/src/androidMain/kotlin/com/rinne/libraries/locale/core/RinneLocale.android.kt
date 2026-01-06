package com.rinne.libraries.locale.core

import java.util.Locale

fun RinneLocale.asAndroidLocale(): Locale = when (this) {
    RinneLocale.Bulgarian -> Locale.forLanguageTag(languageCode)
    RinneLocale.Chinese -> Locale.CHINESE
    RinneLocale.English -> Locale.ENGLISH
    RinneLocale.German -> Locale.GERMAN
    else -> Locale.forLanguageTag(languageCode)
}