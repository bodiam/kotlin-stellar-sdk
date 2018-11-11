import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    jacoco
    kotlin("jvm") version "1.3.0"
}

group = "io.openmotion"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    compile(kotlin("stdlib-jdk8"))

    // Temp dependency
    compile("com.google.code.gson:gson:2.8.5")
    compile("com.google.guava:guava:26.0-android")
    compile("net.i2p.crypto:eddsa:0.3.0")
    compile("com.squareup.okhttp3:okhttp:3.11.0")

    testCompile ("com.squareup.okhttp3:mockwebserver:3.11.0")
    testCompile("junit", "junit", "4.12")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}
tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.withType<JacocoReport> {
    reports {
        xml.isEnabled = true
        html.isEnabled = false
    }
}

jacoco {
    toolVersion = "0.8.2"
}