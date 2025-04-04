package com.reservation_system.authService;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@ActiveProfiles("test") // This will load application-test.properties/yml if available
@TestPropertySource(properties = {
		// Override any critical properties that might be causing issues
		"spring.datasource.url=jdbc:h2:mem:testdb",
		"spring.datasource.driverClassName=org.h2.Driver",
		"spring.datasource.username=sa",
		"spring.datasource.password=password",
		"spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
		"spring.jpa.hibernate.ddl-auto=create-drop",
		"jwt.secret=testsecretkey",
		"jwt.expirationMs=86400000"
})
public class SpringBootSecurityJwtApplicationTests {
	@Test
	public void contextLoads() {
	}
}