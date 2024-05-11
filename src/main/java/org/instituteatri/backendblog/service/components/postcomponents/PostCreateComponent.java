package org.instituteatri.backendblog.service.components.postcomponents;

import lombok.RequiredArgsConstructor;
import org.instituteatri.backendblog.domain.entities.Category;
import org.instituteatri.backendblog.domain.entities.Post;
import org.instituteatri.backendblog.domain.entities.Tag;
import org.instituteatri.backendblog.domain.entities.User;
import org.instituteatri.backendblog.dto.request.PostRequestDTO;
import org.instituteatri.backendblog.dto.response.AuthorResponseDTO;
import org.instituteatri.backendblog.infrastructure.exceptions.CategoryNotFoundException;
import org.instituteatri.backendblog.infrastructure.exceptions.TagNotFoundException;
import org.instituteatri.backendblog.repository.CategoryRepository;
import org.instituteatri.backendblog.repository.PostRepository;
import org.instituteatri.backendblog.repository.TagRepository;
import org.instituteatri.backendblog.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PostCreateComponent {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final PostLoadEntitiesComponent postLoadEntitiesComponent;
    private final ModelMapper modelMapper;

    public PostRequestDTO createNewPostDTOComponent(PostRequestDTO postRequestDTO, User currentUser) {

        Post newPost = createPostFromDTO(postRequestDTO);

        setAuthorAndUser(newPost, currentUser);

        loadAndSetCategoriesAndTags(newPost, postRequestDTO.getCategories(), postRequestDTO.getTags());

        Post createdPost = postRepository.save(newPost);

        incrementCategoryAndTagCounts(createdPost);

        updateCurrentUser(currentUser, createdPost);

        return modelMapper.map(createdPost, PostRequestDTO.class);
    }

    private Post createPostFromDTO(PostRequestDTO postRequestDTO) {
        Post newPost = new Post();
        newPost.setTitle(postRequestDTO.getTitle());
        newPost.setSummary(postRequestDTO.getSummary());
        newPost.setBody(postRequestDTO.getBody());
        newPost.setSlug(postRequestDTO.getSlug());
        newPost.setCreatedAt(LocalDateTime.now());
        return newPost;
    }

    private void setAuthorAndUser(Post post, User user) {
        AuthorResponseDTO authorDTO = new AuthorResponseDTO(user.getName(), user.getLastName());
        post.setAuthorResponseDTO(authorDTO);
        post.setUser(user);
    }

    private void loadAndSetCategoriesAndTags(Post post, List<Category> categories, List<Tag> tags) {
        List<Category> loadCategories = postLoadEntitiesComponent.loadUniqueCategoriesFromDatabase(categories);
        List<Tag> loadTags = postLoadEntitiesComponent.loadUniqueTagsFromDatabase(tags);
        post.setCategories(loadCategories);
        post.setTags(loadTags);
    }

    private void incrementCategoryAndTagCounts(Post post) {
        post.getCategories().forEach(category -> incrementCategoryPostCountComponent(category.getId(), post));
        post.getTags().forEach(tag -> incrementTagPostCountComponent(tag.getId(), post));
    }

    private void updateCurrentUser(User user, Post post) {
        user.getPosts().add(post);
        userRepository.save(user);
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
