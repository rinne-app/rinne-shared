import com.rinne.extensions.rinneAndroid

plugins {
    alias(libs.plugins.rinne.multiplatform.library)
}


rinneAndroid("com.rinne.libraries.rss.reader.core")

kotlin {
    sourceSets.commonMain.dependencies {
        implementation(libs.rssparser)
    }
}
