package org.instituteatri.backendblog.service;

import lombok.RequiredArgsConstructor;
import org.instituteatri.backendblog.domain.entities.Category;
import org.instituteatri.backendblog.domain.entities.Post;
import org.instituteatri.backendblog.domain.entities.Tag;
import org.instituteatri.backendblog.domain.entities.User;
import org.instituteatri.backendblog.dtos.PostDTO;
import org.instituteatri.backendblog.infrastructure.exceptions.NotAuthenticatedException;
import org.instituteatri.backendblog.infrastructure.exceptions.PostNotFoundException;
import org.instituteatri.backendblog.mappings.PostMapper;
import org.instituteatri.backendblog.repository.CategoryRepository;
import org.instituteatri.backendblog.repository.PostRepository;
import org.instituteatri.backendblog.repository.TagRepository;
import org.instituteatri.backendblog.repository.UserRepository;
import org.instituteatri.backendblog.service.components.postcomponents.PostComponentFindAllUsers;
import org.instituteatri.backendblog.service.components.postcomponents.PostCreateComponent;
import org.instituteatri.backendblog.service.components.postcomponents.PostDeleteComponent;
import org.instituteatri.backendblog.service.components.postcomponents.PostUpdateComponent;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostMapper postMapper;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final TagRepository tagRepository;
    private final CategoryRepository categoryRepository;
    private final PostComponentFindAllUsers postFindAllUsers;
    private final PostUpdateComponent postUpdateComponent;
    private final PostCreateComponent postCreateComponent;
    private final PostDeleteComponent postDeleteComponent;


    public ResponseEntity<List<PostDTO>> processFindAllPosts() {
        List<Tag> updatedTags = tagRepository.findAll();
        List<Category> updatedCategories = categoryRepository.findAll();
        List<User> updatedUsers = userRepository.findAll();

        List<Post> posts = postRepository.findAll();

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

        List<Post> updatedPosts = postRepository.findAll();

        return ResponseEntity.ok(updatedPosts.stream()
                .map(postMapper::toPostDto)
                .toList());
    }

    public PostDTO processFindById(String id) {
        Optional<Post> post = postRepository.findById(id);

        return post.map(postMapper::toPostDto).orElseThrow(() -> new PostNotFoundException(id));
    }

    public ResponseEntity<PostDTO> processCreatePost(PostDTO postDTO, Authentication authentication) {
        User currentUser = getCurrentUser(authentication);
        validateCurrentUser(currentUser);

        PostDTO createdPost = postCreateComponent.createNewPostDTOComponent(postDTO, currentUser);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdPost.id())
                .toUri();

        return ResponseEntity.created(uri).body(createdPost);
    }

    public ResponseEntity<Void> processUpdatePost(String id, PostDTO updatedPostDto, User currentUser) {
        validateCurrentUser(currentUser);

        Post existingPost = postUpdateComponent.findPostByIdComponent(id);

        postUpdateComponent.authorizePostUpdateComponent(existingPost, currentUser);

        postUpdateComponent.updatePostPropertiesComponent(existingPost, updatedPostDto);

        postRepository.save(existingPost);

        return ResponseEntity.noContent().build();
    }

    public ResponseEntity<Void> processDeletePost(String id, Authentication authentication) {
        User currentUser = getCurrentUser(authentication);
        Post existingPost = postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException(id));
        postDeleteComponent.validatePostDeleteComponent(existingPost, currentUser);

        existingPost.getCategories().forEach(category -> postDeleteComponent.decrementCategoryPostCountComponent(category.getId(), id));
        existingPost.getTags().forEach(tag -> postDeleteComponent.decrementTagPostCountComponent(tag.getId(), id));

        postRepository.deleteById(id);
        postDeleteComponent.decrementPostCountComponent(currentUser);
        return ResponseEntity.noContent().build();
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