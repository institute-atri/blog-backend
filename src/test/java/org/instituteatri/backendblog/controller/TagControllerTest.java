package org.instituteatri.backendblog.controller;

import org.instituteatri.backendblog.domain.entities.Tag;
import org.instituteatri.backendblog.dto.request.TagRequestDTO;
import org.instituteatri.backendblog.dto.request.TagUpdateRequestDTO;
import org.instituteatri.backendblog.dto.response.PostResponseDTO;
import org.instituteatri.backendblog.dto.response.TagResponseDTO;
import org.instituteatri.backendblog.infrastructure.exceptions.CustomExceptionEntities;
import org.instituteatri.backendblog.infrastructure.exceptions.TagNotFoundException;
import org.instituteatri.backendblog.service.TagService;
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
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TagControllerTest {

    @Mock
    private TagService tagService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private TagController tagController;

    private final String tagId = "123";

    @Nested
    class getAllTags {
        @Test
        @DisplayName("Should get all tags with success")
        void shouldGetAllTagsWithSuccess() {
            // Arrange
            List<TagResponseDTO> expectedResponse = new ArrayList<>();
            expectedResponse.add(new TagResponseDTO(
                    "123",
                    "Tag",
                    "Slug"
            ));
            expectedResponse.add(new TagResponseDTO(
                    "1233",
                    "Tag 2",
                    "Slug 2"
            ));
            when(tagService.processFindAllTags()).thenReturn(ResponseEntity.ok(expectedResponse));

            // Act
            ResponseEntity<List<TagResponseDTO>> responseEntity = tagController.findAllTags();

            // Assert
            assertThat(responseEntity.getBody()).isEqualTo(expectedResponse);
            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
            verify(tagService).processFindAllTags();
        }

        @Test
        @DisplayName("Should get all tags with success when not found")
        void shouldGetAllTagsWithSuccessWhenNotFound() {
            // Arrange
            when(tagService.processFindAllTags()).thenThrow(new TagNotFoundException("No tags found"));

            // Act
            Exception exception = assertThrows(TagNotFoundException.class, () -> tagController.findAllTags());

            // Assert
            assertThat(exception.getMessage()).isEqualTo("No tags found");
        }
    }

    @Nested
    class getTagById {

        @Test
        @DisplayName("Should get tag by id with success")
        void shouldGetTagByIdWithSuccess() {
            // Arrange
            TagResponseDTO expectedResponse = new TagResponseDTO(
                    "123",
                    "Tag",
                    "Slug"
            );
            when(tagService.findById("123")).thenReturn(ResponseEntity.ok(expectedResponse));

            // Act
            ResponseEntity<TagResponseDTO> responseEntity = tagController.findByIdTag("123");

            // Assert
            assertThat(responseEntity.getBody()).isEqualTo(expectedResponse);
            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
            verify(tagService).findById("123");

        }

        @Test
        @DisplayName("Should get tag by id with success when not found")
        void shouldGetTagByIdWithSuccessWhenNotFound() {
            // Arrange
            when(tagService.findById(tagId)).thenThrow(new TagNotFoundException("Could not find tag with id: " + tagId));

            // Act
            Exception exception = assertThrows(TagNotFoundException.class, () -> tagController.findByIdTag(tagId));

            // Assert
            assertThat(exception.getMessage()).isEqualTo("Could not find tag with id: " + tagId);
            verify(tagService).findById(tagId);
        }
    }

    @Nested
    class createTag {

        @Test
        @DisplayName("Should create tag with success")
        void shouldCreateTagWithSuccess() {
            // Arrange
            TagRequestDTO requestDTO = new TagRequestDTO(
                    "Tag",
                    "Slug"
            );

            Tag tag = new Tag(requestDTO.getName(), requestDTO.getSlug());

            TagRequestDTO createdTagRequestDTO = modelMapper.map(tag, TagRequestDTO.class);

            String baseUri = "http://localhost:8080";
            URI uri = UriComponentsBuilder.fromUriString(baseUri)
                    .path("/{id}")
                    .buildAndExpand(tag.getId()).toUri();

            when(tagService.processCreateTag(createdTagRequestDTO))
                    .thenReturn(ResponseEntity.created(uri).body(createdTagRequestDTO));

            // Act
            ResponseEntity<TagRequestDTO> responseEntity = tagController.createTag(createdTagRequestDTO);

            // Assert
            assertThat(responseEntity.getBody()).isEqualTo(createdTagRequestDTO);
            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            verify(tagService).processCreateTag(createdTagRequestDTO);
        }

        @Test
        @DisplayName("Should handle error when creating tag fails")
        void shouldHandleErrorWhenCreatingTagFails() {
            // Arrange
            TagRequestDTO requestDTO = new TagRequestDTO("Tag", "Slug");
            when(tagService.processCreateTag(requestDTO))
                    .thenThrow(new CustomExceptionEntities("Error creating tag."));

            // Act
            Exception exception = assertThrows(CustomExceptionEntities.class, () -> tagController.createTag(requestDTO));

            // Assert
            assertThat(exception.getMessage()).isEqualTo("Error creating tag.");
            verify(tagService).processCreateTag(requestDTO);
        }

    }

    @Nested
    class updateTag {

        @Test
        @DisplayName("Should update tag with success")
        void shouldUpdateTagWithSuccess() {
            // Arrange
            TagUpdateRequestDTO updatedTagRequestDTO = new TagUpdateRequestDTO("Tag", "Slug");
            when(tagService.processUpdateTag(tagId, updatedTagRequestDTO)).thenReturn(ResponseEntity.noContent().build());

            // Act
            ResponseEntity<Void> responseEntity = tagController.updateTag(tagId, updatedTagRequestDTO);

            // Assert
            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
            verify(tagService).processUpdateTag(tagId, updatedTagRequestDTO);
        }

        @Test
        @DisplayName("Should return not found when tag is not found")
        void shouldReturnNotFoundWhenTagIsNotFound() {
            // Arrange
            TagUpdateRequestDTO updatedTagRequestDTO = new TagUpdateRequestDTO("Tag", "Slug");
            when(tagService.processUpdateTag(tagId, updatedTagRequestDTO))
                    .thenThrow(new TagNotFoundException("Could not find tag with id: " + tagId));

            // Act
            Exception exception = assertThrows(TagNotFoundException.class, () -> tagController.updateTag(tagId, updatedTagRequestDTO));

            // Assert
            assertThat(exception.getMessage()).isEqualTo("Could not find tag with id: " + tagId);
            verify(tagService).processUpdateTag(tagId, updatedTagRequestDTO);
        }
    }

    @Nested
    class deleteTag {

        @Test
        @DisplayName("Should delete tag with success")
        void shouldDeleteTagWithSuccess() {
            // Arrange
            ResponseEntity<Void> expectedResponse = ResponseEntity.noContent().build();
            when(tagService.processDeleteTag(tagId)).thenReturn(expectedResponse);

            // Act
            ResponseEntity<Void> responseEntity = tagController.deleteTag(tagId);

            // Assert
            assertThat(responseEntity)
                    .isEqualTo(expectedResponse)
                    .extracting(ResponseEntity::getStatusCode)
                    .isEqualTo(HttpStatus.NO_CONTENT);
            verify(tagService).processDeleteTag(tagId);
        }

        @Test
        @DisplayName("Should return not found when tag is not found")
        void shouldReturnNotFoundWhenTagIsNotFound() {
            // Arrange
            when(tagService.processDeleteTag(tagId))
                    .thenThrow(new TagNotFoundException("Could not find tag with id: " + tagId));

            // Act
            Exception exception = assertThrows(TagNotFoundException.class, () -> tagController.deleteTag(tagId));

            // Assert
            assertThat(exception.getMessage()).isEqualTo("Could not find tag with id: " + tagId);
            verify(tagService).processDeleteTag(tagId);
        }
    }

    @Nested
    class findPostsByUserId {

        @Test
        @DisplayName("Should find posts by tag id")
        void shouldFindPostsByUserId() {
            // Arrange
            List<PostResponseDTO> expectedResponse = new ArrayList<>();
            ResponseEntity<List<PostResponseDTO>> expectedResponseEntity = ResponseEntity.ok(expectedResponse);
            when(tagService.findPostsByTagId(tagId)).thenReturn(expectedResponseEntity);

            // Act
            ResponseEntity<List<PostResponseDTO>> responseEntity = tagController.getPostsByCategoryId(tagId);

            // Assert
            assertThat(responseEntity.getBody()).isEqualTo(expectedResponse);
            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
            verify(tagService).findPostsByTagId(tagId);
        }

        @Test
        @DisplayName("Should return not found when tag is not found")
        void shouldReturnNotFoundWhenTagIsNotFound() {
            // Arrange
            when(tagService.findPostsByTagId(tagId))
                    .thenThrow(new TagNotFoundException("Could not find tag with id: " + tagId));

            // Act
            Exception exception = assertThrows(TagNotFoundException.class, () -> tagController.getPostsByCategoryId(tagId));

            // Assert
            assertThat(exception.getMessage()).isEqualTo("Could not find tag with id: " + tagId);
            verify(tagService).findPostsByTagId(tagId);
        }
    }
}