// Notice there is no "plugins" block! The root file handles that for us.

dependencies {
    // 1. Spring Web (To build REST APIs like @RestController)
    implementation("org.springframework.boot:spring-boot-starter-web")

    // 2. Spring Data JDBC (Gives us JdbcTemplate to write raw SQL)
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")

    // 3. Actuator (We will need this later to monitor our Circuit Breakers)
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    // 4. PostgreSQL Driver (To talk to our Docker database)
    runtimeOnly("org.postgresql:postgresql")

    // 5. Testing tools
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    // 6. Resilience4j for Spring Boot 3
    implementation("io.github.resilience4j:resilience4j-spring-boot3:2.2.0")

    // 7. AOP (Aspect Oriented Programming) - Required for Resilience4j annotations to work!
    implementation("org.springframework.boot:spring-boot-starter-aop")

    // 8. Chaos Monkey for Spring Boot
    implementation("de.codecentric:chaos-monkey-spring-boot:4.0.0")
}