plugins {
    alias(libs.plugins.rinne.multiplatform.library)
}


android {
    namespace = "com.rinne.libraries.book.reader.fb2"
}

kotlin {
    sourceSets.commonMain.dependencies {
        api(projects.libraries.bookReader.core)

        implementation(projects.libraries.logger.core)
        implementation(projects.libraries.serialization.xml)
    }
}
