package org.instituteatri.backendblog.service;

import lombok.RequiredArgsConstructor;
import org.instituteatri.backendblog.domain.entities.Post;
import org.instituteatri.backendblog.domain.entities.Tag;
import org.instituteatri.backendblog.dto.request.TagRequestDTO;
import org.instituteatri.backendblog.dto.response.PostResponseDTO;
import org.instituteatri.backendblog.dto.response.TagResponseDTO;
import org.instituteatri.backendblog.infrastructure.exceptions.TagNotFoundException;
import org.instituteatri.backendblog.repository.PostRepository;
import org.instituteatri.backendblog.repository.TagRepository;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class TagService {


    private final TagRepository tagRepository;
    private final PostRepository postRepository;
    private final ModelMapper modelMapper;

    public ResponseEntity<List<TagResponseDTO>> processFindAllTags() {

        List<Tag> tags = tagRepository.findAll();
        List<TagResponseDTO> result = new ArrayList<>();

        tags.forEach(x -> result.add(modelMapper.map(x, TagResponseDTO.class)));
        return ResponseEntity.ok(result);
    }

    public TagResponseDTO findById(String id) {
        return tagRepository.findById(id).map(tag -> modelMapper.map(tag, TagResponseDTO.class)).orElseThrow(() -> new TagNotFoundException(id));
    }

    public List<PostResponseDTO> findPostsByTagId(String id) {

        Tag existingTag = existingTagByIdOrThrow(id);

        return existingTag.getPosts().stream().map(post -> modelMapper.map(post, PostResponseDTO.class)).toList();
    }

    public ResponseEntity<TagRequestDTO> processCreateTag(TagRequestDTO tagRequestDTO) {
        Tag tag = new Tag(tagRequestDTO.getName(), tagRequestDTO.getSlug());

        tag = tagRepository.save(tag);

        TagRequestDTO createdTagRequestDTO = modelMapper.map(tag, TagRequestDTO.class);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(tag.getId()).toUri();

        return ResponseEntity.created(uri).body(createdTagRequestDTO);
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


    public ResponseEntity<Void> processUpdateTag(String id, TagRequestDTO updatedTagRequestDTO) {

        Tag existingTag = existingTagByIdOrThrow(id);

        updateTagProperties(existingTag, updatedTagRequestDTO);

        tagRepository.save(existingTag);

        return ResponseEntity.noContent().build();
    }

    private Tag existingTagByIdOrThrow(String id) {
        return tagRepository.findById(id).orElseThrow(() -> new TagNotFoundException(id));
    }

    private void updateTagProperties(Tag existingTag, TagRequestDTO updatedTagRequestDTO) {
        updateField(existingTag::setName, existingTag.getName(), updatedTagRequestDTO.getName());
        updateField(existingTag::setSlug, existingTag.getSlug(), updatedTagRequestDTO.getSlug());
    }

    private <T> void updateField(Consumer<T> setter, T currentValue, T newValue) {
        if (newValue != null && !newValue.equals(currentValue)) {
            setter.accept(newValue);
        }
    }
}
