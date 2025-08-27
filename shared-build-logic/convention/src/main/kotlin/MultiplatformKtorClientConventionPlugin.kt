import com.rinne.configureKotlinSerializationMultiplatform
import com.rinne.configureKtorClientMultiplatform
import org.gradle.api.Plugin
import org.gradle.api.Project

class MultiplatformKtorClientConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            configureKtorClientMultiplatform()
            configureKotlinSerializationMultiplatform()
        }
    }
}