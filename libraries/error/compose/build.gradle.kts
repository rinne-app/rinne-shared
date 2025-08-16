plugins {
    alias(libs.plugins.rinne.multiplatform.library)
    alias(libs.plugins.rinne.multiplatform.compose)
}


android {
    namespace = "com.rinne.libraries.error.compose"
}

kotlin {
    sourceSets.commonMain.dependencies {
        api(projects.rinneShared.libraries.text.core)
        implementation(projects.rinneShared.libraries.error.core)
        implementation(projects.rinneShared.libraries.logger.core)
    }
}
