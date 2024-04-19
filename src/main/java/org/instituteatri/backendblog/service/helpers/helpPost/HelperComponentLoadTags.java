package org.instituteatri.backendblog.service.helpers.helpPost;

import lombok.RequiredArgsConstructor;
import org.instituteatri.backendblog.domain.entities.Tag;
import org.instituteatri.backendblog.infrastructure.exceptions.TagNotFoundException;
import org.instituteatri.backendblog.repository.TagRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class HelperComponentLoadTags {

    private final TagRepository tagRepository;

    public List<Tag> loadTags(List<Tag> tags) {
        List<Tag> loadedTags = new ArrayList<>();
        for (Tag tag : tags) {
            Tag loadedTag = tagRepository.findById(tag.getId())
                    .orElseThrow(() -> new TagNotFoundException(tag.getId()));
            loadedTags.add(loadedTag);
        }
        return loadedTags;
    }
}
