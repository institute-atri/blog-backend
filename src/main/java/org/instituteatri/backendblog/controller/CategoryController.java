package org.instituteatri.backendblog.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.instituteatri.backendblog.dto.request.CategoryRequestDTO;
import org.instituteatri.backendblog.dto.request.PostRequestDTO;
import org.instituteatri.backendblog.service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/categories")
@RequiredArgsConstructor
@Tag(name = "Category", description = "Endpoints for managing Category")
public class CategoryController {

    private final CategoryService categoryService;


    @Operation(
            method = "GET",
            summary = "Find posts by category Id",
            description = "Returns posts by category id.")
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
                                            "\"message\":\"Could not find category with id:661eff024af2c96e8a7deda9\"" +
                                            "}")))

    })
    @GetMapping("/posts/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<PostRequestDTO>> getPostsByCategoryId(@PathVariable String id) {
        List<PostRequestDTO> postRequestDTOS = categoryService.findPostsByCategoryId(id);
        return ResponseEntity.ok(postRequestDTOS);
    }


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
                                    value = "[{" +
                                            "\"id\":\"string\"" +
                                            ",\"name\":\"string\"," +
                                            "\"slug\":\"string\"" +
                                            "}]"))),
    })
    @GetMapping()
    public ResponseEntity<List<CategoryRequestDTO>> findAllCategories() {
        return categoryService.processFindAllCategories();
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
                                            "\"message\":\"Could not find category with id:661eff024af2c96e8a7deda9\"" +
                                            "}")))
    })
    @GetMapping("/find/{id}")
    public ResponseEntity<CategoryRequestDTO> findCategoryById(@PathVariable String id) {
        return ResponseEntity.ok(categoryService.findById(id));
    }


    @Operation(
            method = "POST",
            summary = "Create an event.",
            description = "Creates a new category based on a JSON object in the request body. " +
                    "The JSON must contain: 'name' (String) and 'slug' (String). " +
                    "Only the ADMIN role can create a new category." +
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

            @ApiResponse(responseCode = "400", description = "Bad request.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{" +
                                            "\"name\": [\"Name is required.\", \"Name cannot be longer than 50 characters.\"]," +
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
    public ResponseEntity<CategoryRequestDTO> createCategory(@RequestBody @Valid CategoryRequestDTO categoryRequestDTO) {
        return categoryService.processCreateCategory(categoryRequestDTO);
    }


    @Operation(
            method = "PUT",
            summary = "Update an event by ID.",
            description = "Updates an existing category based on a JSON object in the request body. " +
                    "The JSON must contain: 'name' (String) and 'slug' (String). " +
                    "Only the ADMIN role can update an existing category." +
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
                    description = "No content."),

            @ApiResponse(responseCode = "400", description = "Bad request.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{" +
                                            "\"name\": [\"Name is required.\", \"Name cannot be longer than 50 characters.\"]," +
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
                                    value = "{\"message\":\"Could not find category with id:661eff024af2c96e8a7deda9\"}"
                            )))
    })
    @PutMapping("update/{id}")
    public ResponseEntity<Void> updateCategory(
            @PathVariable String id,
            @RequestBody @Valid CategoryRequestDTO categoryRequestDTO) {
        return categoryService.processUpdateCategory(id, categoryRequestDTO);
    }


    @Operation(
            method = "DELETE",
            summary = "Delete an event by ID.",
            description = "Deletes an existing category. Only the ADMIN role can delete an existing category.")
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
                                            "\"message\":\"Could not find category with id:661eff024af2c96e8a7deda9\"" +
                                            "}")))
    })
    @DeleteMapping("delete/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable String id) {
        return categoryService.processDeleteCategory(id);
    }
}
