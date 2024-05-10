package org.instituteatri.backendblog.service.components.postcomponents;

import lombok.RequiredArgsConstructor;
import org.instituteatri.backendblog.domain.entities.Category;
import org.instituteatri.backendblog.domain.entities.Post;
import org.instituteatri.backendblog.domain.entities.Tag;
import org.instituteatri.backendblog.domain.entities.User;
import org.instituteatri.backendblog.dto.response.AuthorResponseDTO;
import org.instituteatri.backendblog.dto.request.PostRequestDTO;
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

    public PostRequestDTO createNewPostDTOComponent(PostRequestDTO postRequestDTO, User currentUser) {
        Post post = new Post();
        post.setTitle(postRequestDTO.title());
        post.setSummary(postRequestDTO.summary());
        post.setBody(postRequestDTO.body());
        post.setSlug(postRequestDTO.slug());

        postMapper.createPostFromDto(postRequestDTO, post);

        post.setAuthorResponseDTO(new AuthorResponseDTO(currentUser.getName(), currentUser.getLastName()));

        post.setUser(currentUser);

        List<Category> categories = postLoadEntitiesComponent.loadCategoriesComponent(postRequestDTO.categories());
        List<Tag> tags = postLoadEntitiesComponent.loadTagsComponent(postRequestDTO.tags());

        post.setCategories(categories);
        post.setTags(tags);

        Post createdPost = postRepository.save(post);
        PostRequestDTO createdPostRequestDTO = postMapper.toPostDto(createdPost);

        categories.forEach(category -> incrementCategoryPostCountComponent(category.getId(), createdPost));
        tags.forEach(tag -> incrementTagPostCountComponent(tag.getId(), createdPost));

        currentUser.getPosts().add(createdPost);
        userRepository.save(currentUser);

        return createdPostRequestDTO;
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
