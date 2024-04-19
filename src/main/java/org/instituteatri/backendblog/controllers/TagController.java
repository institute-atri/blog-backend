package org.instituteatri.backendblog.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.instituteatri.backendblog.domain.entities.Post;
import org.instituteatri.backendblog.domain.entities.Tag;
import org.instituteatri.backendblog.service.TagService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/tag")
@RequiredArgsConstructor
@io.swagger.v3.oas.annotations.tags.Tag(name = "Tag", description = "Endpoints for tags")
public class TagController {

    private final TagService tagService;


    @Operation(
            method = "GET",
            summary = "Find posts by tag Id",
            description = "Returns posts by tag id.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Posts list successfully returned.",
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
            @ApiResponse(
                    responseCode = "404",
                    description = "-Tag id not found.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{" +
                                            "\"message\":\"Could not find tag with id:661eff024af2c96e8a7deda9\"" +
                                            "}")))

    })
    @GetMapping("/{id}/posts")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<Post>> getPostsByCategoryId(@PathVariable String id) {
        List<Post> posts = tagService.findPostsByTagId(id);
        return ResponseEntity.ok(posts);
    }


    @Operation(
            method = "GET",
            summary = "List all tags.",
            description = "Returns a list of tags.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Tags list successfully returned.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "[{" +
                                            "\"id\":\"661eff024af2c96e8a7deda9\"" +
                                            ",\"name\":\"string\"," +
                                            "\"slug\":\"string\"" +
                                            "}]"))),
    })
    @GetMapping("/tags")
    public ResponseEntity<List<Tag>> findAllTags() {
        return ResponseEntity.ok(tagService.findAllTags());
    }


    @Operation(
            method = "GET",
            summary = "Find tag by ID",
            description = "Returns the tag with the specified ID.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "-Tag successfully found.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "[{" +
                                            "\"id\":\"661eff024af2c96e8a7deda9\"" +
                                            ",\"name\":\"string\"," +
                                            "\"slug\":\"string\"" +
                                            "}]"))),
            @ApiResponse(
                    responseCode = "404",
                    description = "-Tag not found.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{" +
                                            "\"message\":\"Could not find tag with id:661eff024af2c96e8a7deda9\"" +
                                            "}")))
    })
    @GetMapping("/{id}")
    public ResponseEntity<Tag> findByIdTag(@PathVariable String id) {
        return ResponseEntity.ok(tagService.findById(id));
    }


    @Operation(
            method = "POST",
            summary = "Create a new tag.",
            description = "Create a new tag. Only the ADMIN role can create a new tag.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "-Tag successfully created.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "[{" +
                                            "\"id\":\"661eff024af2c96e8a7deda9\"" +
                                            ",\"name\":\"string\"," +
                                            "\"slug\":\"string\"" +
                                            "}]"))),
            @ApiResponse(responseCode = "403", description = "-Unauthorized user.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"User isn't authorized.\"}"
                            )))
    })
    @PostMapping("/create")
    public ResponseEntity<Tag> createTag(@RequestBody Tag tag) {
        return tagService.processCreateTag(tag);
    }


    @Operation(
            method = "PUT",
            summary = "Update an existing tag.",
            description = "Updates an existing tag. Only the ADMIN role can update an existing tag.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "-Tag successfully updated.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "[{" +
                                            "\"id\":\"661eff024af2c96e8a7deda9\"" +
                                            ",\"name\":\"string\"," +
                                            "\"slug\":\"string\"" +
                                            "}]"))),

            @ApiResponse(responseCode = "403", description = "-Unauthorized user.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"User isn't authorized.\"}"
                            ))),

            @ApiResponse(responseCode = "404", description = "-Tag not found.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"Could not find tag with id:661eff024af2c96e8a7deda9\"}"
                            )))
    })
    @PutMapping("/{id}")
    public ResponseEntity<Tag> updateTag(
            @PathVariable String id,
            @RequestBody Tag tag) {
        return tagService.processUpdateTag(id, tag);
    }


    @Operation(
            method = "DELETE",
            summary = "Delete an existing tag.",
            description = "Deletes an existing tag. Only the ADMIN role can delete an existing tag.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204", description = "-Tag successfully deleted."),
            @ApiResponse(responseCode = "403", description = "-Unauthorized user."),
            @ApiResponse(responseCode = "404", description = "-Tag not found.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{" +
                                            "\"message\":\"Could not find tag with id:661eff024af2c96e8a7deda9\"" +
                                            "}")))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTag(@PathVariable String id) {
        tagService.deleteTag(id);
        return ResponseEntity.noContent().build();
    }
}
