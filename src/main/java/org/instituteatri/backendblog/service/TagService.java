package org.instituteatri.backendblog.service;

import lombok.RequiredArgsConstructor;
import org.instituteatri.backendblog.domain.entities.Tag;
import org.instituteatri.backendblog.infrastructure.exceptions.DomainAccessDeniedException;
import org.instituteatri.backendblog.infrastructure.exceptions.TagNotFoundException;
import org.instituteatri.backendblog.repository.TagRepository;
import org.instituteatri.backendblog.service.helpers.HelperValidateUser;
import org.instituteatri.backendblog.service.helpers.helpTag.HelperComponentUpdateTag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
    private final HelperValidateUser helperValidateUser;

    public List<Tag> findAllTags() {
        return tagRepository.findAll();
    }

    public Tag findById(String id) {
        Optional<Tag> tag = tagRepository.findById(id);
        return tag.orElseThrow(() -> new TagNotFoundException(id));
    }

    public ResponseEntity<Tag> processCreateTag(Tag tag, Authentication authentication) {
        if (helperValidateUser.isAdmin(authentication)) {
            throw new DomainAccessDeniedException();
        }

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(tag.getId()).toUri();
        return ResponseEntity.created(uri).body(tagRepository.save(tag));
    }

    public ResponseEntity<Tag> processUpdateTag(String id, Tag updatedTag, Authentication authentication) {
        if (helperValidateUser.isAdmin(authentication)) {
            throw new DomainAccessDeniedException();
        }

        helperComponentUpdateTag.helperUpdatedTag(updatedTag);
        updatedTag.setId(id);
        helperComponentUpdateTag.helperUpdate(id, updatedTag);

        return ResponseEntity.noContent().build();
    }

    public void deleteTag(String id, Authentication authentication) {
        if (helperValidateUser.isAdmin(authentication)) {
            throw new DomainAccessDeniedException();
        }

        Tag existingTag = findById(id);
        tagRepository.delete(existingTag);
    }
}
