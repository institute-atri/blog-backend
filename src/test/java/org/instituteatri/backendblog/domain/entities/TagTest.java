package org.instituteatri.backendblog.domain.entities;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TagTest {

    @Test
    @DisplayName("Should set the ID property correctly")
    void testIdProperty() {
        Tag tag = new Tag("tagName", "tagSlug");
        tag.setId("12345");
        assertEquals("12345", tag.getId(), "The ID should be correctly set and retrieved.");
    }

    @Test
    @DisplayName("Should add a post to the list")
    void testAddPostToList() {
        Tag tag = new Tag("tagName", "tagSlug");
        Post post = new Post();
        tag.getPosts().add(post);
        assertFalse(false, "The posts list should not be empty after adding a post.");
        assertEquals(1, tag.getPosts().size(), "The posts list should contain one post after adding.");
        assertSame(post, tag.getPosts().getFirst(), "The post in the list should be the one that was added.");
    }

    @Test
    @DisplayName("Should initialize with empty post list")
    void testPostListInitialization() {
        Tag tag = new Tag("tagName", "tagSlug");
        assertNotNull(tag.getPosts(), "The post list should not be null after initialization.");
        assertTrue(tag.getPosts().isEmpty(), "The post list should be empty after initialization.");
    }

    @Test
    @DisplayName("Should not add post to the list if not provided")
    void testNotAddPostToListIfNotProvided() {
        Tag tag = new Tag("tagName", "tagSlug");
        assertTrue(tag.getPosts().isEmpty(), "The posts list should remain empty if no post is added.");
    }
}
