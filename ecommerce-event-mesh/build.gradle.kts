plugins {
	// We declare the plugins here but set apply = false.
	// This tells Gradle "Download these, but don't apply them to the root folder"
	id("org.springframework.boot") version "3.2.3" apply false
	id("io.spring.dependency-management") version "1.1.6" apply false
	java
}

// This block applies settings to EVERY sub-module (catalog-service, search-service, etc.)
subprojects {
	apply(plugin = "java")
	apply(plugin = "org.springframework.boot")
	apply(plugin = "io.spring.dependency-management")

	group = "com.vyshaliprabananthlal.ecommerce-event-mesh"
	version = "0.0.1-SNAPSHOT"

	java {
		sourceCompatibility = JavaVersion.VERSION_17
	}

	repositories {
		mavenCentral()
	}

	dependencies {
		// Every microservice needs testing tools, so we put them here globally!
		"testImplementation"("org.springframework.boot:spring-boot-starter-test")
		"testImplementation"("org.springframework.kafka:spring-kafka-test")
	}

	tasks.withType<Test> {
		useJUnitPlatform()
	}
}