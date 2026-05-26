package com.webquiz.domain.service.impl;

import com.webquiz.contact.enums.RoleType;
import com.webquiz.contact.enums.StatusUserType;
import com.webquiz.domain.entity.User;
import com.webquiz.domain.repository.UserRepository;
import com.webquiz.domain.service.AuthService;
import com.webquiz.web.dto.request.auth.RegisterRequest;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void register(RegisterRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Tên đăng nhập đã tồn tại");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email đã tồn tại");
        }

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Mật khẩu nhập lại không khớp");
        }

        User user = User.builder()
                        .username(request.getUsername())
                        .email(request.getEmail())
                        .password(passwordEncoder.encode(request.getPassword()))
                        .firstName(request.getFirstName())
                        .lastName(request.getLastName())
                        .role(RoleType.USER)
                        .status(StatusUserType.ACTIVE)
                        .build();

        userRepository.save(user);
    }
}
