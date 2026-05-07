package com.webquiz.web.controller.resources;

import com.webquiz.domain.service.AuthService;
import com.webquiz.web.dto.common.Response;
import com.webquiz.web.dto.request.auth.RegisterRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthResources {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<Response<Void>> register(
            @Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(Response.<Void>builder()
                                             .message("Đăng ký tài khoản thành công")
                                             .build()
                             );
    }
}
