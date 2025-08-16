package com.rinne.extensions

import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

val Project.libs
    get(): VersionCatalog = extensions.getByType<VersionCatalogsExtension>().named("sharedLibs")

fun Project.getLibrary(alias: String) = libs.findLibrary(alias).get()

fun Project.dependencies(configuration: DependencyHandlerScope.(VersionCatalog) -> Unit, ) {
    dependencies {
        configuration(this, libs)
    }
}