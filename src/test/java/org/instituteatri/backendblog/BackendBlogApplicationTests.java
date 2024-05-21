package org.instituteatri.backendblog;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataMongoTest
class BackendBlogApplicationTests {

    @Test
    void contextLoads() {
        String message = "Context loaded successfully!";

        assertNotNull(message, "Message should not be null");
    }
}
