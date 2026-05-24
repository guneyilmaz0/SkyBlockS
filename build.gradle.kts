plugins {
    kotlin("jvm") version "2.3.21"
}

group = "net.guneyilmaz0.skyblocks"
version = "1.0-allay"

repositories {
    mavenCentral()
    maven("https://jitpack.io")
    maven("https://central.sonatype.com/repository/maven-snapshots/")
}

dependencies {
    compileOnly("org.allaymc.allay:api:0.28.0")
    implementation("com.github.guneyilmaz0:MongoS:1.5.0")
}

kotlin {
    jvmToolchain(21)
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
