package org.instituteatri.backendblog.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
            summary = "Get all the events.",
            description = "Collection of events.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Success.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "[" +
                                            "  {" +
                                            "    \"id\": \"string\"," +
                                            "    \"title\": \"string\"," +
                                            "    \"summary\": \"string\"," +
                                            "    \"body\": \"string\"," +
                                            "    \"slug\": \"string\"," +
                                            "    \"createdAt\": \"2024-04-18T01:52:50.928\"," +
                                            "    \"updatedAt\": null," +
                                            "    \"authorDTO\": {" +
                                            "      \"name\": \"string\"," +
                                            "      \"lastName\": \"string\"" +
                                            "    }," +
                                            "    \"categories\": [" +
                                            "      {" +
                                            "        \"id\": \"string\"," +
                                            "        \"name\": \"string\"," +
                                            "        \"slug\": \"string\"" +
                                            "      }" +
                                            "    ]," +
                                            "    \"tags\": [" +
                                            "      {" +
                                            "        \"id\": \"string\"," +
                                            "        \"name\": \"string\"," +
                                            "        \"slug\": \"string\"" +
                                            "      }" +
                                            "    ]," +
                                            "    \"comments\": [" +
                                            "      {" +
                                            "        \"text\": \"string\"," +
                                            "        \"createdAt\": \"2024-04-18T01:52:50.928\"," +
                                            "        \"updatedAt\": null," +
                                            "        \"authorDTO\": {" +
                                            "          \"name\": \"string\"," +
                                            "          \"lastName\": \"string\"" +
                                            "        }" +
                                            "      }" +
                                            "    ]" +
                                            "  }" +
                                            "]"))),

    })
    @GetMapping("/posts")
    public ResponseEntity<List<PostDTO>> findAllPosts() {
        return postService.processFindAllPosts();
    }


    @Operation(
            method = "GET",
            summary = "Get an event.",
            description = "Event identifier {id}.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Success.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "[" +
                                            "  {" +
                                            "    \"id\": \"string\"," +
                                            "    \"title\": \"string\"," +
                                            "    \"summary\": \"string\"," +
                                            "    \"body\": \"string\"," +
                                            "    \"slug\": \"string\"," +
                                            "    \"createdAt\": \"2024-04-18T01:52:50.928\"," +
                                            "    \"updatedAt\": null," +
                                            "    \"authorDTO\": {" +
                                            "      \"name\": \"string\"," +
                                            "      \"lastName\": \"string\"" +
                                            "    }," +
                                            "    \"categories\": [" +
                                            "      {" +
                                            "        \"id\": \"string\"," +
                                            "        \"name\": \"string\"," +
                                            "        \"slug\": \"string\"" +
                                            "      }" +
                                            "    ]," +
                                            "    \"tags\": [" +
                                            "      {" +
                                            "        \"id\": \"string\"," +
                                            "        \"name\": \"string\"," +
                                            "        \"slug\": \"string\"" +
                                            "      }" +
                                            "    ]," +
                                            "    \"comments\": [" +
                                            "      {" +
                                            "        \"text\": \"string\"," +
                                            "        \"createdAt\": \"2024-04-18T01:52:50.928\"," +
                                            "        \"updatedAt\": null," +
                                            "        \"authorDTO\": {" +
                                            "          \"name\": \"string\"," +
                                            "          \"lastName\": \"string\"" +
                                            "        }" +
                                            "      }" +
                                            "    ]" +
                                            "  }" +
                                            "]"))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not found.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{" +
                                            "\"message\":\"Could not find post with id:661eff024af2c96e8a7deda9\"" +
                                            "}")))

    })
    @GetMapping("/{id}")
    public ResponseEntity<PostDTO> findByIdPost(@PathVariable String id) {
        return ResponseEntity.ok(postService.processFindById(id));
    }


    @Operation(
            method = "POST",
            summary = "Create an event.",
            description = "Creates a new post based on a JSON object in the request body. The JSON must contain: " +
                    "'title' (String)," +
                    " 'summary' (String)," +
                    " 'body' (String)," +
                    " 'slug' (String), " +
                    "'categories' (array of 'id' as String), and" +
                    " 'tags' (array of 'id' as String). " +
                    "Requires authentication." +
                    "{{COPY THIS JSON AND PASTE IT INTO THE REQUEST BODY}}" +
                    "{{EXAMPLE JSON}} " +
                    " {" +
                    "        \"title\": \"string\"," +
                    "        \"summary\": \"string\"," +
                    "        \"body\": \"string\"," +
                    "        \"slug\": \"string\"," +
                    "        \"categories\": [" +
                    "            {\"id\": \"string\"}" +
                    "        ]," +
                    "        \"tags\": [" +
                    "            {\"id\": \"string\"}" +
                    "        ]" +
                    " }"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Created.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "[" +
                                            "  {" +
                                            "    \"id\": \"string\"," +
                                            "    \"title\": \"string\"," +
                                            "    \"summary\": \"string\"," +
                                            "    \"body\": \"string\"," +
                                            "    \"slug\": \"string\"," +
                                            "    \"createdAt\": \"2024-04-18T01:52:50.928\"," +
                                            "    \"updatedAt\": null," +
                                            "    \"authorDTO\": {" +
                                            "      \"name\": \"string\"," +
                                            "      \"lastName\": \"string\"" +
                                            "    }," +
                                            "    \"categories\": [" +
                                            "      {" +
                                            "        \"id\": \"string\"," +
                                            "        \"name\": \"string\"," +
                                            "        \"slug\": \"string\"" +
                                            "      }" +
                                            "    ]," +
                                            "    \"tags\": [" +
                                            "      {" +
                                            "        \"id\": \"string\"," +
                                            "        \"name\": \"string\"," +
                                            "        \"slug\": \"string\"" +
                                            "      }" +
                                            "    ]," +
                                            "    \"comments\": []" +
                                            "  }" +
                                            "]"))),
            @ApiResponse(responseCode = "400", description = "Bad request.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{" +
                                            "\"title\": [\"Title is required.\", \"Title must be between 5 and 30 characters.\"]," +
                                            "\"summary\": [\"Summary is required.\", \"Summary must be between 5 and 100 characters.\"]," +
                                            "\"body\": [\"Body is required.\", \"Body must be between 5 and 1000 characters.\"]," +
                                            "\"slug\": [\"Slug is required.\", \"Slug must be between 3 and 50 characters.\"]" +
                                            "}"
                            ))),

            @ApiResponse(responseCode = "401", description = "Unauthorized.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"User isn't authenticated.\"}"
                            ))),

            @ApiResponse(
                    responseCode = "404",
                    description = "Not found.",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "[{\"message\":\"Could not find tag with id:661eff024af2c96e8a7deda9\"}," +
                                            "{\"message\":\"Could not find category with id:661eff024af2c96e8a7deda9\"}]"
                            )))

    })
    @PostMapping("/create")
    public ResponseEntity<PostDTO> createPost(@RequestBody @Valid PostDTO postDTO, Authentication authentication) {
        return postService.processCreatePost(postDTO, authentication);
    }


    @Operation(
            method = "PUT",
            summary = "Update an event by ID.",
            description = "Updates an existing post based on a JSON object in the request body. The JSON must contain: " +
                    "'id' (String) of the post to update," +
                    "'title' (String) for the new title," +
                    "'summary' (String) for the new summary," +
                    "'body' (String) for the new content," +
                    "'slug' (String) for the new slug," +
                    "'categories' (array of 'id' as String) for the new categories, and" +
                    "'tags' (array of 'id' as String) for the new tags. " +
                    "Requires authentication." +
                    "{{COPY THIS JSON AND PASTE IT INTO THE REQUEST BODY}}" +
                    "{{EXAMPLE JSON}} " +
                    " {" +
                    "        \"title\": \"string\"," +
                    "        \"summary\": \"string\"," +
                    "        \"body\": \"string\"," +
                    "        \"slug\": \"string\"," +
                    "        \"categories\": [" +
                    "            {\"id\": \"string\"}" +
                    "        ]," +
                    "        \"tags\": [" +
                    "            {\"id\": \"string\"}" +
                    "        ]" +
                    " }"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204", description = "No content."),

            @ApiResponse(responseCode = "400", description = "Bad request.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{" +
                                            "\"title\": [\"Title is required.\", \"Title must be between 5 and 30 characters.\"]," +
                                            "\"summary\": [\"Summary is required.\", \"Summary must be between 5 and 100 characters.\"]," +
                                            "\"body\": [\"Body is required.\", \"Body must be between 5 and 1000 characters.\"]," +
                                            "\"slug\": [\"Slug is required.\", \"Slug must be between 3 and 50 characters.\"]" +
                                            "}"
                            ))),

            @ApiResponse(responseCode = "401", description = "Unauthorized.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"User isn't authenticated.\"}"
                            ))),

            @ApiResponse(responseCode = "403", description = "Forbidden.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"User isn't authorized.\"}"
                            ))),

            @ApiResponse(
                    responseCode = "404",
                    description = "Not found.",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "[" +
                                            "  {\"message\": \"Could not find post with id:661eff024af2c96e8a7deda9\"}," +
                                            "  {\"message\": \"Could not find category with id:66219da1dfc2226d1141683f\"}," +
                                            "  {\"message\": \"Could not find tag with id:66219da1dfc2226d1141683f\"}" +
                                            "]")))

    })
    @PutMapping("/{id}")
    public ResponseEntity<Void> updatePost(
            @PathVariable String id,
            @RequestBody @Valid PostDTO postDTO,
            @AuthenticationPrincipal User currentUser
    ) {
        return postService.processUpdatePost(id, postDTO, currentUser);
    }


    @Operation(
            method = "DELETE",
            summary = "Delete an event by ID.",
            description = "Deletes an existing post. Only with authenticated users.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204", description = "No content."),
            @ApiResponse(responseCode = "403", description = "Forbidden."),
            @ApiResponse(responseCode = "404", description = "Not found.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{" +
                                            "\"message\":\"Could not find post with id:661eff024af2c96e8a7deda9\"" +
                                            "}")))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable String id, Authentication authentication) {
        return postService.processDeletePost(id, authentication);
    }
}
