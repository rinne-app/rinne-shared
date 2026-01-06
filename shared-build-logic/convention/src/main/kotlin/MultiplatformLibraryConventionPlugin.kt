import com.rinne.shared.configureCoroutinesMultiplatform
import com.rinne.shared.configureKotlinMultiplatform
import com.rinne.shared.configureLoggerMultiplatform
import com.rinne.shared.extensions.sharedLibs
import com.rinne.shared.extensions.shouldEnableAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class MultiplatformLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            val includeAndroid = shouldEnableAndroid()
            
            pluginManager.apply {
                apply(sharedLibs.findPlugin("kotlin-multiplatform").get().get().pluginId)
                if (includeAndroid) {
                    apply(sharedLibs.findPlugin("android-library").get().get().pluginId)
                }
            }

            configureKotlinMultiplatform(includeAndroid)
            configureCoroutinesMultiplatform(includeAndroid)

            if (path != ":rinne-shared:libraries:logger:core"
                && path != ":rinne-shared:libraries:date-time:core"
            ) configureLoggerMultiplatform()
        }
    }
}