package org.instituteatri.backendblog.service;

import org.instituteatri.backendblog.domain.entities.Post;
import org.instituteatri.backendblog.domain.entities.Tag;
import org.instituteatri.backendblog.dto.request.TagRequestDTO;
import org.instituteatri.backendblog.dto.request.TagUpdateRequestDTO;
import org.instituteatri.backendblog.dto.response.PostResponseDTO;
import org.instituteatri.backendblog.dto.response.TagResponseDTO;
import org.instituteatri.backendblog.infrastructure.exceptions.CustomExceptionEntities;
import org.instituteatri.backendblog.infrastructure.exceptions.TagNotFoundException;
import org.instituteatri.backendblog.repository.PostRepository;
import org.instituteatri.backendblog.repository.TagRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TagServiceTest {

    @Mock
    private TagRepository tagRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private TagService tagService;

    private Tag existingTag;
    private final String tagId = "123";

    @BeforeEach
    void setUp() {
        existingTag = new Tag("Technology", "technology");
        existingTag.setId(tagId);
        existingTag.setName("Technology");
        existingTag.setSlug("technology");
    }

    @Nested
    @DisplayName("Test Update Field Method")
    class testUpdateFieldMethod {
        @Test
        @DisplayName("updateField should update the field when the new value is different")
        void updateField_ShouldUpdateField_WhenNewValueIsDifferent() {
            // Arrange
            existingTag.setName("Technology");

            // Act
            tagService.updateField(existingTag::setName, existingTag.getName(), "Science");

            // Assert
            assertEquals("Science", existingTag.getName());
        }

        @Test
        @DisplayName("updateField should not update the field when the new value is null")
        void updateField_ShouldNotUpdateField_WhenNewValueIsNull() {
            // Arrange
            existingTag.setName("Technology");

            // Act
            tagService.updateField(existingTag::setName, existingTag.getName(), null);

            // Assert
            assertEquals("Technology", existingTag.getName());
        }

        @Test
        @DisplayName("updateField should not update the field when the new value is the same as the current value")
        void updateField_ShouldNotUpdateField_WhenNewValueIsSameAsCurrentValue() {
            // Arrange
            existingTag.setName("Technology");

            // Act
            tagService.updateField(existingTag::setName, existingTag.getName(), "Technology");

            // Assert
            assertEquals("Technology", existingTag.getName());
        }

        @Test
        @DisplayName("updateField should update the field when the current value is null")
        void updateField_ShouldUpdateField_WhenCurrentValueIsNull() {
            // Act
            tagService.updateField(existingTag::setName, null, "NewTechnology");

            // Assert
            assertEquals("NewTechnology", existingTag.getName());
        }
    }

    @Nested
    @DisplayName("Test Find All Tags Method")
    class testFindAllTagsMethod {
        @Test
        @DisplayName("processFindAllTags should return all tags")
        void processFindAllTags_ShouldReturnAllTags() {
            // Arrange
            Tag tag1 = new Tag("Technology", "technology");
            Tag tag2 = new Tag("Science", "science");
            when(tagRepository.findAll()).thenReturn(List.of(tag1, tag2));

            TagResponseDTO tagResponse1 = new TagResponseDTO(tag1.getId(), tag1.getName(), tag1.getSlug());
            TagResponseDTO tagResponse2 = new TagResponseDTO(tag2.getId(), tag2.getName(), tag2.getSlug());
            when(modelMapper.map(tag1, TagResponseDTO.class)).thenReturn(tagResponse1);
            when(modelMapper.map(tag2, TagResponseDTO.class)).thenReturn(tagResponse2);

            // Act
            ResponseEntity<List<TagResponseDTO>> response = tagService.processFindAllTags();

            // Assert
            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            List<TagResponseDTO> tags = response.getBody();
            assertNotNull(tags);
            assertEquals(2, tags.size());
            assertEquals("Technology", tags.get(0).getName());
            assertEquals("Science", tags.get(1).getName());
        }

        @Test
        @DisplayName("processFindAllTags should throw TagNotFoundException when no tags found")
        void processFindAllTags_ShouldThrowTagNotFoundException_WhenNoTagsFound() {
            // Arrange
            when(tagRepository.findAll()).thenReturn(Collections.emptyList());

            // Act & Assert
            assertThrows(TagNotFoundException.class, () -> tagService.processFindAllTags());
        }
    }

    @Nested
    @DisplayName("Test Find Tag By Id Method")
    class testFindTagByIdMethod {
        @Test
        @DisplayName("findById should return tag when tag exists")
        void findById_ShouldReturnTag_WhenTagExists() {
            // Arrange
            when(tagRepository.findById(tagId)).thenReturn(Optional.of(existingTag));
            TagResponseDTO tagResponse = new TagResponseDTO(tagId, "Technology", "technology");
            when(modelMapper.map(existingTag, TagResponseDTO.class)).thenReturn(tagResponse);

            // Act
            ResponseEntity<TagResponseDTO> responseEntity = tagService.findById(tagId);

            // Assert
            assertNotNull(responseEntity);
            assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
            TagResponseDTO responseBody = responseEntity.getBody();
            assertNotNull(responseBody);
            assertEquals(tagId, responseBody.getId());
            assertEquals("Technology", responseBody.getName());
        }

        @Test
        @DisplayName("findById should throw TagNotFoundException when tag does not exist")
        void findById_ShouldThrowTagNotFoundException_WhenTagDoesNotExist() {
            // Arrange
            when(tagRepository.findById(tagId)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(TagNotFoundException.class, () -> tagService.findById(tagId));
        }
    }

    @Nested
    @DisplayName("Test Find Posts By Tag Id Method")
    class testFindPostsByTagIdMethod {
        @Test
        @DisplayName("findPostsByTagId should return posts when tag exists")
        void findPostsByTagId_ShouldReturnPosts_WhenTagExists() {
            // Arrange
            Post post1 = new Post();
            Post post2 = new Post();
            existingTag.setPosts(List.of(post1, post2));
            when(tagRepository.findById(tagId)).thenReturn(Optional.of(existingTag));

            PostResponseDTO postResponse1 = new PostResponseDTO();
            PostResponseDTO postResponse2 = new PostResponseDTO();
            when(modelMapper.map(post1, PostResponseDTO.class)).thenReturn(postResponse1);
            when(modelMapper.map(post2, PostResponseDTO.class)).thenReturn(postResponse2);

            // Act
            ResponseEntity<List<PostResponseDTO>> responseEntity = tagService.findPostsByTagId(tagId);

            // Assert
            assertNotNull(responseEntity);
            assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
            List<PostResponseDTO> posts = responseEntity.getBody();
            assertNotNull(posts);
            assertEquals(2, posts.size());
        }

        @Test
        @DisplayName("findPostsByTagId should throw TagNotFoundException when tag does not exist")
        void findPostsByTagId_ShouldThrowTagNotFoundException_WhenTagDoesNotExist() {
            // Arrange
            when(tagRepository.findById(tagId)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(TagNotFoundException.class, () -> tagService.findPostsByTagId(tagId));
        }

        @Test
        @DisplayName("findPostsByTagId should return empty list when tag has no posts")
        void findPostsByTagId_ShouldReturnEmptyList_WhenTagHasNoPosts() {
            // Arrange
            existingTag.setPosts(Collections.emptyList());
            when(tagRepository.findById(tagId)).thenReturn(Optional.of(existingTag));

            // Act
            ResponseEntity<List<PostResponseDTO>> response = tagService.findPostsByTagId(tagId);

            // Assert
            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            List<PostResponseDTO> posts = response.getBody();
            assertNotNull(posts);
            assertTrue(posts.isEmpty());
        }
    }

    @Nested
    @DisplayName("Test Create Tag Method")
    class testCreateTagMethod {

        TagRequestDTO tagRequest = new TagRequestDTO("Technology", "technology");

        @Test
        @DisplayName("processCreateTag should create a new tag")
        void processCreateTag_ShouldCreateNewTag() {
            // Arrange
            Tag newTag = new Tag("Technology", "technology");
            newTag.setId(tagId);
            when(tagRepository.save(any(Tag.class))).thenReturn(newTag);

            TagRequestDTO createdTagRequest = new TagRequestDTO("Technology", "technology");
            when(modelMapper.map(newTag, TagRequestDTO.class)).thenReturn(createdTagRequest);

            URI location = UriComponentsBuilder.fromUriString("http://localhost:8080")
                    .path("/{id}")
                    .buildAndExpand(tagId)
                    .toUri();

            // Act
            ResponseEntity<TagRequestDTO> responseEntity = tagService.processCreateTag(tagRequest);

            // Assert
            assertNotNull(responseEntity);
            assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
            assertEquals(location, responseEntity.getHeaders().getLocation());
            TagRequestDTO responseBody = responseEntity.getBody();
            assertNotNull(responseBody);
        }

        @Test
        @DisplayName("processCreateTag should throw CustomExceptionEntities on error")
        void processCreateTag_ShouldThrowCustomExceptionEntities_OnError() {
            // Arrange
            when(tagRepository.save(any(Tag.class))).thenThrow(new RuntimeException());

            // Act & Assert
            assertThrows(CustomExceptionEntities.class, () -> tagService.processCreateTag(tagRequest));
        }
    }

    @Nested
    @DisplayName("Test Delete Tag Method")
    class testDeleteTagMethod {
        @Test
        @DisplayName("processDeleteTag should delete existing tag")
        void processDeleteTag_ShouldDeleteExistingTag() {
            // Arrange
            Post postExistingTag1 = new Post();
            Post postExistingTag2 = new Post();
            List<Post> posts = List.of(postExistingTag1, postExistingTag2);
            when(tagRepository.findById(tagId)).thenReturn(Optional.of(existingTag));
            when(postRepository.findPostsById(tagId)).thenReturn(posts);

            // Act
            ResponseEntity<Void> responseEntity = tagService.processDeleteTag(tagId);

            // Assert
            assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
            verify(tagRepository, times(1)).delete(existingTag);
            verify(postRepository, times(2)).save(any(Post.class));
        }

        @Test
        @DisplayName("processDeleteTag should throw TagNotFoundException when tag does not exist")
        void processDeleteTag_ShouldThrowTagNotFoundException_WhenTagDoesNotExist() {
            // Arrange
            when(tagRepository.findById(tagId)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(TagNotFoundException.class, () -> tagService.processDeleteTag(tagId));
        }
    }

    @Nested
    @DisplayName("Test Update Tag Method")
    class testUpdateTagMethod {

        TagUpdateRequestDTO tagUpdateRequestDTO = new TagUpdateRequestDTO("Science", "science");

        @Test
        @DisplayName("processUpdateTag should update existing tag")
        void processUpdateTag_ShouldUpdateExistingTag() {
            // Arrange
            when(tagRepository.findById(tagId)).thenReturn(Optional.of(existingTag));
            existingTag.setName(tagUpdateRequestDTO.name());
            existingTag.setSlug(tagUpdateRequestDTO.slug());
            when(tagRepository.save(existingTag)).thenReturn(existingTag);

            // Act
            ResponseEntity<Void> responseEntity = tagService.processUpdateTag(tagId, tagUpdateRequestDTO);

            // Assert
            assertNotNull(responseEntity);
            assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
            verify(tagRepository, times(1)).save(existingTag);
        }

        @Test
        @DisplayName("processUpdateTag should throw TagNotFoundException when tag does not exist")
        void processUpdateTag_ShouldThrowTagNotFoundException_WhenTagDoesNotExist() {
            // Arrange
            when(tagRepository.findById(tagId)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(TagNotFoundException.class, () -> tagService.processUpdateTag(tagId, tagUpdateRequestDTO));
        }
    }
}
