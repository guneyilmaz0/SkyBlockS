plugins {
    kotlin("jvm") version "2.1.10"
}

group = "net.guneyilmaz0.skyblocks"
version = "1.0.1"

repositories {
    mavenCentral()
    maven("https://repo.opencollab.dev/maven-releases")
    maven("https://repo.opencollab.dev/maven-snapshots")
    maven("https://jitpack.io")
}

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5:1.9.23")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.10.0")
    implementation("com.github.guneyilmaz0:MongoS:5.0.1")
    implementation("org.xerial:sqlite-jdbc:3.34.0")
    implementation("com.mysql:mysql-connector-j:9.1.0")
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