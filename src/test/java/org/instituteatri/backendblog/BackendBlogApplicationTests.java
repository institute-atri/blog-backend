package org.instituteatri.backendblog;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class BackendBlogApplicationTests {

	@Test
	void contextLoads() {
		String message = "Context loaded successfully!";

		assertNotNull(message, "Message should not be null");
	}
}
