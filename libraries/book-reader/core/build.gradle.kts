import com.rinne.extensions.rinneAndroid

plugins {
    alias(libs.plugins.rinne.multiplatform.library)
}

rinneAndroid("com.rinne.libraries.book.reader.core")

kotlin {
    sourceSets.commonMain.dependencies {}
}
