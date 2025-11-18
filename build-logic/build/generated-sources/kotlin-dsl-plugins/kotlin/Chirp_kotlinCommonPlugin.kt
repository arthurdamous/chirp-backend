/**
 * Precompiled [chirp.kotlin-common.gradle.kts][Chirp_kotlin_common_gradle] script plugin.
 *
 * @see Chirp_kotlin_common_gradle
 */
public
class Chirp_kotlinCommonPlugin : org.gradle.api.Plugin<org.gradle.api.Project> {
    override fun apply(target: org.gradle.api.Project) {
        try {
            Class
                .forName("Chirp_kotlin_common_gradle")
                .getDeclaredConstructor(org.gradle.api.Project::class.java, org.gradle.api.Project::class.java)
                .newInstance(target, target)
        } catch (e: java.lang.reflect.InvocationTargetException) {
            throw e.targetException
        }
    }
}
