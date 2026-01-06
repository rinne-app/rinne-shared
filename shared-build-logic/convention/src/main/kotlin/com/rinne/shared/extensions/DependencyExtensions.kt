package com.rinne.shared.extensions

import org.gradle.api.artifacts.VersionCatalog
import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler

fun KotlinDependencyHandler.implementationCoreProject(path: String) {
    implementationProject(":core:$path")
}

fun KotlinDependencyHandler.implementationProject(path: String) {
    implementation(project(path))
}

fun KotlinDependencyHandler.implementationLibrary(path: String, libs: VersionCatalog) {
    implementation(libs.findLibrary(path).get())
}

fun KotlinDependencyHandler.implementationBom(path: String, libs: VersionCatalog) {
    implementation(project.dependencies.platform(libs.findLibrary(path).get()))
}