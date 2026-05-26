package com.webquiz.domain.service.impl;

import com.webquiz.contact.enums.StatusUserType;
import com.webquiz.domain.entity.User;
import com.webquiz.domain.repository.UserRepository;
import com.webquiz.domain.service.UserService;
import com.webquiz.web.dto.common.ResponsePage;
import com.webquiz.web.dto.response.user.UserItemResponse;
import lombok.AllArgsConstructor;
import com.webquiz.web.dto.response.user.UserProfileResponse;
import com.webquiz.web.dto.request.user.UpdateProfileRequest;
import com.webquiz.web.dto.request.user.ChangePasswordRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void blockUser(String id) {

        User user = userRepository.findById(id)
                                  .orElseThrow(() -> new RuntimeException("User không tồn tại"));

        user.setStatus(StatusUserType.BLOCKED);

        userRepository.save(user);
    }

    @Override
    public void activeUser(String id) {

        User user = userRepository.findById(id)
                                  .orElseThrow(() -> new RuntimeException("User không tồn tại"));

        user.setStatus(StatusUserType.ACTIVE);

        userRepository.save(user);
    }

    @Override
    public void delete(String id) {

        User user = userRepository.findById(id)
                                  .orElseThrow(() -> new RuntimeException("User không tồn tại"));

        userRepository.delete(user);
    }

    @Override
    public ResponsePage<List<UserItemResponse>> getAllUsers(Pageable pageable, String name, String phone, String email) {

        String normalizedName = Normalizer.normalize(name, Normalizer.Form.NFC);
        String normalizedPhone = Normalizer.normalize(phone, Normalizer.Form.NFC);
        String normalizedEmail = Normalizer.normalize(email, Normalizer.Form.NFC);

        Page<User> userPage = userRepository.findAllWithFilters(normalizedName, normalizedPhone, normalizedEmail, pageable
        );

        List<UserItemResponse> items = userPage.getContent().stream()
                                               .map(u -> UserItemResponse.builder()
                                                     .id(u.getId())
                                                     .username(u.getUsername())
                                                     .email(u.getEmail())
                                                     .phone(u.getPhone())
                                                     .fullName(u.getFirstName() + " " + u.getLastName())
                                                     .role(u.getRole())
                                                     .status(u.getStatus())
                                                     .build()
                                               )
                                               .toList();

        return ResponsePage.<List<UserItemResponse>>builder()
                           .data(items)
                           .totalElement(userPage.getTotalElements())
                           .totalPage(userPage.getTotalPages())
                           .pageSize(userPage.getSize())
                           .pageIndex(userPage.getNumber())
                           .build();
    }

    @Override
    public UserProfileResponse getUserProfile(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));

        return UserProfileResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole())
                .build();
    }

    @Override
    public void updateProfile(String userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));

        // Kiểm tra email trùng lặp nếu email thay đổi
        if (request.getEmail() != null && !request.getEmail().equalsIgnoreCase(user.getEmail())) {
            Optional<User> existingEmailUser = userRepository.findByEmail(request.getEmail());
            if (existingEmailUser.isPresent() && !existingEmailUser.get().getId().equals(userId)) {
                throw new RuntimeException("Email đã được sử dụng bởi tài khoản khác");
            }
            user.setEmail(request.getEmail());
        }

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhone(request.getPhone());

        userRepository.save(user);
    }

    @Override
    public void changePassword(String userId, ChangePasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));

        // Kiểm tra mật khẩu cũ
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("Mật khẩu cũ không chính xác");
        }

        // Kiểm tra mật khẩu mới và xác nhận mật khẩu mới
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Xác nhận mật khẩu mới không khớp");
        }

        // Cập nhật mật khẩu mới đã được mã hóa
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        userRepository.save(user);
    }
}
