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
import org.instituteatri.backendblog.dtos.UserDTO;
import org.instituteatri.backendblog.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/user")
@Tag(name = "User", description = "Endpoints for user management")
public class UserController {

    private final UserService userService;

    @Operation(
            method = "GET",
            summary = "List all users.",
            description = "Returns a list of all users registered in the system, only with the admin token.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User list successfully returned.",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = "[{" +
                                                    "\"id\":\"661eff024af2c96e8a7deda9\"" +
                                                    ",\"name\":\"Lorem\"," +
                                                    "\"lastName\":\"Ipsum\"," +
                                                    "\"phoneNumber\":\"123456789\"," +
                                                    "\"bio\":\"string\"," +
                                                    "\"email\":\"bLs2d@example.com\"," +
                                                    "\"password\":\"string\"," +
                                                    "\"role\":\"ADMIN\"," +
                                                    "\"enabled\":\"true\"," +
                                                    "\"active\":\"true\"," +
                                                    "\"lockExpirationTime\":\"null\"}]"))
                    ),
                    @ApiResponse(responseCode = "403", description = "-Unauthorized user.",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = "{\"message\":\"User isn't authorized.\"}"
                                    )))
            })
    @GetMapping("/users")
    public ResponseEntity<List<User>> findAllUsers() {
        return ResponseEntity.ok(userService.findAllUsers());
    }

    @Operation(
            method = "GET",
            summary = "Find user by ID",
            description = "Returns the user with the specified ID. Only users with the ADMIN role can search by user.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "-User successfully found.",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = "[{" +
                                                    "\"id\":\"661eff024af2c96e8a7deda9\"" +
                                                    ",\"name\":\"Lorem\"," +
                                                    "\"lastName\":\"Ipsum\"," +
                                                    "\"phoneNumber\":\"123456789\"," +
                                                    "\"bio\":\"string\"," +
                                                    "\"email\":\"bLs2d@example.com\"," +
                                                    "\"password\":\"string\"," +
                                                    "\"role\":\"ADMIN\"," +
                                                    "\"enabled\":\"true\"," +
                                                    "\"active\":\"true\"," +
                                                    "\"lockExpirationTime\":\"null\"}]"))),
                    @ApiResponse(
                            responseCode = "404",
                            description = "-User not found.",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = "{\"message\":\"Could not find user with id:661eff5e4af2c96e8a7dedc92\"}"
                                    ))),
                    @ApiResponse(
                            responseCode = "403",
                            description = "-Unauthorized user.",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = "{\"message\":\"User isn't authorized.\"}"
                                    )))
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<User> findByIdUser(@PathVariable String id) {
        User user = userService.findById(id);
        return ResponseEntity.ok().body(user);
    }


    @Operation(
            method = "PUT",
            summary = "Update user by ID",
            description = "Updates the user with the specified ID. Only users authenticated with their token can update.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "-User successfully updated.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "[{" +
                                            "\"id\":\"661eff024af2c96e8a7deda9\"" +
                                            ",\"name\":\"Lorem\"," +
                                            "\"lastName\":\"Ipsum\"," +
                                            "\"phoneNumber\":\"123456789\"," +
                                            "\"bio\":\"string\"," +
                                            "\"email\":\"bLs2d@example.com\"," +
                                            "\"password\":\"string\"," +
                                            "\"role\":\"ADMIN\"," +
                                            "\"enabled\":\"true\"," +
                                            "\"active\":\"true\"," +
                                            "\"lockExpirationTime\":\"null\"}]"
                            ))),

            @ApiResponse(responseCode = "400", description = "-Property validation ",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{" +
                                            "\"name\": [\"Name is required.\", \"Name must be between 5 and 30 characters.\"]," +
                                            "\"lastName\": [\"Last name is required.\", \"Last name must be between 5 and 30 characters.\"]," +
                                            "\"phoneNumber\": [\"Phone number is required.\", \"Phone number must be less than 11 characters.\", \"Phone number must contain only digits.\"]," +
                                            "\"email\": [\"Email is required.\", \"Invalid email format.\", \"Email must be between 10 and 30 characters.\"]," +
                                            "\"password\": [\"Password must be strong and contain at least one uppercase letter, one lowercase letter, one digit, and one special character.\", \"Password must be between 10 and 30 characters.\", \"Password is required.\"]" +
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

            @ApiResponse(responseCode = "404", description = "-User not found.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"Could not find user with id:661eff5e4af2c96e8a7dedc92\"}"
                            ))),

            @ApiResponse(responseCode = "409", description = "-E-mail not available.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"E-mail not available.\"}"
                            )))
    })
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateUser(
            @PathVariable String id,
            @RequestBody @Valid UserDTO user,
            Authentication authentication
    ) {
        return userService.processUpdateUser(id, user, authentication);
    }

    @Operation(
            method = "DELETE",
            summary = "Delete user by ID",
            description = "Deletes the user with the specified ID. Only users with the ADMIN role can delete the user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "-User successfully deleted."),
            @ApiResponse(responseCode = "403", description = "-Unauthorized user."),
            @ApiResponse(responseCode = "404", description = "-User not found.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"message\":\"Could not find user with id:661eff5e4af2c96e8a7dedc92\"}"
                            ))),
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }


    @Operation(
            method = "GET",
            summary = "Find posts by user Id",
            description = "Returns posts by user id.")
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
                    description = "-User id not found.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{" +
                                            "\"message\":\"Could not find user with id:661eff024af2c96e8a7deda9\"" +
                                            "}")))

    })
    @GetMapping("/{id}/posts")
    public ResponseEntity<List<Post>> findAllPosts(@PathVariable String id) {
        User obj = userService.findById(id);
        return ResponseEntity.ok().body(obj.getPosts());
    }
}