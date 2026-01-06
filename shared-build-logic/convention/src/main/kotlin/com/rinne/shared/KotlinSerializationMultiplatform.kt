package com.rinne.shared

import com.rinne.shared.extensions.sharedLibs
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

internal fun Project.configureKotlinSerializationMultiplatform(
) {
    with(pluginManager) {
        apply(sharedLibs.findPlugin("kotlinx-serialization").get().get().pluginId)
    }
    extensions.configure<KotlinMultiplatformExtension> {
        sourceSets.apply {
            commonMain.dependencies {
                implementation(sharedLibs.findLibrary("kotlinx-serialization-json").get())
            }
        }
    }
}