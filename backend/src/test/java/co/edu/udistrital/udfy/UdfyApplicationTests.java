package co.edu.udistrital.udfy;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
		"spring.datasource.url=jdbc:h2:mem:testdb",
		"spring.datasource.driver-class-name=org.h2.Driver",
		"spring.datasource.username=sa",
		"spring.datasource.password=",
		"spring.jpa.hibernate.ddl-auto=create-drop",
		"app.jwt.secret=abcdefghijklmnopqrstuvwxyz0123456789",
		"admin.email=",
		"admin.password="
})
class UdfyApplicationTests {

	@Test
	void contextLoads() {
	}

}
