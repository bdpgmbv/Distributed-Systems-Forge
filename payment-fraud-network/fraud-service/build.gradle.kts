plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    java
}

dependencies {
    // Shared DTOs
    implementation(project(":common-dto"))

    // Spring Kafka
    implementation("org.springframework.kafka:spring-kafka")

    // Spring Data JDBC (Plain JDBC, NO JPA/Hibernate)
    implementation("org.springframework.boot:spring-boot-starter-jdbc")

    // PostgreSQL Driver
    runtimeOnly("org.postgresql:postgresql")
}