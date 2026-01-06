package com.rinne.shared

import com.rinne.shared.extensions.sharedLibs
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

internal fun Project.configureKoinMultiplatform() {
    extensions.configure<KotlinMultiplatformExtension> {
        sourceSets.commonMain.dependencies {
            implementation(sharedLibs.findLibrary("koin-core").get())
        }
    }
}