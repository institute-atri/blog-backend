package org.instituteatri.backendblog.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.instituteatri.backendblog.domain.entities.Post;
import org.instituteatri.backendblog.domain.entities.User;
import org.instituteatri.backendblog.dtos.PostDTO;
import org.instituteatri.backendblog.service.PostService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/post")
@RequiredArgsConstructor
@Tag(name = "Posts", description = "Endpoints for managing Posts")
public class PostController {

    private final PostService postService;


    @Operation(
            method = "GET",
            summary = "Get all posts.",
            description = "Returns a list of posts.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "-Posts list successfully returned.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "[" +
                                            "  {" +
                                            "    \"id\": \"6620a722185c5e3107f13732\"," +
                                            "    \"title\": \"title\"," +
                                            "    \"summary\": \"summary\"," +
                                            "    \"body\": \"body\"," +
                                            "    \"slug\": \"slug\"," +
                                            "    \"createdAt\": \"2024-04-18T01:52:50.928\"," +
                                            "    \"updatedAt\": null," +
                                            "    \"categories\": [" +
                                            "      {" +
                                            "        \"id\": \"6620a722185c5e3107f13730\"," +
                                            "        \"name\": \"Technology\"," +
                                            "        \"slug\": \"tech\"" +
                                            "      }" +
                                            "    ]," +
                                            "    \"tags\": [" +
                                            "      {" +
                                            "        \"id\": \"6620a722185c5e3107f13731\"," +
                                            "        \"name\": \"Java\"," +
                                            "        \"slug\": \"java\"" +
                                            "      }" +
                                            "    ]," +
                                            "    \"authorDTO\": {" +
                                            "      \"name\": \"Rafael\"," +
                                            "      \"lastName\": \"Silva\"" +
                                            "    }," +
                                            "    \"comments\": [" +
                                            "      {" +
                                            "        \"text\": \"Falando sobre java\"," +
                                            "        \"createdAt\": \"2024-04-18T01:52:50.928\"," +
                                            "        \"updatedAt\": null," +
                                            "        \"authorDTO\": {" +
                                            "          \"name\": \"User\"," +
                                            "          \"lastName\": \"Test\"" +
                                            "        }" +
                                            "      }" +
                                            "    ]" +
                                            "  }" +
                                            "]"))),

    })
    @GetMapping("/posts")
    public ResponseEntity<List<Post>> findAllPosts() {
        return ResponseEntity.ok(postService.findAllPosts());
    }


    @Operation(
            method = "GET",
            summary = "Find post by ID.",
            description = "Returns the post with the specified ID.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "-Posts successfully found.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "[" +
                                            "  {" +
                                            "    \"id\": \"6620a722185c5e3107f13732\"," +
                                            "    \"title\": \"title\"," +
                                            "    \"summary\": \"summary\"," +
                                            "    \"body\": \"body\"," +
                                            "    \"slug\": \"slug\"," +
                                            "    \"createdAt\": \"2024-04-18T01:52:50.928\"," +
                                            "    \"updatedAt\": null," +
                                            "    \"categories\": [" +
                                            "      {" +
                                            "        \"id\": \"6620a722185c5e3107f13730\"," +
                                            "        \"name\": \"Technology\"," +
                                            "        \"slug\": \"tech\"" +
                                            "      }" +
                                            "    ]," +
                                            "    \"tags\": [" +
                                            "      {" +
                                            "        \"id\": \"6620a722185c5e3107f13731\"," +
                                            "        \"name\": \"Java\"," +
                                            "        \"slug\": \"java\"" +
                                            "      }" +
                                            "    ]," +
                                            "    \"authorDTO\": {" +
                                            "      \"name\": \"Rafael\"," +
                                            "      \"lastName\": \"Silva\"" +
                                            "    }," +
                                            "    \"comments\": [" +
                                            "      {" +
                                            "        \"text\": \"Falando sobre java\"," +
                                            "        \"createdAt\": \"2024-04-18T01:52:50.928\"," +
                                            "        \"updatedAt\": null," +
                                            "        \"authorDTO\": {" +
                                            "          \"name\": \"User\"," +
                                            "          \"lastName\": \"User\"" +
                                            "        }" +
                                            "      }" +
                                            "    ]" +
                                            "  }" +
                                            "]"))),
            @ApiResponse(
                    responseCode = "404",
                    description = "-Post not found.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{" +
                                            "\"message\":\"Could not find post with id:661eff024af2c96e8a7deda9\"" +
                                            "}")))

    })
    @GetMapping("/{id}")
    public ResponseEntity<Post> findByIdPost(@PathVariable String id) {
        return ResponseEntity.ok(postService.findById(id));
    }


    @Operation(
            method = "POST",
            summary = "Create a new post.",
            description = "Create a new post. Only with authenticated users.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "-Post successfully created.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{" +
                                            "    \"id\": \"6620b59605666035d093e091\"," +
                                            "    \"title\": \"title example\"," +
                                            "    \"summary\": \"summary example\"," +
                                            "    \"body\": \"bodies example\"," +
                                            "    \"slug\": \"slug example\"," +
                                            "    \"createdAt\": \"2024-04-18T02:54:29.9783407\"," +
                                            "    \"updatedAt\": null," +
                                            "    \"categories\": [" +
                                            "        {" +
                                            "            \"id\": \"6620b57c05666035d093e08a\"," +
                                            "            \"name\": \"Technology\"," +
                                            "            \"slug\": \"tech\"" +
                                            "        }" +
                                            "    ]," +
                                            "    \"tags\": [" +
                                            "        {" +
                                            "            \"id\": \"6620b57c05666035d093e08b\"," +
                                            "            \"name\": \"Java\"," +
                                            "            \"slug\": \"java\"" +
                                            "        }" +
                                            "    ]," +
                                            "    \"authorDTO\": {" +
                                            "        \"name\": \"User\"," +
                                            "        \"lastName\": \"User\"" +
                                            "    }," +
                                            "    \"comments\": []" +
                                            "}"))),

            @ApiResponse(responseCode = "400", description = "-Property validation ",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{" +
                                            "\"title\": [\"Title is required.\", \"Title must be between 5 and 30 characters.\"]," +
                                            "\"summary\": [\"Summary is required.\", \"Summary must be between 5 and 100 characters.\"]," +
                                            "\"body\": [\"Body is required.\", \"Body must be between 5 and 1000 characters.\"]," +
                                            "\"slug\": [\"Slug is required.\", \"Slug must be between 3 and 50 characters.\"]" +
                                            "}"
                            ))),

            @ApiResponse(responseCode = "401", description = "-Unauthenticated user.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"User isn't authenticated.\"}"
                            ))),

            @ApiResponse(
                    responseCode = "404",
                    description = "Tag and Category not found.",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "[{\"message\":\"Could not find tag with id:661eff024af2c96e8a7deda9\"}," +
                                            "{\"message\":\"Could not find category with id:661eff024af2c96e8a7deda9\"}]"
                            )))

    })
    @PostMapping("/create")
    public ResponseEntity<Post> createPost(@RequestBody @Valid PostDTO postDTO, Authentication authentication) {
        return postService.processCreatePost(postDTO, authentication);
    }


    @Operation(
            method = "PUT",
            summary = "Update an existing post.",
            description = "Updates an existing post. Only with authenticated users.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "-Post successfully updated.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{" +
                                            "    \"title\": \"Changes post\"," +
                                            "    \"body\": \"Changes\"," +
                                            "    \"summary\": \"Changes summary\"," +
                                            "    \"slug\": \"Changes slug\"" +
                                            "}"))),

            @ApiResponse(responseCode = "400", description = "-Property validation ",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{" +
                                            "\"title\": [\"Title is required.\", \"Title must be between 5 and 30 characters.\"]," +
                                            "\"summary\": [\"Summary is required.\", \"Summary must be between 5 and 100 characters.\"]," +
                                            "\"body\": [\"Body is required.\", \"Body must be between 5 and 1000 characters.\"]," +
                                            "\"slug\": [\"Slug is required.\", \"Slug must be between 3 and 50 characters.\"]" +
                                            "}"
                            ))),

            @ApiResponse(responseCode = "401", description = "-Unauthenticated user.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"User isn't authenticated.\"}"
                            ))),

            @ApiResponse(responseCode = "403", description = "-Unauthorized user.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"User isn't authorized.\"}"
                            ))),

            @ApiResponse(responseCode = "404", description = "-Post not found.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"Could not find post with id:661eff024af2c96e8a7deda9\"}"
                            )))
    })
    @PutMapping("/{id}")
    public ResponseEntity<Post> updatePost(
            @PathVariable String id,
            @RequestBody @Valid PostDTO postDTO,
            @AuthenticationPrincipal User currentUser
    ) {
        return postService.processUpdatePost(id, postDTO, currentUser);
    }


    @Operation(
            method = "DELETE",
            summary = "Delete an existing post.",
            description = "Deletes an existing post. Only with authenticated users.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204", description = "-Post successfully deleted."),
            @ApiResponse(responseCode = "403", description = "-Unauthorized user."),
            @ApiResponse(responseCode = "404", description = "-Post not found.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{" +
                                            "\"message\":\"Could not find post with id:661eff024af2c96e8a7deda9\"" +
                                            "}")))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable String id, Authentication authentication) {
        postService.deletePost(id, authentication);
        return ResponseEntity.noContent().build();
    }
}
