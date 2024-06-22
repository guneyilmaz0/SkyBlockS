plugins {
    kotlin("jvm") version "1.9.23"
}

group = "net.guneyilmaz0.skyblocks"
version = "0.0.1-alpha"

repositories {
    mavenCentral()
    maven {
        url = uri("https://repo.opencollab.dev/maven-releases")
    }
    maven {
        url = uri("https://repo.opencollab.dev/maven-snapshots")
    }
}

dependencies {
    testImplementation(kotlin("test"))
    compileOnly ("cn.nukkit:nukkit:1.0-SNAPSHOT")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}