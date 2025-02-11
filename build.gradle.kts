plugins {
    id("java")
    id("application")
    id("io.micronaut.application") version "4.2.0"
}

group = "org.example"
version = "1.0-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_21


repositories {
    mavenCentral()
    maven { url = uri("https://s01.oss.sonatype.org/content/repositories/releases") }
}

dependencies {
    // Tests w/ JUnit
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    // Micronaut dependencies
    implementation("io.micronaut:micronaut-runtime:4.2.0")
    implementation("io.micronaut.data:micronaut-data-hibernate-jpa:4.2.0")
    implementation("io.micronaut.openapi:micronaut-openapi")
    implementation("io.micronaut:micronaut-jackson-databind")

    // Jakarta Persistence API
    implementation("jakarta.persistence:jakarta.persistence-api:3.1.0")
    implementation("jakarta.validation:jakarta.validation-api:3.0.0")


    // Database dependencies
    implementation("org.postgresql:postgresql:42.7.2")

    // Lombok - reduce boilerplate code
    implementation("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")

    runtimeOnly("org.yaml:snakeyaml:2.2")
    runtimeOnly("ch.qos.logback:logback-classic:1.4.11")
}

micronaut {
    version("4.2.0")
    runtime("netty")
    testRuntime("junit5")
    processing {
        incremental(true)
        annotations("*")
    }
}

//micronaut {
//    runtime("netty") // Default Micronaut runtime
//    testRuntime("junit5")
//    processing {
//        incremental(true)
//        annotations("org.example.*")
//    }
//}

application {
    mainClass.set("com.wallet.Application")
}

tasks.test {
    useJUnitPlatform()
}

