plugins {
	id("org.springframework.boot") version "3.2.4" apply false
	id("io.spring.dependency-management") version "1.1.6" apply false
}

allprojects {
	group = "com.vyshaliprabananthlal"
	version = "1.0.0"

	repositories {
		mavenCentral()
	}
}

subprojects {
	apply(plugin = "java")
	apply(plugin = "org.springframework.boot")
	apply(plugin = "io.spring.dependency-management")

	// THE FIX: We explicitly tell Kotlin DSL we are configuring the Java plugin
	configure<JavaPluginExtension> {
		sourceCompatibility = JavaVersion.VERSION_21
	}

	// THE FIX: We use configureEach for tasks in subprojects to avoid lifecycle errors
	tasks.withType<Test>().configureEach {
		useJUnitPlatform()
	}
}