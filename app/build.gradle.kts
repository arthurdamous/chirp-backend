plugins {
    id("chirp.spring-boot-app")
}

group = "com.plcoding"
version = "0.0.1-SNAPSHOT"
description = "Chirp Backend"

dependencies {
    implementation(projects.chat)
    implementation(projects.user)
    implementation(projects.notification)
    implementation(projects.common)
}
