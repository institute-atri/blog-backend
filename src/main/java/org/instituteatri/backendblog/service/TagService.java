package org.instituteatri.backendblog.service;

import lombok.RequiredArgsConstructor;
import org.instituteatri.backendblog.domain.entities.Post;
import org.instituteatri.backendblog.domain.entities.Tag;
import org.instituteatri.backendblog.dto.request.PostRequestDTO;
import org.instituteatri.backendblog.dto.request.TagRequestDTO;
import org.instituteatri.backendblog.infrastructure.exceptions.TagNotFoundException;
import org.instituteatri.backendblog.mappings.TagMapper;
import org.instituteatri.backendblog.repository.PostRepository;
import org.instituteatri.backendblog.repository.TagRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class TagService {


    private final TagRepository tagRepository;
    private final TagMapper tagMapper;
    private final PostRepository postRepository;

    public ResponseEntity<List<TagRequestDTO>> processFindAllTags() {
        List<Tag> tags = tagRepository.findAll();

        return ResponseEntity.ok(tags.stream()
                .map(tagMapper::toTagDto)
                .toList());
    }

    public TagRequestDTO findById(String id) {
        Optional<Tag> tag = tagRepository.findById(id);

        return tag.map(tagMapper::toTagDto).orElseThrow(() -> new TagNotFoundException(id));
    }

    public List<PostRequestDTO> findPostsByTagId(String tagId) {
        TagRequestDTO tagRequestDTO = findById(tagId);
        return tagRequestDTO.postRequestDTOS();
    }

    public ResponseEntity<TagRequestDTO> processCreateTag(TagRequestDTO tagRequestDTO) {
        Tag tag = new Tag(tagRequestDTO.name(), tagRequestDTO.slug());

        tag = tagRepository.save(tag);

        TagRequestDTO createdTagRequestDTO = tagMapper.toTagDto(tag);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(tag.getId())
                .toUri();

        return ResponseEntity.created(uri).body(createdTagRequestDTO);
    }

    public ResponseEntity<Void> processDeleteTag(String id) {
        Tag existingTag = tagRepository.findById(id)
                .orElseThrow(() -> new TagNotFoundException(id));

        List<Post> posts = postRepository.findPostsById(id);
        for (Post post : posts) {
            post.getTags().removeIf(tag -> tag.getId().equals(id));
            postRepository.save(post);
        }

        tagRepository.delete(existingTag);

        return ResponseEntity.noContent().build();
    }

    public ResponseEntity<Void> processUpdateTag(String id, TagRequestDTO updatedTagRequestDTO) {
        Tag existingTag = tagRepository.findById(id)
                .orElseThrow(() -> new TagNotFoundException(id));

        updateTagProperties(existingTag, updatedTagRequestDTO);

        tagRepository.save(existingTag);

        return ResponseEntity.noContent().build();
    }

    private void updateTagProperties(Tag existingTag, TagRequestDTO updatedTagRequestDTO) {
        updateField(existingTag::setName, existingTag.getName(), updatedTagRequestDTO.name());
        updateField(existingTag::setSlug, existingTag.getSlug(), updatedTagRequestDTO.slug());
    }

    private <T> void updateField(Consumer<T> setter, T currentValue, T newValue) {
        if (newValue != null && !newValue.equals(currentValue)) {
            setter.accept(newValue);
        }
    }
}
