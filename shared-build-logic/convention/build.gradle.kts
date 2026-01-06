plugins {
    `kotlin-dsl`
}

group = "com.rinne.shared.buildlogic"

dependencies {
    compileOnly(sharedLibs.android.gradlePlugin)
    compileOnly(sharedLibs.kotlin.gradlePlugin)
//    compileOnly(libs.compose.gradlePlugin)
//    compileOnly(libs.compose.compiler.gradlePlugin)
}



gradlePlugin {
    plugins {
        register("sharedMultiplatformLibraryConventionPlugin") {
            id = "rinne.shared.multiplatform.library"
            implementationClass = "MultiplatformLibraryConventionPlugin"
        }
        register("sharedMultiplatformCoreConventionPlugin") {
            id = "rinne.shared.multiplatform.core"
            implementationClass = "MultiplatformCoreConventionPlugin"
        }
        register("sharedMultiplatformKoinConventionPlugin") {
            id = "rinne.shared.multiplatform.koin"
            implementationClass = "MultiplatformKoinConventionPlugin"
        }
        register("sharedMultiplatformKotlinSerializationConventionPlugin") {
            id = "rinne.shared.multiplatform.kotlin.serialization"
            implementationClass = "MultiplatformKotlinSerializationConventionPlugin"
        }
        register("sharedMultiplatformKtorClientConventionPlugin") {
            id = "rinne.shared.multiplatform.ktor.client"
            implementationClass = "MultiplatformKtorClientConventionPlugin"
        }
    }
}
