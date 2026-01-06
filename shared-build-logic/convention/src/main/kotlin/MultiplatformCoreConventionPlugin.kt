import com.rinne.shared.configureCoroutinesMultiplatform
import com.rinne.shared.configureKoinMultiplatform
import com.rinne.shared.configureKotlinMultiplatform
import com.rinne.shared.extensions.sharedLibs
import com.rinne.shared.extensions.shouldEnableAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class MultiplatformCoreConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        val includeAndroid = target.shouldEnableAndroid()
        with(target) {
            pluginManager.apply {
                apply(sharedLibs.findPlugin("multiplatform").get().get().pluginId)
                if (includeAndroid) apply(sharedLibs.findPlugin("android-library").get().get().pluginId)
            }

            configureKotlinMultiplatform()
            configureCoroutinesMultiplatform()
            configureKoinMultiplatform()
        }
    }
}