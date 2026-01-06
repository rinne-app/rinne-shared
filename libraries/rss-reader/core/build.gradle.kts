import com.rinne.shared.extensions.rinneAndroid

plugins {
    alias(sharedLibs.plugins.rinne.multiplatform.library)
}

kotlin {
    rinneAndroid("com.rinne.libraries.rss.reader.core")
    sourceSets.commonMain.dependencies {
        implementation(libs.rssparser)
    }
}
