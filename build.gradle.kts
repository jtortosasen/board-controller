import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.21"
}

version = "1.0-SNAPSHOT"

repositories {
    jcenter()
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.2.0")
    implementation("io.ktor:ktor-network:1.1.3")
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation("org.koin:koin-core:2.0.0-rc-2")
    implementation("com.beust:klaxon:5.0.1")
    compile("io.github.microutils:kotlin-logging:1.6.24")
    compile(group = "ch.qos.logback", name = "logback-classic", version = "1.2.3")
    compile(group = "commons-net", name = "commons-net", version = "3.6")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}