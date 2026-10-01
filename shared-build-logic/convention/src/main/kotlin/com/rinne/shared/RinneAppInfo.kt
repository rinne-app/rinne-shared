package com.rinne.shared

object RinneAppInfo {

    object Android {
        const val minSdk = 30
        const val targetSdk = 35

        /**
         * Compose Multiplatform 1.12 requires compiling against API 37, independently of
         * [targetSdk], which is what opts the app into new runtime behaviour.
         */
        const val compileSdk = 37
        const val applicationId = "com.rinne"
    }

    object Ios {
        const val name = "RinneShared"
    }
}