package com.quarkus.controller;

import com.quarkus.entity.User;
import com.quarkus.exception.UserNotFoundException;
import com.quarkus.exceptionhandler.ExceptionHandler;
import com.quarkus.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;
import org.jboss.resteasy.annotations.jaxrs.PathParam;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

@RequestScoped
@Path("/v1/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserController {

    private final UserService userService;

    @Inject
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GET
    public List<User> getUsers() {
        return userService.getAllUsers();
    }

    @GET
    @Path("/dataGempa")
    public Map<String, Object> getDataGempa() throws IOException, ParseException, InterruptedException {
        return userService.getDataGempa();
    }

    @GET
    @Path("/{id}")
    public User getUser(@PathParam("id") int id) throws UserNotFoundException {
        return userService.getUserById(id);
    }

    @POST
    public User createUser(@Valid UserDto userDto) {
        return userService.saveUser(userDto.toUser());
    }

    @PUT
    @Path("/{id}")
    public User updateUser(@PathParam("id") int id, @Valid UserDto userDto) throws UserNotFoundException {
        return userService.updateUser(id, userDto.toUser());
    }

    @DELETE
    @Path("/{id}")
    public Response deleteUser(@PathParam("id") int id) throws UserNotFoundException {
        userService.deleteUser(id);
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @Schema(name="UserDTO", description="User representation to create")
    public static class UserDto {

        @NotBlank
        @Schema(title = "Username", required = true)
        private String username;

        @NotBlank
        @Schema(title = "Password", required = true)
        private String password;

        @Schema(title = "User role, either ADMIN or USER. USER is default")
        private String role;

        @NotBlank
        @Schema(title="User given name", required = true)
        private String firstName;

        @NotBlank
        @Schema(title="User surname", required = true)
        private String lastName;

        @Min(value = 1, message = "The value must be more than 0")
        @Max(value = 200, message = "The value must be less than 200")
        @Schema(title="User age between 1 to 200", required = true)
        private int age;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public User toUser() {
            User user = new User();
            user.setUsername(username);
            user.setPassword(password);
            user.setRole(StringUtils.isBlank(role) ? "USER" : StringUtils.upperCase(role));
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setAge(age);
            return user;
        }
    }
}
