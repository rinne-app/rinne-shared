package com.rinne

import com.rinne.extensions.libs
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

internal fun Project.configureKtorClientMultiplatform() {
    extensions.configure<KotlinMultiplatformExtension> {
        sourceSets.apply {
            commonMain.dependencies {
                implementation(libs.findLibrary("ktor-client-core").get())
                implementation(libs.findLibrary("ktor-client-content-negotiation").get())
                implementation(libs.findLibrary("ktor-client-serialization").get())
                implementation(libs.findLibrary("ktor-client-serialization-kotlinx-json").get())
                implementation(libs.findLibrary("ktor-client-logging").get())
            }

            androidMain.dependencies {
                implementation(libs.findLibrary("ktor-client-okhttp").get())
            }

            iosMain.dependencies {
                implementation(libs.findLibrary("ktor-client-darwin").get())
            }

            getByName("desktopMain").dependencies {
                implementation(libs.findLibrary("ktor-client-cio").get())
            }
        }
    }
}