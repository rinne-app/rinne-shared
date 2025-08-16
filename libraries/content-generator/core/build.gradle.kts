plugins {
    alias(libs.plugins.rinne.multiplatform.library)
    alias(libs.plugins.rinne.multiplatform.ktor)
}


android {
    namespace = "com.rinne.libraries.content.generator.core"
}

kotlin {
    sourceSets.commonMain.dependencies {}
}
