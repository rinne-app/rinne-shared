import com.android.build.gradle.LibraryExtension
import com.rinne.RinneAppInfo
import com.rinne.configureCoroutinesMultiplatform
import com.rinne.configureKotlinMultiplatform
import com.rinne.configureLoggerMultiplatform
import com.rinne.extensions.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class MultiplatformLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply {
                apply(libs.findPlugin("kotlin-multiplatform").get().get().pluginId)
                apply(libs.findPlugin("android-library").get().get().pluginId)
            }

            extensions.configure<LibraryExtension> {
                compileSdk = RinneAppInfo.Android.targetSdk
                defaultConfig.targetSdk = RinneAppInfo.Android.targetSdk
                defaultConfig.minSdk = RinneAppInfo.Android.minSdk
            }
            configureKotlinMultiplatform()
            configureCoroutinesMultiplatform()
            if (!target.path.contains("logger:core")) configureLoggerMultiplatform()
        }
    }
}