package com.demo.folder.controller.skeleton;

import com.demo.folder.entity.dto.request.ChangeLoginRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Login Controller", description = "Operations related to user login and credentials management")
@RequestMapping(value = "api", consumes = {"application/json"}, produces = {
    "application/json"})
public interface LoginControllerInterface {

  @PostMapping("/login")
  @Operation(summary = "User Login", description = "Authenticates a user with username and password.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully logged in"),
      @ApiResponse(responseCode = "400", description = "Invalid input data"),
      @ApiResponse(responseCode = "401", description = "Invalid credentials"),
      @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  ResponseEntity<?> login(
      @Valid @RequestParam(name = "username") String username,
      @RequestParam(name = "password") String password,
      BindingResult result,
      HttpSession session
  );

  @PutMapping("/login/change-login")
  @Operation(summary = "Change Login Credentials", description = "Allows a user to change their login credentials.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully changed login credentials"),
      @ApiResponse(responseCode = "400", description = "Invalid input data"),
      @ApiResponse(responseCode = "401", description = "Invalid credentials"),
      @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  ResponseEntity<?> changeLogin(
      @Valid @RequestBody ChangeLoginRequestDTO changeLoginDTO,
      BindingResult result
  );


  @PostMapping("/logout")
  @Operation(summary = "User Log Out", description = "Allows a user to log out with username and password.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully logged out"),
      @ApiResponse(responseCode = "401", description = "Invalid credentials"),
      @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  ResponseEntity<?> logout(
      @Valid @RequestParam(name = "username") String username,
      @RequestParam(name = "password") String password,
      HttpSession session
  );
}
