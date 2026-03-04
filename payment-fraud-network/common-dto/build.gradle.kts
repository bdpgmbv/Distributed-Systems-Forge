// We don't need Spring Boot plugin here, just plain Java
plugins {
    java
}

dependencies {
    // We only need Jackson so we can serialize these objects to JSON for Kafka
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.18.2")
}