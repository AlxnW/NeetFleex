package com.example.neetflex.patterns.builder;

import com.example.neetflex.model.user.User;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserBuilderTest {

    @Test
    void testUserBuilder() {
        // Arrange
        Long id = 1L;
        String username = "testuser";
        String password = "password123";
        String email = "test@example.com";
        String firstName = "Test";
        String lastName = "User";

        // Act
        User user = new UserBuilder()
                .id(id)
                .username(username)
                .password(password)
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .build();

        // Assert
        assertEquals(id, user.getId());
        assertEquals(username, user.getUsername());
        assertEquals(password, user.getPassword());
        assertEquals(email, user.getEmail());
        assertEquals(firstName, user.getFirstName());
        assertEquals(lastName, user.getLastName());
    }

    @Test
    void testUserBuilderWithoutId() {
        // Arrange
        String username = "testuser2";
        String password = "password456";
        String email = "test2@example.com";
        String firstName = "Test2";
        String lastName = "User2";

        // Act
        User user = new UserBuilder()
                .username(username)
                .password(password)
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .build();

        // Assert
        assertNull(user.getId());
        assertEquals(username, user.getUsername());
        assertEquals(password, user.getPassword());
        assertEquals(email, user.getEmail());
        assertEquals(firstName, user.getFirstName());
        assertEquals(lastName, user.getLastName());
    }
}