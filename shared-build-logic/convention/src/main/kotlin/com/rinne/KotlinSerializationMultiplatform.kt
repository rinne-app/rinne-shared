package com.rinne

import com.rinne.extensions.libs
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

internal fun Project.configureKotlinSerializationMultiplatform(
) {
    with(pluginManager) {
        apply(libs.findPlugin("kotlinx-serialization").get().get().pluginId)
    }
    extensions.configure<KotlinMultiplatformExtension> {
        sourceSets.apply {
            commonMain.dependencies {
                implementation(libs.findLibrary("kotlinx-serialization-json").get())
            }
        }
    }
}