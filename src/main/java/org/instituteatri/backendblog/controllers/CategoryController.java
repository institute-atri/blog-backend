package org.instituteatri.backendblog.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.instituteatri.backendblog.domain.entities.Category;
import org.instituteatri.backendblog.domain.entities.Post;
import org.instituteatri.backendblog.service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/category")
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
                    description = "-Category id not found.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{" +
                                            "\"message\":\"Could not find category with id:661eff024af2c96e8a7deda9\"" +
                                            "}")))

    })
    @GetMapping("/{id}/posts")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<Post>> getPostsByCategoryId(@PathVariable String id) {
        List<Post> posts = categoryService.findPostsByCategoryId(id);
        return ResponseEntity.ok(posts);
    }


    @Operation(
            method = "GET",
            summary = "List all categories.",
            description = "Returns a list of categories.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Categories list successfully returned.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "[{" +
                                            "\"id\":\"661eff024af2c96e8a7deda9\"" +
                                            ",\"name\":\"string\"," +
                                            "\"slug\":\"string\"" +
                                            "}]"))),
    })
    @GetMapping("/categories")
    public ResponseEntity<List<Category>> findAllCategories() {
        return ResponseEntity.ok(categoryService.findAllCategories());
    }


    @Operation(
            method = "GET",
            summary = "Find category by ID",
            description = "Returns the category with the specified ID.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "-Category successfully found.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "[{" +
                                            "\"id\":\"661eff024af2c96e8a7deda9\"" +
                                            ",\"name\":\"string\"," +
                                            "\"slug\":\"string\"" +
                                            "}]"))),
            @ApiResponse(
                    responseCode = "404",
                    description = "-Category not found.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{" +
                                            "\"message\":\"Could not find category with id:661eff024af2c96e8a7deda9\"" +
                                            "}")))
    })
    @GetMapping("/{id}")
    public ResponseEntity<Category> findCategoryById(@PathVariable String id) {
        return ResponseEntity.ok(categoryService.findById(id));
    }


    @Operation(
            method = "POST",
            summary = "Create a new category.",
            description = "Create a new category. Only the ADMIN role can create a new category.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "-Category successfully created.",
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
    public ResponseEntity<Category> createCategory(@RequestBody Category category) {
        return categoryService.processCreateCategory(category);
    }


    @Operation(
            method = "PUT",
            summary = "Update an existing category.",
            description = "Updates an existing category. Only the ADMIN role can update an existing category.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "-Category successfully updated.",
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

            @ApiResponse(responseCode = "404", description = "-Category not found.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"Could not find category with id:661eff024af2c96e8a7deda9\"}"
                            )))
    })
    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(
            @PathVariable String id,
            @RequestBody Category category) {
        return categoryService.processUpdateCategory(id, category);
    }


    @Operation(
            method = "DELETE",
            summary = "Delete an existing category.",
            description = "Deletes an existing category. Only the ADMIN role can delete an existing category.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204", description = "-Tag successfully deleted."),
            @ApiResponse(responseCode = "403", description = "-Unauthorized user."),
            @ApiResponse(responseCode = "404", description = "-Category not found.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{" +
                                            "\"message\":\"Could not find category with id:661eff024af2c96e8a7deda9\"" +
                                            "}")))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable String id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
