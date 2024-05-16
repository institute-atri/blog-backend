package org.instituteatri.backendblog.domain.entities;

import org.instituteatri.backendblog.domain.token.Token;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    User user = new User();
    String email = "test@localhost.com";


    @Nested
    class testEqualsAndHashCode {

        @Test
        @DisplayName("Test equals method")
        void testEquals() {
            // Arrange
            User userTestEquals1 = new User();
            userTestEquals1.setId("1");
            User userTestEquals2 = new User();
            userTestEquals2.setId("1");
            // Act & Assert
            assertEquals(userTestEquals1, userTestEquals2, "Objects should be equal when they have the same values");
        }

        @Test
        @DisplayName("Test equals method with different values")
        void testEqualsWithDifferentValues() {
            // Arrange
            User userDifferentValues1 = new User();
            userDifferentValues1.setId("1");
            User userDifferentValues2 = new User();
            userDifferentValues2.setId("2");

            // Act & Assert
            assertNotEquals(userDifferentValues1, userDifferentValues2, "Objects should not be equal when they have different values");
        }

        @Test
        @DisplayName("Test equals method with different type")
        void testEqualsWithDifferentType() {
            // Arrange
            Object differentObject = new Object();

            // Act & Assert
            assertNotEquals(user, differentObject, "Objects should not be equal when compared with a different type");
        }

        @Test
        @DisplayName("Test equals method with same object")
        void testEqualsWithSameObject() {
            // Act & Assert
            assertEquals(user, user, "An object should be equal to itself");
        }

        @Test
        @DisplayName("Test equals method with null")
        void testEqualsWithNull() {
            // Act & Assert
            assertNotEquals(user, null, "An object should not be equal to null");
        }

        @Test
        @DisplayName("Test hashCode method")
        void testHashCode() {
            // Arrange
            User userHashCode1 = new User();
            userHashCode1.setId("user1");
            User userHashCode2 = new User();
            userHashCode2.setId("user1");
            // Act & Assert
            assertEquals(userHashCode1.hashCode(), userHashCode2.hashCode(), "Hash codes of two equal users should be the same");
        }
    }


    @Test
    @DisplayName("Should initialize post list")
    void shouldInitializePostList() {
        // Act
        List<Post> posts = user.getPosts();

        // Assert
        assertNotNull(posts, "Post list should be initialized");
        assertTrue(posts.isEmpty(), "Post list should be empty initially");
    }

    @Test
    @DisplayName("Should initialize token list")
    void shouldInitializeTokenList() {
        // Act
        List<Token> tokens = user.getTokens();

        // Assert
        assertNotNull(tokens, "Token list should be initialized");
        assertTrue(tokens.isEmpty(), "Token list should be empty initially");
    }

    @Test
    @DisplayName("Should return authorities based on user role")
    void shouldReturnAuthoritiesBasedOnUserRole() {
        // Arrange
        User userAdmin = new User();
        userAdmin.setRole(UserRole.ADMIN);
        User userNormal = new User();
        userNormal.setRole(UserRole.USER);

        // Act & Assert
        assertTrue(userAdmin.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN")), "Admin user should have ROLE_ADMIN authority");
        assertTrue(userNormal.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ROLE_USER")), "Normal user should have ROLE_USER authority");
    }

    @Nested
    class testPropertiesUser {

        @Test
        @DisplayName("Should verify equals and hashCode methods work as expected")
        void shouldVerifyEqualsAndHashCodeMethodsWorkAsExpected() {
            // Arrange
            User user1 = new User();
            User user2 = new User();

            // Assert
            assertEquals(user1, user2, "Two users with same properties should be equal");
            assertEquals(user1.hashCode(), user2.hashCode(), "Hash codes of two equal users should be same");
        }

        @Test
        @DisplayName("Should test all-args constructor")
        void shouldTestAllArgsConstructor() {
            // Arrange
            String id = "123";
            String name = "Name";
            String lastName = "LastName";
            String phoneNumber = "123456789";
            String bio = "Bio";
            String password = "password";
            boolean isActive = true;
            int failedLoginAttempts = 0;
            LocalDateTime lockExpirationTime = LocalDateTime.now();
            UserRole role = UserRole.USER;
            List<Post> posts = new ArrayList<>();
            List<Token> tokens = new ArrayList<>();
            int postCount = 0;

            // Act
            User testUser = new User(id, name, lastName, phoneNumber, bio, email, password, isActive, failedLoginAttempts, lockExpirationTime, role, posts, tokens, postCount);

            // Assert
            assertEquals(id, testUser.getId(), "ID should be set correctly");
            assertEquals(name, testUser.getName(), "Name should be set correctly");
            assertEquals(lastName, testUser.getLastName(), "Last name should be set correctly");
            assertEquals(phoneNumber, testUser.getPhoneNumber(), "Phone number should be set correctly");
            assertEquals(bio, testUser.getBio(), "Bio should be set correctly");
            assertEquals(email, testUser.getEmail(), "Email should be set correctly");
            assertEquals(password, testUser.getPassword(), "Password should be set correctly");
            assertTrue(testUser.isActive(), "isActive should be set correctly");
            assertEquals(failedLoginAttempts, testUser.getFailedLoginAttempts(), "Failed login attempts should be set correctly");
            assertEquals(lockExpirationTime, testUser.getLockExpirationTime(), "Lock expiration time should be set correctly");
            assertEquals(role, testUser.getRole(), "Role should be set correctly");
            assertEquals(posts, testUser.getPosts(), "Posts should be set correctly");
            assertEquals(tokens, testUser.getTokens(), "Tokens should be set correctly");
            assertEquals(postCount, testUser.getPostCount(), "Post count should be set correctly");
        }

        @Test
        @DisplayName("Should not affect other properties when locking account")
        void shouldNotAffectOtherPropertiesWhenLockingAccount() {
            // Arrange
            User testUser = new User(
                    "Name",
                    "LastName",
                    "1234567890",
                    "Bio",
                    "test@localhost.com",
                    "password",
                    true,
                    UserRole.USER);
            String idBefore = testUser.getId();
            String emailBefore = testUser.getEmail();

            // Act
            testUser.lockAccountForHours();

            // Assert
            assertEquals(idBefore, testUser.getId(), "ID should not be affected by locking the account");
            assertEquals("Name", testUser.getName(), "Name should not be affected by locking the account");
            assertEquals("LastName", testUser.getLastName(), "LastName should not be affected by locking the account");
            assertEquals("1234567890", testUser.getPhoneNumber(), "PhoneNumber should not be affected by locking the account");
            assertEquals("Bio", testUser.getBio(), "Bio should not be affected by locking the account");
            assertEquals(emailBefore, testUser.getEmail(), "Email should not be affected by locking the account");
            assertEquals("password", testUser.getPassword(), "Password should not be affected by locking the account");
            assertFalse(testUser.isActive(), "isActive should be set to false when locking the account");
            assertEquals(UserRole.USER, testUser.getRole(), "Role should not be affected by locking the account");
        }

        @Test
        @DisplayName("Should initialize user properties")
        void shouldInitializeUserProperties() {
            // Arrange
            String name = "Name";
            String lastName = "LastName";
            String phoneNumber = "123456789";
            String bio = "Bio";
            String password = "password";
            boolean isActive = true;
            UserRole role = UserRole.USER;

            // Act
            User testUser = new User(name, lastName, phoneNumber, bio, email, password, isActive, role);

            // Assert
            assertEquals(name, testUser.getName());
            assertEquals(lastName, testUser.getLastName());
            assertEquals(phoneNumber, testUser.getPhoneNumber());
            assertEquals(bio, testUser.getBio());
            assertEquals(email, testUser.getEmail());
            assertEquals(password, testUser.getPassword());
            assertTrue(testUser.isActive());
            assertEquals(role, testUser.getRole());
        }
    }

    @Nested
    class testPostCount {
        @Test
        @DisplayName("Should initialize post count to 0")
        void shouldInitializePostCountToZero() {
            // Act & Assert
            assertEquals(0, user.getPostCount(), "Post count should be initialized to 0");
        }

        @Test
        @DisplayName("Should initialize post count to 0 when initialized with a negative value")
        void shouldInitializePostCountToZeroWhenInitializedWithNegativeValue() {
            // Arrange
            int negativePostCount = -5;

            // Act
            user.setPostCount(negativePostCount);

            // Assert
            assertEquals(0, user.getPostCount(), "Post count should be initialized to 0");
        }
    }

    @Nested
    class testGetUserName {
        @Test
        @DisplayName("Should return username as email")
        void shouldReturnUsernameAsEmail() {
            // Arrange
            user.setEmail(email);

            // Act & Assert
            assertEquals(email, user.getUsername(), "Username should return user's email");
        }

        @Test
        @DisplayName("Should return null username when email is not set")
        void shouldReturnNullUsernameWhenEmailIsNotSet() {
            // Act & Assert
            assertNull(user.getUsername(), "Username should be null when email is not set");
        }
    }

    @Nested
    class testIsAccountNonExpired {
        @Test
        @DisplayName("Should verify account is non-expired")
        void shouldVerifyAccountIsNonExpired() {
            // Act & Assert
            assertTrue(user.isAccountNonExpired(), "Account should never expire");
        }

        @Test
        @DisplayName("Should verify account is expired when expiration time is in the past")
        void shouldVerifyAccountIsExpiredWhenExpirationTimeIsInThePast() {
            // Arrange
            user.setLockExpirationTime(LocalDateTime.now().minusDays(1));

            // Act & Assert
            assertFalse(user.isAccountNonExpired(), "Account should be expired when expiration time is in the past");
        }
    }

    @Nested
    class testIsCredentialsNonExpired {
        @Test
        @DisplayName("Should verify credentials are non-expired")
        void shouldVerifyCredentialsAreNonExpired() {
            // Act & Assert
            assertTrue(user.isCredentialsNonExpired(), "Credentials should never expire");
        }

        @Test
        @DisplayName("Should verify credentials are expired when expiration time is in the past")
        void shouldVerifyCredentialsAreExpiredWhenExpirationTimeIsInThePast() {
            // Act
            user.setLockExpirationTime(LocalDateTime.now().minusDays(1));

            // Assert
            assertFalse(user.isCredentialsNonExpired(), "Credentials should be expired when expiration time is in the past");
        }
    }

    @Nested
    class testIsEnabled {
        @Test
        @DisplayName("Should set isActive to false when locking account for hours")
        void shouldSetIsActiveToFalseWhenLockingAccountForHours() {
            // Act
            user.lockAccountForHours();

            // Assert
            assertFalse(user.isEnabled(), "isActive should be set to false");
        }

        @Test
        @DisplayName("Should set isActive to true when account is not locked")
        void shouldSetIsActiveToTrueWhenAccountIsNotLocked() {
            // Act
            user.setActive(true);

            // Assert
            assertTrue(user.isEnabled(), "isActive should be set to true when account is not locked");
        }
    }

    @Nested
    class testLockAccountForHours {
        @Test
        @DisplayName("Should set lock expiration correctly when locking account for hours")
        void shouldSetLockExpirationCorrectlyWhenLockingAccountForHours() {
            // Act
            user.lockAccountForHours();
            LocalDateTime expectedLockExpiration = LocalDateTime.now().plusHours(2);

            // Assert
            assertTrue(expectedLockExpiration.minusMinutes(1).isBefore(user.getLockExpirationTime()) &&
                            expectedLockExpiration.plusMinutes(1).isAfter(user.getLockExpirationTime()),
                    "Lock expiration time should be approximately 2 hours from now");
        }

        @Test
        @DisplayName("Should remain locked until lock expiration")
        void shouldRemainLockedUntilLockExpiration() {
            // Act
            user.lockAccountForHours();

            // Assert
            assertTrue(LocalDateTime.now().plusHours(1).isBefore(user.getLockExpirationTime()),
                    "Account should remain locked");
        }

        @Test
        @DisplayName("Should be unlocked after lock expiration")
        void shouldBeUnlockedAfterLockExpiration() {
            //Act
            user.lockAccountForHours();
            user.setLockExpirationTime(LocalDateTime.now().minusMinutes(1)); // Manually setting for test

            // Assert
            assertTrue(user.isAccountNonLocked(), "Account should be unlocked after lock expiration time");
        }

        @Test
        @DisplayName("Should not change account status when lock expiration time is not reached")
        void shouldNotChangeAccountStatusWhenLockExpirationTimeIsNotReached() {
            // Act
            user.lockAccountForHours();
            user.setLockExpirationTime(LocalDateTime.now().plusHours(2));
            user.checkLockExpiration();

            // Assert
            assertFalse(user.isActive(), "Account should remain locked if lock expiration time is not reached");
            assertNotNull(user.getLockExpirationTime(), "Lock expiration time should not be null before expiration");
        }
    }

    @Nested
    class testLockExpirationTime {
        @Test
        @DisplayName("Should not unlock account when lock expiration time is not reached")
        void shouldNotUnlockAccountWhenLockExpirationTimeIsNotReached() {
            // Act
            user.setLockExpirationTime(LocalDateTime.now().plusHours(2));

            // Assert
            assertFalse(user.isActive(), "Account should remain locked if lock expiration time is not reached");
            assertNotNull(user.getLockExpirationTime(), "Lock expiration time should not be null before expiration");
        }

        @Test
        @DisplayName("Should set isActive to true when lock expiration time is in the past")
        void shouldSetActiveToTrueWhenLockExpirationTimeIsInThePast() {
            // Act
            user.setActive(false);
            user.setLockExpirationTime(LocalDateTime.now().minusHours(1));
            user.checkLockExpiration();

            // Assert
            assertTrue(user.isActive());
        }

        @Test
        @DisplayName("Should return true when lockExpirationTime is null")
        void shouldReturnTrueWhenLockExpirationTimeIsNull() {
            // Arrange
            user.setLockExpirationTime(null);

            // Act & Assert
            assertTrue(user.isAccountNonLocked(), "Account should not be locked when lockExpirationTime is null");
        }

        @Test
        @DisplayName("Should return true when lockExpirationTime is before current time")
        void shouldReturnTrueWhenLockExpirationTimeIsBeforeCurrentTime() {
            // Arrange
            LocalDateTime pastExpiration = LocalDateTime.now().minusHours(1);
            user.setLockExpirationTime(pastExpiration);

            // Act & Assert
            assertTrue(user.isAccountNonLocked(), "Account should not be locked when lockExpirationTime is before current time");
        }

        @Test
        @DisplayName("Should return false when lockExpirationTime is after current time")
        void shouldReturnFalseWhenLockExpirationTimeIsAfterCurrentTime() {
            // Arrange
            LocalDateTime futureExpiration = LocalDateTime.now().plusHours(1);
            user.setLockExpirationTime(futureExpiration);

            // Act & Assert
            assertFalse(user.isAccountNonLocked(), "Account should be locked when lockExpirationTime is after current time");
        }
    }
}