package org.instituteatri.backendblog.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

import static com.mongodb.assertions.Assertions.assertNull;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DataMongoTest
class UserRepositoryTest {

    @MockBean
    private UserRepository mockUserRepository;

    @Test
    @DisplayName("Should retrieve user details with valid email")
    void ShouldRetrieveUserDetailsWithValidEmail() {
        // Arrange
        Collection<GrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));
        UserDetails mockUserDetails = new User(
                "valid@example.com",
                "password",
                authorities);
        when(mockUserRepository.findByEmail("valid@example.com")).thenReturn(mockUserDetails);

        // Act
        UserDetails userDetails = mockUserRepository.findByEmail("valid@example.com");

        // Assert
        assertNotNull(userDetails);
        assertThat(userDetails.getUsername()).isEqualTo("valid@example.com");
        verify(mockUserRepository).findByEmail("valid@example.com");
    }

    @Test
    @DisplayName("Should return null for non-existent email")
    void ShouldReturnNullForNonExistentEmail() {
        // Arrange
        when(mockUserRepository.findByEmail("nonexistent@example.com")).thenReturn(null);

        // Act
        UserDetails userDetails = mockUserRepository.findByEmail("nonexistent@example.com");

        // Assert
        assertNull(userDetails);
        verify(mockUserRepository).findByEmail("nonexistent@example.com");
    }
}
