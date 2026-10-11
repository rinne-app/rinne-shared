import com.rinne.shared.extensions.rinneAndroid

plugins {
    alias(sharedLibs.plugins.rinne.multiplatform.library)
    alias(sharedLibs.plugins.rinne.multiplatform.kotlin.serialization)
    id("rinne.shared.strings")
}

// The string tables (tables/*.json) are the one source of every user-facing text: the clients ship
// them as their base layer and the backend serves them for runtime updates. This module parses and
// validates them and holds the keys, signatures and plural rules generated from them.
kotlin {
    rinneAndroid("com.rinne.shared.strings")
    sourceSets.commonMain.dependencies {
        api(sharedLibs.kotlinx.serialization.json)
    }
}
