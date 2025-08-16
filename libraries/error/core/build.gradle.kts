plugins {
    alias(libs.plugins.rinne.multiplatform.library)
}


android {
    namespace = "com.rinne.libraries.error.core"
}

kotlin {
    sourceSets.commonMain.dependencies {
        implementation(projects.rinneShared.libraries.logger.core)
    }
}
