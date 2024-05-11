package org.instituteatri.backendblog.service;

import lombok.RequiredArgsConstructor;
import org.instituteatri.backendblog.domain.entities.Category;
import org.instituteatri.backendblog.domain.entities.Post;
import org.instituteatri.backendblog.domain.entities.Tag;
import org.instituteatri.backendblog.domain.entities.User;
import org.instituteatri.backendblog.dto.request.PostRequestDTO;
import org.instituteatri.backendblog.dto.response.PostResponseDTO;
import org.instituteatri.backendblog.infrastructure.exceptions.NotAuthenticatedException;
import org.instituteatri.backendblog.infrastructure.exceptions.PostNotFoundException;
import org.instituteatri.backendblog.repository.CategoryRepository;
import org.instituteatri.backendblog.repository.PostRepository;
import org.instituteatri.backendblog.repository.TagRepository;
import org.instituteatri.backendblog.repository.UserRepository;
import org.instituteatri.backendblog.service.components.postcomponents.PostComponentFindAllUsers;
import org.instituteatri.backendblog.service.components.postcomponents.PostCreateComponent;
import org.instituteatri.backendblog.service.components.postcomponents.PostDeleteComponent;
import org.instituteatri.backendblog.service.components.postcomponents.PostUpdateComponent;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final TagRepository tagRepository;
    private final CategoryRepository categoryRepository;
    private final PostComponentFindAllUsers postFindAllUsers;
    private final PostUpdateComponent postUpdateComponent;
    private final PostCreateComponent postCreateComponent;
    private final PostDeleteComponent postDeleteComponent;
    private final ModelMapper modelMapper;

    public ResponseEntity<List<PostResponseDTO>> processFindAllPosts() {

        List<Post> posts = postRepository.findAll();
        updateEntitiesInThePostList(posts);

        List<Post> updatedPosts = postRepository.findAll();
        List<PostResponseDTO> responseDTOs = new ArrayList<>();

        updatedPosts.forEach(x -> responseDTOs.add(modelMapper.map(x, PostResponseDTO.class)));
        return ResponseEntity.ok(responseDTOs);
    }

    public PostResponseDTO processFindById(String id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException(id));
        return modelMapper.map(post, PostResponseDTO.class);
    }

    public ResponseEntity<PostRequestDTO> processCreatePost(PostRequestDTO postRequestDTO, Authentication authentication) {

        User currentUser = getCurrentUser(authentication);
        validateCurrentUser(currentUser);

        PostRequestDTO createdPost = postCreateComponent.createNewPostDTOComponent(postRequestDTO, currentUser);
        String baseUri = "http://localhost:8080";

        URI uri = UriComponentsBuilder
                .fromUriString(baseUri)
                .path("/{id}")
                .buildAndExpand(createdPost.getId())
                .toUri();

        return ResponseEntity.created(uri).body(createdPost);
    }

    public ResponseEntity<Void> processUpdatePost(String id, PostRequestDTO updatedPostRequestDto, User currentUser) {

        validateCurrentUser(currentUser);

        Post existingPost = getExistingPost(id);

        postUpdateComponent.verifyUserAuthorizationForPostUpdate(existingPost, currentUser);

        postUpdateComponent.updatePostPropertiesComponent(existingPost, updatedPostRequestDto);

        postRepository.save(existingPost);

        return ResponseEntity.noContent().build();
    }

    public ResponseEntity<Void> processDeletePost(String id, Authentication authentication) {

        User currentUser = getCurrentUser(authentication);

        Post existingPost = getExistingPost(id);

        postDeleteComponent.validatePostDeleteComponent(existingPost, currentUser);

        decrementCategoryAndTagCounts(existingPost);

        postRepository.deleteById(existingPost.getId());

        postDeleteComponent.decrementPostCountComponent(currentUser);

        return ResponseEntity.noContent().build();
    }

    private Post getExistingPost(String id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException(id));
    }

    private void decrementCategoryAndTagCounts(Post post) {
        post.getCategories().forEach(category -> postDeleteComponent.decrementCategoryPostCountComponent(category.getId(), post.getId()));
        post.getTags().forEach(tag -> postDeleteComponent.decrementTagPostCountComponent(tag.getId(), post.getId()));
    }

    private void updateEntitiesInThePostList(List<Post> posts) {
        List<User> updatedUsers = userRepository.findAll();
        List<Tag> updatedTags = tagRepository.findAll();
        List<Category> updatedCategories = categoryRepository.findAll();

        for (Post post : posts) {
            String userId = post.getUser().getId();
            if (updatedUsers.stream().noneMatch(user -> user.getId().equals(userId))) {
                postRepository.delete(post);
            } else {
                postFindAllUsers.updatePostAuthorWithUpdatedUser(post, updatedUsers);
                postFindAllUsers.updatePostTagsWithUpdatedEntities(post, updatedTags);
                postFindAllUsers.updatePostCategoriesWithUpdatedEntities(post, updatedCategories);
                postRepository.save(post);
            }
        }
    }

    private void validateCurrentUser(User currentUser) {
        if (currentUser == null) {
            throw new NotAuthenticatedException();
        }
    }

    private User getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new NotAuthenticatedException();
        }
        return (User) authentication.getPrincipal();
    }
}