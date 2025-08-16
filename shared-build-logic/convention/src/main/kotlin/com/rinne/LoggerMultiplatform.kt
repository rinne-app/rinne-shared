package com.rinne

import com.rinne.extensions.implementationProject
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

internal fun Project.configureLoggerMultiplatform(
) {
    extensions.configure<KotlinMultiplatformExtension> {
        sourceSets.apply {
            commonMain.dependencies {
                implementationProject(":rinne-shared:libraries:logger:core")
            }
        }
    }
}