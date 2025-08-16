dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
    versionCatalogs {
        create("sharedLibs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}

rootProject.name = "shared-build-logic"
include(":convention")