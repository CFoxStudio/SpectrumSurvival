plugins {
    id("java")
    alias(libs.plugins.shadowJar)
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "dev.celestialfox.spectrumsurvival.Server"
        archiveFileName.set("SpectrumSurvival.jar")
    }
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    mergeServiceFiles()
}

group = "dev.celestialfox.spectrumsurvival"
version = "1.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation("net.minestom:minestom-snapshots:7b659f0fc3")
    implementation("dev.hollowcube:polar:1.12.1")
    implementation("net.kyori:adventure-text-minimessage:4.17.0")
    implementation("org.slf4j:slf4j-api:2.0.13")
    implementation("org.jline:jline-terminal:3.26.3")
    implementation("org.jline:jline-reader:3.26.3")
    implementation("org.xerial:sqlite-jdbc:3.46.1.3")
    implementation("org.mongodb:mongodb-driver-sync:5.2.0")
    implementation("org.mariadb.jdbc:mariadb-java-client:3.4.1")
    implementation("ch.qos.logback:logback-classic:1.5.15")
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}