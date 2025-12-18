import com.android.build.gradle.LibraryExtension
import com.rinne.RinneAppInfo
import com.rinne.configureCoroutinesMultiplatform
import com.rinne.configureKotlinMultiplatform
import com.rinne.configureLoggerMultiplatform
import com.rinne.extensions.libs
import com.rinne.extensions.shouldEnableAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class MultiplatformLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            val includeAndroid = shouldEnableAndroid()
            
            pluginManager.apply {
                apply(libs.findPlugin("kotlin-multiplatform").get().get().pluginId)
                if (includeAndroid) {
                    apply(libs.findPlugin("android-library").get().get().pluginId)
                }
            }

            if (includeAndroid) {
                extensions.configure<LibraryExtension> {
                    compileSdk = RinneAppInfo.Android.targetSdk
                    defaultConfig.targetSdk = RinneAppInfo.Android.targetSdk
                    defaultConfig.minSdk = RinneAppInfo.Android.minSdk
                }
            }
            configureKotlinMultiplatform(includeAndroid)
            configureCoroutinesMultiplatform(includeAndroid)
            if (!target.path.contains("logger:core")) configureLoggerMultiplatform()
        }
    }
}