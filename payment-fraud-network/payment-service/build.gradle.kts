plugins {
    // This is a Spring Boot application
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    java
}

dependencies {
    // Bring in our shared DTOs!
    implementation(project(":common-dto"))

    // Spring Boot Web (For REST APIs)
    implementation("org.springframework.boot:spring-boot-starter-web")

    // Spring Kafka (For sending/receiving messages)
    implementation("org.springframework.kafka:spring-kafka")
}