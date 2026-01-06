import com.rinne.shared.extensions.rinneAndroid

plugins {
    alias(sharedLibs.plugins.rinne.multiplatform.library)
}

kotlin {
    rinneAndroid("com.rinne.shared.memorizer")
    sourceSets.commonMain.dependencies {
        implementation(projects.rinneShared.libraries.dateTime.core)
    }
}
