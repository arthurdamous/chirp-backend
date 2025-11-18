/**
 * Precompiled [chirp.spring-boot-service.gradle.kts][Chirp_spring_boot_service_gradle] script plugin.
 *
 * @see Chirp_spring_boot_service_gradle
 */
public
class Chirp_springBootServicePlugin : org.gradle.api.Plugin<org.gradle.api.Project> {
    override fun apply(target: org.gradle.api.Project) {
        try {
            Class
                .forName("Chirp_spring_boot_service_gradle")
                .getDeclaredConstructor(org.gradle.api.Project::class.java, org.gradle.api.Project::class.java)
                .newInstance(target, target)
        } catch (e: java.lang.reflect.InvocationTargetException) {
            throw e.targetException
        }
    }
}
