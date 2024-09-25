package com.demo.folder.controller;

import com.demo.folder.entity.base.User;
import com.demo.folder.entity.dto.request.ChangeLoginRequestDTO;
import com.demo.folder.entity.dto.request.LoginRequestDTO;
import com.demo.folder.error.exception.AuthenticationException;
import com.demo.folder.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "api/login", consumes = {"application/JSON"}, produces = {
    "application/JSON", "application/XML"})
@Tag(name = "Login Controller", description = "Operations related to user login and credentials management")
public class LoginController {

  @Autowired
  private UserService userService;

  @GetMapping
  @Operation(summary = "User Login", description = "Authenticates a user with username and password.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully logged in"),
      @ApiResponse(responseCode = "400", description = "Invalid input data"),
      @ApiResponse(responseCode = "401", description = "Invalid credentials"),
      @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO loginRequestDTO,
      BindingResult result, HttpSession session) {
    if (result.hasErrors()) {
      return ResponseEntity.badRequest()
          .body(Objects.requireNonNull(result.getFieldError()).getDefaultMessage());
    }
    User user = userService.authenticate(loginRequestDTO.getUsername(),
        loginRequestDTO.getPassword());
    if (user != null) {
      session.setAttribute("authenticatedUser", user);
      return ResponseEntity.ok("Login successful");
    } else {
      throw new AuthenticationException("Invalid credentials");
    }
  }

  @PutMapping("/change-login")
  @Operation(summary = "Change Login Credentials", description = "Allows a user to change their login credentials.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully changed login credentials"),
      @ApiResponse(responseCode = "400", description = "Invalid input data"),
      @ApiResponse(responseCode = "401", description = "Invalid credentials"),
      @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  public ResponseEntity<?> changeLogin(@Valid @RequestBody ChangeLoginRequestDTO changeLoginDTO,
      BindingResult result, HttpSession session) {
    if (result.hasErrors()) {
      return ResponseEntity.badRequest()
          .body(Objects.requireNonNull(result.getFieldError()).getDefaultMessage());
    }
    User user = userService.authenticate(changeLoginDTO.getUsername(),
        changeLoginDTO.getPassword());
    if (user != null) {
      userService.updatePassword(user, changeLoginDTO.getNewPassword());
      return ResponseEntity.ok("User Password got successfully updated.");
    }
    throw new AuthenticationException("Invalid credentials");
  }
}
