package com.rinne.extensions

import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.plugin.sources.android.findAndroidSourceSet
import java.io.File
import java.util.Properties

val Project.libs
    get(): VersionCatalog = extensions.getByType<VersionCatalogsExtension>().named("sharedLibs")

fun Project.getLibrary(alias: String) = libs.findLibrary(alias).get()

fun Project.dependencies(configuration: DependencyHandlerScope.(VersionCatalog) -> Unit) {
    dependencies {
        configuration(this, libs)
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

fun Project.rinneAndroid(
    namespaceValue: String,
    configure: Action<LibraryExtension> = Action {}
) {
    extensions.findByType(LibraryExtension::class.java)?.apply {
        namespace = namespaceValue
        configure.execute(this)
    }
}