package com.academiaNetAPI.demo.controller;

import com.academiaNetAPI.demo.dto.CreateUserRequest;
import com.academiaNetAPI.demo.dto.UpdateUserRequest;
import com.academiaNetAPI.demo.dto.UserResponse;
import com.academiaNetAPI.demo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Usuarios", description = "CRUD de usuarios (panel admin)")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @Operation(summary = "Listar usuarios")
    public List<UserResponse> list(@RequestParam(required = false) UUID companyId) {
        return userService.list(companyId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear usuario",
            description = "role: estudiante | profesor | admin. password es opcional (default 123456).")
    public UserResponse create(@RequestParam(required = false) UUID companyId,
                               @Valid @RequestBody CreateUserRequest request) {
        return userService.create(companyId, request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Editar usuario")
    public UserResponse update(@PathVariable UUID id, @Valid @RequestBody UpdateUserRequest request) {
        return userService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Eliminar usuario (soft-delete)")
    public void delete(@PathVariable UUID id) {
        userService.delete(id);
    }
}
