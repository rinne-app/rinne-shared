import com.rinne.shared.extensions.rinneAndroid

plugins {
    alias(sharedLibs.plugins.rinne.multiplatform.library)
    alias(sharedLibs.plugins.rinne.multiplatform.compose)
}

kotlin {
    rinneAndroid("com.rinne.libraries.highlights")
    sourceSets.commonMain.dependencies {
        implementation(projects.rinneShared.libraries.text.core)
    }
}
