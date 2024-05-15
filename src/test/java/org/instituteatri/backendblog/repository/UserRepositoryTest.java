package org.instituteatri.backendblog.repository;

import org.instituteatri.backendblog.domain.entities.User;
import org.instituteatri.backendblog.domain.entities.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.security.core.userdetails.UserDetails;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;

@DataMongoTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Should retrieve user details with valid email")
    void ShouldRetrieveUserDetailsWithValidEmail() {
        // Arrange
        User user = new User(
                "Name",
                "LastName",
                "123456789",
                "Bio",
                "valid@example.com",
                "password",
                true,
                UserRole.USER);
        userRepository.save(user);

        // Act
        UserDetails userDetails = userRepository.findByEmail(user.getEmail());

        // Assert
        assertNotNull(userDetails);
        assertThat(userDetails.getUsername()).isEqualTo(user.getEmail());

        userRepository.delete(user);
    }

    @Test
    @DisplayName("Should return null for non-existent email")
    void ShouldReturnNullForNonExistentEmail() {
        // Act
        UserDetails userDetails = userRepository.findByEmail("nonexistent@example.com");

        // Assert
        assertThat(userDetails).isNull();
    }
}
