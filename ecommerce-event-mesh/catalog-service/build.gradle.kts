dependencies {
    // Web: For our REST API Controllers
    implementation("org.springframework.boot:spring-boot-starter-web")

    // Kafka: To connect to our KRaft broker
    implementation("org.springframework.kafka:spring-kafka")

    // JDBC & Postgres: For our raw SQL Outbox Pattern (No JPA!)
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    runtimeOnly("org.postgresql:postgresql")

    // Jackson: Tool to convert our Java objects into JSON strings for the outbox payload
    implementation("com.fasterxml.jackson.core:jackson-databind")
}