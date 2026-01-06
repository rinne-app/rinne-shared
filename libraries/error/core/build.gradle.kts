import com.rinne.shared.extensions.rinneAndroid

plugins {
    alias(sharedLibs.plugins.rinne.multiplatform.library)
}

kotlin {
    rinneAndroid("com.rinne.libraries.error.core")
    sourceSets.commonMain.dependencies {
        implementation(projects.rinneShared.libraries.logger.core)
    }
}
