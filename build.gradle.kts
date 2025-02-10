plugins {
    id("java")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven { url = uri("https://s01.oss.sonatype.org/content/repositories/releases") }
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    // Micronaut dependencies (with explicit versions)
    implementation("io.micronaut:micronaut-runtime:4.2.0")
    implementation("io.micronaut.data:micronaut-data-hibernate-jpa:4.2.0")
    implementation("io.micronaut.flyway:micronaut-flyway:4.2.0")

    // Jakarta Persistence API
    implementation("jakarta.persistence:jakarta.persistence-api:3.1.0")

    // Database dependencies
    implementation("org.postgresql:postgresql:42.7.2")
    implementation("org.flywaydb:flyway-core:9.22.3")

    // Lombok for reducing boilerplate
    implementation("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")

    // Logging
    runtimeOnly("ch.qos.logback:logback-classic:1.4.11")
}


tasks.test {
    useJUnitPlatform()
}