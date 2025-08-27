plugins {
    alias(sharedLibs.plugins.rinne.multiplatform.library)
    alias(sharedLibs.plugins.rinne.multiplatform.ktor.client)
}


android {
    namespace = "com.rinne.libraries.content.generator.core"
}

kotlin {
    sourceSets.commonMain.dependencies {}
}
