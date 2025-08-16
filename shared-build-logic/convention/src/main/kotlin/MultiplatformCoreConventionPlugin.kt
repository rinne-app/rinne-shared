import com.android.build.gradle.LibraryExtension
import com.rinne.RinneAppInfo
import com.rinne.configureCoroutinesMultiplatform
import com.rinne.configureKoinMultiplatform
import com.rinne.configureKotlinMultiplatform
import com.rinne.extensions.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class MultiplatformCoreConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply {
                apply(libs.findPlugin("multiplatform").get().get().pluginId)
                apply(libs.findPlugin("android-library").get().get().pluginId)
            }

            extensions.configure<LibraryExtension> {
                compileSdk = RinneAppInfo.Android.targetSdk
                defaultConfig.targetSdk = RinneAppInfo.Android.targetSdk
                defaultConfig.minSdk = RinneAppInfo.Android.minSdk
            }

            configureKotlinMultiplatform()
            configureCoroutinesMultiplatform()
            configureKoinMultiplatform()
        }
    }
}