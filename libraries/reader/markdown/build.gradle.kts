import com.rinne.shared.extensions.rinneAndroid

plugins {
    alias(sharedLibs.plugins.rinne.multiplatform.library)
    alias(sharedLibs.plugins.rinne.multiplatform.compose)
}

kotlin {
    rinneAndroid("com.rinne.libraries.reader.markdown")
    sourceSets.commonMain.dependencies {
        implementation("org.jetbrains.kotlinx:kotlinx-collections-immutable:0.4.0")
        implementation("org.jetbrains:markdown:0.7.3")
        implementation("dev.snipme:highlights:1.0.0")
////        implementation("com.mikepenz:multiplatform-markdown-renderer:0.35.0")
//        implementation("com.mikepenz:multiplatform-markdown-renderer-m2:0.35.0")
    }
}
