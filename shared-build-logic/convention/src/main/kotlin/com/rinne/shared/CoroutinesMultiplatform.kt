package com.rinne.shared

import com.rinne.shared.extensions.sharedLibs
import com.rinne.shared.extensions.shouldEnableAndroid
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

internal fun Project.configureCoroutinesMultiplatform(
    includeAndroid: Boolean = shouldEnableAndroid()
) {
    extensions.configure<KotlinMultiplatformExtension> {
        sourceSets.apply {
            commonMain.dependencies {
                implementation(sharedLibs.findLibrary("kotlinx-coroutines-core").get())
            }
            if (includeAndroid) androidMain.dependencies {
                implementation(sharedLibs.findLibrary("kotlinx-coroutines-android").get())
            }
            getByName("desktopMain").dependencies {
                implementation(sharedLibs.findLibrary("kotlinx-coroutines-swing").get())
            }
        }
    }
}