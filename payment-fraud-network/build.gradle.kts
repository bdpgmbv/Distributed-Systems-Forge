plugins {
	id("org.springframework.boot") version "3.4.0" apply false
	id("io.spring.dependency-management") version "1.1.6" apply false
}

// This block applies to every sub-module we create later
subprojects {
	apply(plugin = "org.springframework.boot")
	apply(plugin = "io.spring.dependency-management")
	apply(plugin = "java")

	group = "com.vyshaliprabananthlal"
	version = "0.0.1-SNAPSHOT"

	configure<JavaPluginExtension> {
		toolchain {
			languageVersion.set(JavaLanguageVersion.of(21)) // Change to 17 if using Java 17
		}
	}

	repositories {
		mavenCentral()
	}

	dependencies {
		// FIX: Using 'add' because type-safe accessors aren't available for dynamically applied plugins
		add("implementation", "org.springframework.boot:spring-boot-starter")
		add("testImplementation", "org.springframework.boot:spring-boot-starter-test")
	}

	tasks.withType<Test> {
		useJUnitPlatform()
	}
}