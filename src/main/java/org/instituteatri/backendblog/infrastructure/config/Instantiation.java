package org.instituteatri.backendblog.infrastructure.config;

import lombok.RequiredArgsConstructor;
import org.instituteatri.backendblog.domain.entities.*;
import org.instituteatri.backendblog.repository.CategoryRepository;
import org.instituteatri.backendblog.repository.PostRepository;
import org.instituteatri.backendblog.repository.TagRepository;
import org.instituteatri.backendblog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${admin.password}")
    private String adminPassword;

    @Value("${user.password}")
    private String userPassword;

    @Override
    public void run(String... args) {
        userRepository.deleteAll();
        postRepository.deleteAll();
        tagRepository.deleteAll();
        categoryRepository.deleteAll();

        String encryptedAdminPassword = passwordEncoder.encode(adminPassword);
        User admin = new User(
                "Rafael",
                "Silva",
                "40028922",
                "Bio",
                "admin@localhost.com",
                encryptedAdminPassword,
                true,
                UserRole.ADMIN);
        userRepository.save(admin);

        Category category1 = new Category("Technology", "tech");
        categoryRepository.save(category1);

        Tag tag1 = new Tag("Java", "java");
        tagRepository.save(tag1);

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

        String encryptedUserPassword = passwordEncoder.encode(userPassword);

        User user = new User(
                "User",
                "Test",
                "48665456",
                "Bio",
                "user@localhost.com",
                encryptedUserPassword,
                true,
                UserRole.USER);
        userRepository.save(user);

        Category category2 = new Category("Business", "business");
        categoryRepository.save(category2);

        Tag tag2 = new Tag("C#", "csharp");
        tagRepository.save(tag2);

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

        Comment comment1 = new Comment("Falando sobre java", createdAt, user);
        Comment comment2 = new Comment("Falando sobre c#", createdAt, admin);

        post1.getComments().add(comment1);
        post2.getComments().add(comment2);
        postRepository.saveAll(Arrays.asList(post1, post2));


        category1.getPosts().add(post1);
        category2.getPosts().add(post2);
        categoryRepository.saveAll(Arrays.asList(category1, category2));

        tag1.getPosts().add(post1);
        tag2.getPosts().add(post2);
        tagRepository.saveAll(Arrays.asList(tag1, tag2));

        admin.getPosts().add(post1);
        user.getPosts().add(post2);
        userRepository.saveAll(Arrays.asList(admin, user));
    }
}
