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
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5:1.9.23")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.10.0")
    compileOnly ("cn.nukkit:nukkit:1.0-SNAPSHOT")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "net.guneyilmaz0.skyblocks.SkyBlocks"
    }
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from(sourceSets.main.get().output)
    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })
}

kotlin {
    jvmToolchain(21)
}