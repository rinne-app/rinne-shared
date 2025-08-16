plugins {
    alias(libs.plugins.rinne.multiplatform.library)
}


android {
    namespace = "com.rinne.libraries.rss.reader.core"
}

kotlin {
    sourceSets.commonMain.dependencies {
        implementation(libs.rssparser)
    }
}
