package com.rinne.libraries.locale.core

sealed interface RinneLocale {
    val code: String
    val languageCode: String get() = code

    sealed interface Known : RinneLocale

    data object Unspecified : Known {
        override val code: String = "-"
    }

    data object English : Known {
        override val code: String = "en"
    }

    data object Bulgarian : Known {
        override val code: String = "bg"
    }

    data object Russian : Known {
        override val code: String = "ru"
    }

    data object Chinese : Known {
        override val code: String = "zh"
    }

    data object Ukrainian : Known {
        override val code: String = "ua"
    }

    data object Spanish : Known {
        override val code: String = "es"
    }

    data object German : Known {
        override val code: String = "de"
    }

    data class Custom(
        override val code: String,
        override val languageCode: String = code
    ) : RinneLocale
}
