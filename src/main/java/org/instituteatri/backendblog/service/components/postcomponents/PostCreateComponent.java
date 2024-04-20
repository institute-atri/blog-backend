package org.instituteatri.backendblog.service.components.postComponents;

import lombok.RequiredArgsConstructor;
import org.instituteatri.backendblog.domain.entities.Category;
import org.instituteatri.backendblog.domain.entities.Post;
import org.instituteatri.backendblog.domain.entities.Tag;
import org.instituteatri.backendblog.domain.entities.User;
import org.instituteatri.backendblog.dtos.AuthorDTO;
import org.instituteatri.backendblog.dtos.PostDTO;
import org.instituteatri.backendblog.infrastructure.exceptions.CategoryNotFoundException;
import org.instituteatri.backendblog.infrastructure.exceptions.TagNotFoundException;
import org.instituteatri.backendblog.mappings.PostMapper;
import org.instituteatri.backendblog.repository.CategoryRepository;
import org.instituteatri.backendblog.repository.PostRepository;
import org.instituteatri.backendblog.repository.TagRepository;
import org.instituteatri.backendblog.repository.UserRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PostCreateComponent {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final PostMapper postMapper;
    private final PostLoadEntitiesComponent postLoadEntitiesComponent;

    public PostDTO createNewPostDTOComponent(PostDTO postDTO, User currentUser) {
        Post post = new Post();
        post.setTitle(postDTO.title());
        post.setSummary(postDTO.summary());
        post.setBody(postDTO.body());
        post.setSlug(postDTO.slug());

        postMapper.createPostFromDto(postDTO, post);

        post.setAuthorDTO(new AuthorDTO(currentUser.getName(), currentUser.getLastName()));

        post.setUser(currentUser);

        List<Category> categories = postLoadEntitiesComponent.loadCategoriesComponent(postDTO.categories());
        List<Tag> tags = postLoadEntitiesComponent.loadTagsComponent(postDTO.tags());

        post.setCategories(categories);
        post.setTags(tags);

        Post createdPost = postRepository.save(post);
        PostDTO createdPostDTO = postMapper.toPostDto(createdPost);

        categories.forEach(category -> incrementCategoryPostCountComponent(category.getId(), createdPost));
        tags.forEach(tag -> incrementTagPostCountComponent(tag.getId(), createdPost));

        currentUser.getPosts().add(createdPost);
        userRepository.save(currentUser);

        return createdPostDTO;
    }

    private void incrementCategoryPostCountComponent(String categoryId, Post post) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException(categoryId));
        category.setPostCount(category.getPostCount() + 1);
        category.getPosts().add(post);
        categoryRepository.save(category);
    }


    private void incrementTagPostCountComponent(String tagId, Post post) {
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new TagNotFoundException(tagId));
        tag.setPostCount(tag.getPostCount() + 1);
        tag.getPosts().add(post);
        tagRepository.save(tag);
    }
}
