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

    implementation(libs.spring.boot.starter.security)
    implementation(libs.jackson.datatype.jsr310)

    api(libs.jackson.module.kotlin)

    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.spring.boot.starter.data.redis)
    implementation(libs.spring.boot.starter.amqp)
    implementation(libs.spring.boot.starter.mail)
    runtimeOnly(libs.postgresql)
}
