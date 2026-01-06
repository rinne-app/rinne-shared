import com.rinne.shared.configureKotlinSerializationMultiplatform
import com.rinne.shared.configureKtorClientMultiplatform
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