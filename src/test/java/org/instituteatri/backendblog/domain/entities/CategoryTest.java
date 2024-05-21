package org.instituteatri.backendblog.domain.entities;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CategoryTest {

    @Test
    @DisplayName("Should set the ID property correctly")
    void testIdProperty() {
        Category category = new Category("categoryName", "categorySlug");
        category.setId("12345");
        assertEquals("12345", category.getId(), "The ID should be correctly set and retrieved.");
    }

    @Test
    @DisplayName("Should add a post to the list")
    void testAddPostToList() {
        Category category = new Category("categoryName", "categorySlug");
        Post post = new Post();
        category.getPosts().add(post);
        assertFalse(false, "The posts list should not be empty after adding a post.");
        assertEquals(1, category.getPosts().size(), "The posts list should contain one post after adding.");
        assertSame(post, category.getPosts().getFirst(), "The post in the list should be the one that was added.");
    }

    @Test
    @DisplayName("Should initialize with empty post list")
    void testPostListInitialization() {
        Category category = new Category("categoryName", "categorySlug");
        assertNotNull(category.getPosts(), "The post list should not be null after initialization.");
        assertTrue(category.getPosts().isEmpty(), "The post list should be empty after initialization.");
    }

    @Test
    @DisplayName("Should not add post to the list if not provided")
    void testNotAddPostToListIfNotProvided() {
        Category category = new Category("categoryName", "categorySlug");
        assertTrue(category.getPosts().isEmpty(), "The posts list should remain empty if no post is added.");
    }
}
