import com.rinne.extensions.rinneAndroid

plugins {
    alias(sharedLibs.plugins.rinne.multiplatform.library)
}


rinneAndroid("com.rinne.libraries.logger.kermit")

kotlin {
    sourceSets.commonMain.dependencies {
        api(projects.rinneShared.libraries.logger.core)
        implementation(sharedLibs.kermit)
    }
}
