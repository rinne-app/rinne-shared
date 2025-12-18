import com.rinne.extensions.rinneAndroid

plugins {
    alias(libs.plugins.rinne.multiplatform.library)
    alias(libs.plugins.rinne.multiplatform.compose)
}


rinneAndroid("com.rinne.libraries.highlights")

kotlin {
    sourceSets.commonMain.dependencies {
        implementation(projects.rinneShared.libraries.text.core)
    }
}
