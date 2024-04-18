package org.instituteatri.backendblog.infrastructure.config;

import lombok.RequiredArgsConstructor;
import org.instituteatri.backendblog.domain.entities.*;
import org.instituteatri.backendblog.repository.CategoryRepository;
import org.instituteatri.backendblog.repository.PostRepository;
import org.instituteatri.backendblog.repository.TagRepository;
import org.instituteatri.backendblog.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Arrays;

@Configuration
@RequiredArgsConstructor
public class Instantiation implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PostRepository postRepository;
    private final TagRepository tagRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public void run(String... args) {
        // Clear all repository data
        userRepository.deleteAll();
        postRepository.deleteAll();
        tagRepository.deleteAll();
        categoryRepository.deleteAll();

        // Create an admin user
        String encryptedPassword = passwordEncoder.encode("@Visual23k+");
        User admin = new User(
                "Rafael",
                "Silva",
                "40028922",
                "Bio",
                "admin@localhost.com",
                encryptedPassword,
                true,
                UserRole.ADMIN);
        userRepository.save(admin);

        // Create and save a category
        Category category1 = new Category("Technology", "tech");
        categoryRepository.save(category1);

        // Create and save a tag
        Tag tag1 = new Tag("Java", "java");
        tagRepository.save(tag1);

        // Create and save a post
        LocalDateTime createdAt = LocalDateTime.now();
        Post post1 = new Post(
                "title",
                "summary",
                "body",
                "slug",
                createdAt,
                admin
        );
        post1.getCategories().add(category1);
        post1.getTags().add(tag1);
        postRepository.save(post1);

        // Create a new user
        User user = new User(
                "User",
                "Test",
                "48665456",
                "Bio",
                "user@localhost.com",
                encryptedPassword,
                true,
                UserRole.USER);
        userRepository.save(user);

        // Create and save a new category
        Category category2 = new Category("Business", "business");
        categoryRepository.save(category2);

        // Create and save a new tag
        Tag tag2 = new Tag("C#", "csharp");
        tagRepository.save(tag2);

        // Create and save a new post
        Post post2 = new Post(
                "title",
                "summary",
                "body",
                "slug",
                createdAt,
                user
        );
        post2.getCategories().add(category2);
        post2.getTags().add(tag2);
        postRepository.save(post2);

        // Create and save comments and associate them with posts
        Comment comment1 = new Comment("Falando sobre java", createdAt, user);
        Comment comment2 = new Comment("Falando sobre c#", createdAt, admin);

        post1.getComments().add(comment1);
        post2.getComments().add(comment2);
        postRepository.saveAll(Arrays.asList(post1, post2));


        // For each post, add the post to the list of posts in the corresponding categories
        for (Post post : Arrays.asList(post1, post2)) {
            for (Category category : post.getCategories()) {
                category.getPosts().add(post);
            }
        }

        // Save the updated categories in the repository
        categoryRepository.saveAll(Arrays.asList(category1, category2));

        for (Post post : Arrays.asList(post1, post2)) {
            for (Tag tag : post.getTags()) {
                tag.getPosts().add(post);
            }
        }

        // Save the updated tags in the repository
        tagRepository.saveAll(Arrays.asList(tag1, tag2));


        // Update the list of user posts
        admin.getPosts().add(post1);
        user.getPosts().add(post2);
        userRepository.saveAll(Arrays.asList(admin, user));
    }
}
