plugins {
    alias(libs.plugins.rinne.multiplatform.library)
}


android {
    namespace = "com.rinne.libraries.book.reader.core"
}

kotlin {
    sourceSets.commonMain.dependencies {}
}
