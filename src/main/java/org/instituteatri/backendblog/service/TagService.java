package org.instituteatri.backendblog.service;

import lombok.RequiredArgsConstructor;
import org.instituteatri.backendblog.domain.entities.Post;
import org.instituteatri.backendblog.domain.entities.Tag;
import org.instituteatri.backendblog.dtos.TagDTO;
import org.instituteatri.backendblog.infrastructure.exceptions.TagNotFoundException;
import org.instituteatri.backendblog.repository.TagRepository;
import org.instituteatri.backendblog.service.helpers.helpTag.HelperComponentUpdateTag;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;
    private final HelperComponentUpdateTag helperComponentUpdateTag;

    public List<Post> findPostsByTagId(String tagId) {
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new TagNotFoundException(tagId));
        return tag.getPosts();
    }

    public List<Tag> findAllTags() {
        return tagRepository.findAll();
    }

    public Tag findById(String id) {
        Optional<Tag> tag = tagRepository.findById(id);
        return tag.orElseThrow(() -> new TagNotFoundException(id));
    }

    public ResponseEntity<Tag> processCreateTag(TagDTO tagDTO) {
        Tag tag = new Tag(tagDTO.name(), tagDTO.slug());

        tag = tagRepository.save(tag);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(tag.getId())
                .toUri();
        return ResponseEntity.created(uri).body(tag);
    }

    public ResponseEntity<Void> processUpdateTag(String id, TagDTO updatedTagDTO) {
        helperComponentUpdateTag.helperUpdatedTag(updatedTagDTO);
        helperComponentUpdateTag.helperUpdate(id, updatedTagDTO);

        return ResponseEntity.noContent().build();
    }

    public void deleteTag(String id) {
        Tag existingTag = findById(id);
        tagRepository.delete(existingTag);
    }
}
