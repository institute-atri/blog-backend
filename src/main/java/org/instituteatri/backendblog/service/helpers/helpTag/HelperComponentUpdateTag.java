package org.instituteatri.backendblog.service.helpers.helpTag;

import lombok.RequiredArgsConstructor;
import org.instituteatri.backendblog.domain.entities.Tag;
import org.instituteatri.backendblog.dtos.TagDTO;
import org.instituteatri.backendblog.infrastructure.exceptions.TagNotFoundException;
import org.instituteatri.backendblog.repository.TagRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HelperComponentUpdateTag {

    private final TagRepository tagRepository;

    public void helperUpdate(String id, TagDTO  updatedTagDTO) {
        Tag existingTag = tagRepository.findById(id)
                .orElseThrow(() -> new TagNotFoundException(id));

        helperUpdateName(existingTag, updatedTagDTO.name());
        helperUpdateSlug(existingTag, updatedTagDTO.slug());

        tagRepository.save(existingTag);
    }
    public void helperUpdatedTag(TagDTO tagDTO) {
        new Tag(tagDTO.name(), tagDTO.slug());
    }

    private void helperUpdateName(Tag existingTag, String newName) {
        if (newName != null && !newName.equals(existingTag.getName())) {
            existingTag.setName(newName);
        }
    }

    private void helperUpdateSlug(Tag existingTag, String newSlug) {
        if (newSlug != null && !newSlug.equals(existingTag.getSlug())) {
            existingTag.setSlug(newSlug);
        }
    }
}
