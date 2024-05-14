package org.instituteatri.backendblog.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.instituteatri.backendblog.dto.request.TagRequestDTO;
import org.instituteatri.backendblog.dto.request.TagUpdateRequestDTO;
import org.instituteatri.backendblog.dto.response.PostResponseDTO;
import org.instituteatri.backendblog.dto.response.TagResponseDTO;
import org.instituteatri.backendblog.service.TagService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/tags")
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
                                            "    \"authorDTO\": {" +
                                            "      \"name\": \"string\"," +
                                            "      \"lastName\": \"string\"" +
                                            "    }," +
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
                                            "\"message\":\"Could not find tag with id:661eff024af2c96e8a7deda9\"" +
                                            "}")))

    })
    @GetMapping("/posts/{id}")
    public ResponseEntity<List<PostResponseDTO>> getPostsByTagId(@PathVariable String id) {
        return tagService.findPostsByTagId(id);
    }

    @Operation(
            method = "GET",
            summary = "Get all events.",
            description = "Collection of events.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Success.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "[{" +
                                            "\"id\":\"string\"" +
                                            ",\"name\":\"string\"," +
                                            "\"slug\":\"string\"" +
                                            "}]"))),
    })
    @GetMapping()
    public ResponseEntity<List<TagResponseDTO>> findAllTags() {
        return tagService.processFindAllTags();
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
                                    value = "[{" +
                                            "\"id\":\"string\"" +
                                            ",\"name\":\"string\"," +
                                            "\"slug\":\"string\"" +
                                            "}]"))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not found.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{" +
                                            "\"message\":\"Could not find tag with id:661eff024af2c96e8a7deda9\"" +
                                            "}")))
    })
    @GetMapping("/find/{id}")
    public ResponseEntity<TagResponseDTO> findByIdTag(@PathVariable String id) {
        return tagService.findById(id);
    }

    @Operation(
            method = "POST",
            summary = "Create an event.",
            description = "Creates a new tag based on a JSON object in the request body. " +
                    "The JSON must contain: 'name' (String) and 'slug' (String). " +
                    "Only the ADMIN role can create a new tag." +
                    "{{COPY THIS JSON AND PASTE IT INTO THE REQUEST BODY}}" +
                    "{{EXAMPLE JSON}} " +
                    " {" +
                    "        \"name\": \"string\"," +
                    "        \"slug\": \"string\"" +
                    " }"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Created.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "[{" +
                                            "\"id\":\"string\"" +
                                            ",\"name\":\"string\"," +
                                            "\"slug\":\"string\"" +
                                            "}]"))),

            @ApiResponse(responseCode = "400", description = "Bad Request.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{" +
                                            "\"name\": [\"Name is required.\", \"Name cannot be longer than 10 characters.\"]," +
                                            "\"slug\": [\"Slug is required.\", \"Slug cannot be longer than 50 characters.\"]" +
                                            "}"
                            ))),

            @ApiResponse(responseCode = "403", description = "Forbidden.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"User isn't authorized.\"}"
                            )))
    })
    @PostMapping("/create")
    public ResponseEntity<TagRequestDTO> createTag(@RequestBody @Valid TagRequestDTO tagRequestDTO) {
        return tagService.processCreateTag(tagRequestDTO);
    }

    @Operation(
            method = "PUT",
            summary = "Update an event.",
            description = "Updates an existing tag based on a JSON object in the request body. " +
                    "The JSON must contain: 'name' (String) and 'slug' (String). " +
                    "Only the ADMIN role can update an existing tag." +
                    "{{COPY THIS JSON AND PASTE IT INTO THE REQUEST BODY}}" +
                    "{{EXAMPLE JSON}} " +
                    " {" +
                    "        \"name\": \"string\"," +
                    "        \"slug\": \"string\"" +
                    " }"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "No Content."),

            @ApiResponse(responseCode = "400", description = "Bad request.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{" +
                                            "\"name\": [\"Name is required.\", \"Name cannot be longer than 10 characters.\"]," +
                                            "\"slug\": [\"Slug is required.\", \"Slug cannot be longer than 50 characters.\"]" +
                                            "}"
                            ))),

            @ApiResponse(responseCode = "403", description = "Forbidden.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"User isn't authorized.\"}"
                            ))),

            @ApiResponse(responseCode = "404", description = "Not found.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"Could not find tag with id:661eff024af2c96e8a7deda9\"}"
                            )))
    })
    @PutMapping("/update/{id}")
    public ResponseEntity<Void> updateTag(
            @PathVariable String id,
            @RequestBody @Valid TagUpdateRequestDTO tagRequestDTO) {
        return tagService.processUpdateTag(id, tagRequestDTO);
    }

    @Operation(
            method = "DELETE",
            summary = "Delete an event by ID.",
            description = "Deletes an existing tag. Only the ADMIN role can delete an existing tag.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204", description = "No content."),

            @ApiResponse(responseCode = "403", description = "Forbidden.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"User isn't authorized.\"}"
                            ))),

            @ApiResponse(responseCode = "404", description = "Not found.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{" +
                                            "\"message\":\"Could not find tag with id:661eff024af2c96e8a7deda9\"" +
                                            "}")))
    })
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteTag(@PathVariable String id) {
        return tagService.processDeleteTag(id);
    }
}
