package com.webquiz.domain.service.impl;

import com.webquiz.contact.enums.StatusUserType;
import com.webquiz.domain.entity.User;
import com.webquiz.domain.repository.UserRepository;
import com.webquiz.domain.service.UserService;
import com.webquiz.web.dto.common.ResponsePage;
import com.webquiz.web.dto.response.user.UserItemResponse;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.List;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

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
}
