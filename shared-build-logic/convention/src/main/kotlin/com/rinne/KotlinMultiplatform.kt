package com.rinne

import com.rinne.extensions.libs
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSetTree


internal fun Project.configureKotlinMultiplatform() {
    with(pluginManager) {
        apply(libs.findPlugin("kotlin-multiplatform").get().get().pluginId)
    }

    extensions.configure<KotlinMultiplatformExtension> {
        jvmToolchain(11)

        jvm("desktop")

        androidTarget {
            instrumentedTestVariant.sourceSetTree.set(KotlinSourceSetTree.test)
        }

        listOf(
            iosX64(),
            iosArm64(),
            iosSimulatorArm64(),
        ).forEach {
            it.binaries.framework {
                baseName = RinneAppInfo.Ios.name
                isStatic = true
            }
        }

        sourceSets.apply {
            commonMain.dependencies {
                implementation(libs.findLibrary("kotlinx-datetime").get())
            }
        }
    }
}