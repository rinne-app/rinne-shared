import com.rinne.shared.extensions.rinneAndroid

plugins {
    alias(sharedLibs.plugins.rinne.multiplatform.library)
}


kotlin {
    rinneAndroid("com.rinne.libraries.book.reader.fb2")
    sourceSets.commonMain.dependencies {
        api(projects.rinneShared.libraries.bookReader.core)

        implementation(projects.rinneShared.libraries.logger.core)
        implementation(projects.rinneShared.libraries.serialization.xml)
    }
}
