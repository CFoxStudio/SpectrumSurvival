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

group = "dev.celestialfox.spectrumsurvival"
version = "1.0.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation("net.minestom:minestom-snapshots:461c56e749")
    implementation("dev.hollowcube:polar:1.11.1")
    implementation("net.kyori:adventure-text-minimessage:4.17.0")
    implementation("org.slf4j:slf4j-api:2.0.13")
    implementation("org.jline:jline-terminal:3.26.3")
    implementation("org.jline:jline-reader:3.26.3")
    implementation("ch.qos.logback:logback-classic:1.5.6")
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}