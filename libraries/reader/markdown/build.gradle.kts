import com.rinne.extensions.rinneAndroid

plugins {
    alias(libs.plugins.rinne.multiplatform.library)
    alias(libs.plugins.rinne.multiplatform.compose)
}


rinneAndroid("com.rinne.libraries.reader.markdown")

kotlin {
    sourceSets.commonMain.dependencies {
        implementation("org.jetbrains.kotlinx:kotlinx-collections-immutable:0.4.0")
        implementation("org.jetbrains:markdown:0.7.3")
        implementation("dev.snipme:highlights:1.0.0")
////        implementation("com.mikepenz:multiplatform-markdown-renderer:0.35.0")
//        implementation("com.mikepenz:multiplatform-markdown-renderer-m2:0.35.0")
    }
}
