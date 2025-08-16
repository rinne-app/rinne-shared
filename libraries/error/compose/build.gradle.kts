plugins {
    alias(libs.plugins.rinne.multiplatform.library)
    alias(libs.plugins.rinne.multiplatform.compose)
}


android {
    namespace = "com.rinne.libraries.error.compose"
}

kotlin {
    sourceSets.commonMain.dependencies {
        api(projects.libraries.text.core)
        implementation(projects.libraries.error.core)
        implementation(projects.libraries.logger.core)
    }
}
