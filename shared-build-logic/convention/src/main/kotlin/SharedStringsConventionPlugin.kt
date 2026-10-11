import com.rinne.shared.strings.GenerateRinneStringsTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.register
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

/**
 * `rinne-shared/shared/strings`: generates the keys, signatures and bundled tables from `tables/`
 * into the module's common sources. Apply after the multiplatform library plugin.
 */
class SharedStringsConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            val generate = tasks.register<GenerateRinneStringsTask>("generateRinneStrings") {
                tablesDirectory.set(layout.projectDirectory.dir("tables"))
                packageName.set("com.rinne.shared.strings")
                kotlinOutputDirectory.set(layout.buildDirectory.dir("generated/rinneStrings/kotlin"))
                osTextsOutputDirectory.set(layout.buildDirectory.dir("generated/rinneStrings/os"))
            }
            extensions.configure<KotlinMultiplatformExtension> {
                sourceSets.getByName("commonMain").kotlin.srcDir(generate.map { it.kotlinOutputDirectory })
            }
        }
    }
}
