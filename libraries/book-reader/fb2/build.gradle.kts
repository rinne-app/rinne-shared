plugins {
    alias(libs.plugins.rinne.multiplatform.library)
}


android {
    namespace = "com.rinne.libraries.book.reader.fb2"
}

kotlin {
    sourceSets.commonMain.dependencies {
        api(projects.rinneShared.libraries.bookReader.core)

        implementation(projects.rinneShared.libraries.logger.core)
        implementation(projects.rinneShared.libraries.serialization.xml)
    }
}
