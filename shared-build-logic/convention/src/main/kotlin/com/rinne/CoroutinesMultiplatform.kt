package com.rinne

import com.rinne.extensions.libs
import com.rinne.extensions.shouldEnableAndroid
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

internal fun Project.configureCoroutinesMultiplatform(includeAndroid: Boolean = shouldEnableAndroid()
) {
    extensions.configure<KotlinMultiplatformExtension> {
        sourceSets.apply {
            commonMain.dependencies {
                implementation(libs.findLibrary("kotlinx-coroutines-core").get())
            }
            if (includeAndroid) {
                androidMain.dependencies {
                    implementation(libs.findLibrary("kotlinx-coroutines-android").get())
                }
            }
            getByName("desktopMain").dependencies {
                implementation(libs.findLibrary("kotlinx-coroutines-swing").get())
            }
        }
    }
}