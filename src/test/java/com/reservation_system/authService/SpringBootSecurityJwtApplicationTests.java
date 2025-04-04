package com.reservation_system.authService;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@ActiveProfiles("test") // This will load application-test.properties/yml if available
//@TestPropertySource(properties = {
//		"spring.datasource.url=jdbc:mysql://localhost:3306/testdb_spring?useSSL=false",
//		"spring.datasource.username=root",
//		"spring.datasource.password=",
//		"spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect",
//		"spring.jpa.hibernate.ddl-auto=create-drop",
//		"bezkoder.app.jwtSecret=ea57eb7bee56775e6d33631b5b9ce4305bd014aae96d473e98ee6d99cc31f5b04d3855e257da480ae42f9becaf8f7aec3760eaab167da4938cdad140e5f850ab",
//		"bezkoder.app.jwtExpirationMs=86400000"
//})
@TestPropertySource(properties = {
		"spring.datasource.url=jdbc:mysql://localhost:3306/testdb_spring?useSSL=false&allowPublicKeyRetrieval=true",
		"spring.datasource.username=root",
		"spring.datasource.password=root",
		"spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect",
		"spring.jpa.hibernate.ddl-auto=create-drop",
		"bezkoder.app.jwtSecret=ea57eb7bee56775e6d33631b5b9ce4305bd014aae96d473e98ee6d99cc31f5b04d3855e257da480ae42f9becaf8f7aec3760eaab167da4938cdad140e5f850ab",
		"bezkoder.app.jwtExpirationMs=86400000"
})
public class SpringBootSecurityJwtApplicationTests {
	@Test
	public void contextLoads() {
	}
}