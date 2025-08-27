plugins {
    `kotlin-dsl`
}

group = "com.rinne.buildlogic"

dependencies {
    compileOnly(sharedLibs.android.gradlePlugin)
    compileOnly(sharedLibs.kotlin.gradlePlugin)
//    compileOnly(libs.compose.gradlePlugin)
//    compileOnly(libs.compose.compiler.gradlePlugin)
}



gradlePlugin {
    plugins {
        register("multiplatformLibraryConventionPlugin") {
            id = "rinne.shared.multiplatform.library"
            implementationClass = "MultiplatformLibraryConventionPlugin"
        }
        register("multiplatformCoreConventionPlugin") {
            id = "rinne.shared.multiplatform.core"
            implementationClass = "MultiplatformCoreConventionPlugin"
        }
        register("multiplatformKoinConventionPlugin") {
            id = "rinne.shared.multiplatform.koin"
            implementationClass = "MultiplatformKoinConventionPlugin"
        }
        register("multiplatformKotlinSerializationConventionPlugin") {
            id = "rinne.shared.multiplatform.kotlin.serialization"
            implementationClass = "MultiplatformKotlinSerializationConventionPlugin"
        }
        register("multiplatformKtorClientConventionPlugin") {
            id = "rinne.shared.multiplatform.ktor.client"
            implementationClass = "MultiplatformKtorClientConventionPlugin"
        }
    }
}
