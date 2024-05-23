package org.instituteatri.backendblog.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Slf4j
@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;
    private final PostRepository postRepository;
    private final ModelMapper modelMapper;

    public ResponseEntity<List<TagResponseDTO>> processFindAllTags() {
        List<Tag> tags = tagRepository.findAll();
        if (tags.isEmpty()) {
            throw new TagNotFoundException("No tags found");
        }

        List<TagResponseDTO> result = new ArrayList<>();
        tags.forEach(x -> result.add(modelMapper.map(x, TagResponseDTO.class)));
        return ResponseEntity.ok(result);
    }

    public ResponseEntity<TagResponseDTO> findById(String id) {
        Tag tag = existingTagByIdOrThrow(id);

        TagResponseDTO tagResponse = modelMapper.map(tag, TagResponseDTO.class);
        return ResponseEntity.ok(tagResponse);
    }

    public ResponseEntity<List<PostResponseDTO>> findPostsByTagId(String id) {
        Tag existingTag = existingTagByIdOrThrow(id);

        return ResponseEntity.ok().body(
                existingTag
                        .getPosts()
                        .stream()
                        .map(post -> modelMapper.map(post, PostResponseDTO.class))
                        .toList());
    }

    public ResponseEntity<TagRequestDTO> processCreateTag(TagRequestDTO tagRequestDTO) {
        try {
            Tag tag = new Tag(tagRequestDTO.getName(), tagRequestDTO.getSlug());

            tag = tagRepository.save(tag);

            TagRequestDTO createdTagRequestDTO = modelMapper.map(tag, TagRequestDTO.class);

            String baseUri = "http://localhost:8080";
            URI uri = UriComponentsBuilder.fromUriString(baseUri)
                    .path("/{id}")
                    .buildAndExpand(tag.getId())
                    .toUri();

            return ResponseEntity.created(uri).body(createdTagRequestDTO);
        } catch (Exception ex) {
            log.warn("Error creating", ex);
            throw new CustomExceptionEntities("Error creating tag.");
        }
    }

    public ResponseEntity<Void> processDeleteTag(String id) {

        Tag existingTag = existingTagByIdOrThrow(id);

        List<Post> posts = postRepository.findPostsById(id);
        for (Post post : posts) {
            post.getTags().removeIf(tag -> tag.getId().equals(id));
            postRepository.save(post);
        }

        tagRepository.delete(existingTag);

        return ResponseEntity.noContent().build();
    }

    public ResponseEntity<Void> processUpdateTag(String id, TagUpdateRequestDTO updatedTagRequestDTO) {

        Tag existingTag = existingTagByIdOrThrow(id);

        updateTagProperties(existingTag, updatedTagRequestDTO);

        tagRepository.save(existingTag);

        return ResponseEntity.noContent().build();
    }

    private Tag existingTagByIdOrThrow(String id) {
        return tagRepository.findById(id).orElseThrow(() -> new TagNotFoundException("Could not find tag with id: " + id));
    }

    private void updateTagProperties(Tag existingTag, TagUpdateRequestDTO updatedTagRequestDTO) {
        updateField(existingTag::setName, existingTag.getName(), updatedTagRequestDTO.name());
        updateField(existingTag::setSlug, existingTag.getSlug(), updatedTagRequestDTO.slug());
    }

    protected <T> void updateField(Consumer<T> setter, T currentValue, T newValue) {
        if (newValue != null && !newValue.equals(currentValue)) {
            setter.accept(newValue);
        }
    }
}
