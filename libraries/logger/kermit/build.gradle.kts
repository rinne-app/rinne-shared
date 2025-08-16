plugins {
    alias(libs.plugins.rinne.multiplatform.library)
    alias(libs.plugins.rinne.multiplatform.koin)
    alias(libs.plugins.rinne.multiplatform.ktor)
}


android {
    namespace = "com.rinne.libraries.logger.kermit"
}

kotlin {
    sourceSets.commonMain.dependencies {
        api(projects.libraries.logger.core)
        implementation(libs.kermit)
    }
}
