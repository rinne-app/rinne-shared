import com.rinne.shared.extensions.rinneAndroid

plugins {
    alias(sharedLibs.plugins.rinne.multiplatform.library)
}

kotlin {
    rinneAndroid("com.rinne.libraries.logger.kermit")
    sourceSets.commonMain.dependencies {
        api(projects.rinneShared.libraries.logger.core)
        implementation(sharedLibs.kermit)
    }
}
