package com.webquiz.domain.service;

import com.webquiz.web.dto.common.ResponsePage;
import com.webquiz.web.dto.response.user.UserItemResponse;
import com.webquiz.web.dto.response.user.UserProfileResponse;
import com.webquiz.web.dto.request.user.UpdateProfileRequest;
import com.webquiz.web.dto.request.user.ChangePasswordRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {

    void blockUser(String id);

    void activeUser(String id);

    void delete(String id);

    ResponsePage<List<UserItemResponse>> getAllUsers(Pageable pageable,
                                                     String name,
                                                     String phone,
                                                     String email);

    UserProfileResponse getUserProfile(String userId);

    void updateProfile(String userId, UpdateProfileRequest request);

    void changePassword(String userId, ChangePasswordRequest request);
}
