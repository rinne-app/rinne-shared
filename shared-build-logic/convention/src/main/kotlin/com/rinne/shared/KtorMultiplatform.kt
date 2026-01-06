package com.rinne.shared

import com.rinne.shared.extensions.sharedLibs
import com.rinne.shared.extensions.shouldEnableAndroid
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

internal fun Project.configureKtorClientMultiplatform(
    includeAndroid: Boolean = shouldEnableAndroid()
) {
    extensions.configure<KotlinMultiplatformExtension> {
        sourceSets.apply {
            commonMain.dependencies {
                implementation(sharedLibs.findLibrary("ktor-client-core").get())
                implementation(sharedLibs.findLibrary("ktor-client-content-negotiation").get())
                implementation(sharedLibs.findLibrary("ktor-client-serialization").get())
                implementation(sharedLibs.findLibrary("ktor-client-serialization-kotlinx-json").get())
                implementation(sharedLibs.findLibrary("ktor-client-logging").get())
            }

            if (includeAndroid) androidMain.dependencies {
                implementation(sharedLibs.findLibrary("ktor-client-okhttp").get())
            }

            iosMain.dependencies {
                implementation(sharedLibs.findLibrary("ktor-client-darwin").get())
            }

            getByName("desktopMain").dependencies {
                implementation(sharedLibs.findLibrary("ktor-client-cio").get())
            }
        }
    }
}