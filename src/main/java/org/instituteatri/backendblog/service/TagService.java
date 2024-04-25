package org.instituteatri.backendblog.service;

import lombok.RequiredArgsConstructor;
import org.instituteatri.backendblog.domain.entities.Post;
import org.instituteatri.backendblog.domain.entities.Tag;
import org.instituteatri.backendblog.dtos.PostDTO;
import org.instituteatri.backendblog.dtos.TagDTO;
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

    public ResponseEntity<List<TagDTO>> processFindAllTags() {
        List<Tag> tags = tagRepository.findAll();

        return ResponseEntity.ok(tags.stream()
                .map(tagMapper::toTagDto)
                .toList());
    }

    public TagDTO findById(String id) {
        Optional<Tag> tag = tagRepository.findById(id);

        return tag.map(tagMapper::toTagDto).orElseThrow(() -> new TagNotFoundException(id));
    }

    public List<PostDTO> findPostsByTagId(String tagId) {
        TagDTO tagDTO = findById(tagId);
        return tagDTO.postDTOS();
    }

    public ResponseEntity<TagDTO> processCreateTag(TagDTO tagDTO) {
        Tag tag = new Tag(tagDTO.name(), tagDTO.slug());

        tag = tagRepository.save(tag);

        TagDTO createdTagDTO = tagMapper.toTagDto(tag);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(tag.getId())
                .toUri();

        return ResponseEntity.created(uri).body(createdTagDTO);
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

    public ResponseEntity<Void> processUpdateTag(String id, TagDTO updatedTagDTO) {
        Tag existingTag = tagRepository.findById(id)
                .orElseThrow(() -> new TagNotFoundException(id));

        updateTagProperties(existingTag, updatedTagDTO);

        tagRepository.save(existingTag);

        return ResponseEntity.noContent().build();
    }

    private void updateTagProperties(Tag existingTag, TagDTO updatedTagDTO) {
        updateField(existingTag::setName, existingTag.getName(), updatedTagDTO.name());
        updateField(existingTag::setSlug, existingTag.getSlug(), updatedTagDTO.slug());
    }

    private <T> void updateField(Consumer<T> setter, T currentValue, T newValue) {
        if (newValue != null && !newValue.equals(currentValue)) {
            setter.accept(newValue);
        }
    }
}
