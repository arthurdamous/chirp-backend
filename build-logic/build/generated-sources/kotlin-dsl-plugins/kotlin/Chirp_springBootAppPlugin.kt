/**
 * Precompiled [chirp.spring-boot-app.gradle.kts][Chirp_spring_boot_app_gradle] script plugin.
 *
 * @see Chirp_spring_boot_app_gradle
 */
public
class Chirp_springBootAppPlugin : org.gradle.api.Plugin<org.gradle.api.Project> {
    override fun apply(target: org.gradle.api.Project) {
        try {
            Class
                .forName("Chirp_spring_boot_app_gradle")
                .getDeclaredConstructor(org.gradle.api.Project::class.java, org.gradle.api.Project::class.java)
                .newInstance(target, target)
        } catch (e: java.lang.reflect.InvocationTargetException) {
            throw e.targetException
        }
    }
}
