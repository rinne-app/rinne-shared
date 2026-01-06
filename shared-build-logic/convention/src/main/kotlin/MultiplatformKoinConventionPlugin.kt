import com.rinne.shared.configureKoinMultiplatform
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class MultiplatformKoinConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.configureKoinMultiplatform()
    }
}