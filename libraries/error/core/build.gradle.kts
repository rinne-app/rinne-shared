import com.rinne.extensions.rinneAndroid

plugins {
    alias(libs.plugins.rinne.multiplatform.library)
}


rinneAndroid("com.rinne.libraries.error.core")

kotlin {
    sourceSets.commonMain.dependencies {
        implementation(projects.rinneShared.libraries.logger.core)
    }
}
