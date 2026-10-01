package com.rinne.shared.extensions

import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget
import com.rinne.shared.RinneAppInfo
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import java.io.File
import java.util.*

val Project.sharedLibs
    get(): VersionCatalog = extensions.getByType<VersionCatalogsExtension>().named("sharedLibs")

fun Project.getLibrary(alias: String) = sharedLibs.findLibrary(alias).get()

fun Project.dependencies(configuration: DependencyHandlerScope.(VersionCatalog) -> Unit) {
    dependencies {
        configuration(this, sharedLibs)
    }
}

/**
 * Declares the module's Android target.
 *
 * AGP 9 dropped the `androidLibrary` extension function, so the target is configured through the
 * extension the `com.android.kotlin.multiplatform.library` plugin registers on the Kotlin
 * extension. That form also works from a precompiled convention plugin, where script accessors are
 * not available.
 */
fun KotlinMultiplatformExtension.rinneAndroid(
    namespace: String,
    action: KotlinMultiplatformAndroidLibraryTarget.() -> Unit = {},
) {
    if (!project.shouldEnableAndroid()) return

    (this as ExtensionAware).extensions.configure<KotlinMultiplatformAndroidLibraryTarget>(
        "androidLibrary"
    ) {
        this.namespace = namespace
        this.compileSdk = RinneAppInfo.Android.compileSdk
        this.minSdk = RinneAppInfo.Android.minSdk
        action(this)
    }
}

fun Project.shouldEnableAndroid(): Boolean {
    // Check for explicit property first (e.g. -PnoAndroid or -PskipAndroid)
    if (findProperty("skipAndroid") == "true") return false

    // Check environment variable for SDK
    val androidHome = System.getenv("ANDROID_HOME")
    if (androidHome != null && File(androidHome).exists()) return true

    // Check local.properties
    val localProperties = rootProject.file("local.properties")
    if (localProperties.exists()) {
        val properties = Properties()
        localProperties.inputStream().use { properties.load(it) }
        val sdkDir = properties.getProperty("sdk.dir")
        if (sdkDir != null && File(sdkDir).exists()) return true
    }

    // If we are in a CI/CD environment without SDK, we probably want to skip Android
    // But typically developers have SDK.
    // If no SDK is found, we should skip to avoid the error.
    return false
}