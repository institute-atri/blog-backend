package org.instituteatri.backendblog.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.instituteatri.backendblog.domain.entities.Post;
import org.instituteatri.backendblog.service.PostService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class AccountController {
    private final PostService postService;


    @PostMapping("/login")
    public ResponseEntity<Post> login(@RequestBody @Valid Post post, Authentication authentication) {
        return postService.processCreatePost(post, authentication);
    }

    @PostMapping("/register")
    public ResponseEntity<Post> register(@RequestBody @Valid Post post, Authentication authentication) {
        return postService.processCreatePost(post, authentication);
    }


}