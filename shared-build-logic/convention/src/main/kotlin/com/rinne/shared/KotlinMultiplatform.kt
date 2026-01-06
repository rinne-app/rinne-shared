package com.rinne.shared

import com.android.build.api.dsl.androidLibrary
import com.rinne.shared.extensions.implementationProject
import com.rinne.shared.extensions.sharedLibs
import com.rinne.shared.extensions.shouldEnableAndroid
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSetTree


internal fun Project.configureKotlinMultiplatform(includeAndroid: Boolean = shouldEnableAndroid()) {
    with(pluginManager) {
        apply(sharedLibs.findPlugin("kotlin-multiplatform").get().get().pluginId)
    }

    extensions.configure<KotlinMultiplatformExtension> {
        jvmToolchain(11)

        jvm("desktop")

//        if (includeAndroid) {
//            androidLibrary()
//        }

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
                if (path != ":rinne-shared:libraries:date-time:core") implementationProject(":rinne-shared:libraries:date-time:core")
            }
        }
    }
}