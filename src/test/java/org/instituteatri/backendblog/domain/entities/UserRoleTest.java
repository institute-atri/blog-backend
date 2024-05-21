package org.instituteatri.backendblog.domain.entities;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserRoleTest {

    @Test
    void testEnumValues() {
        assertEquals("ADMIN", UserRole.ADMIN.getRole());
        assertEquals("USER", UserRole.USER.getRole());
    }
}