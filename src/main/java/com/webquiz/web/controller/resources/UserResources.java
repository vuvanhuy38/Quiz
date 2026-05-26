package com.webquiz.web.controller.resources;

import com.webquiz.contact.SessionUtil;
import com.webquiz.domain.service.UserService;
import com.webquiz.web.dto.common.Response;
import com.webquiz.web.dto.common.ResponsePage;
import com.webquiz.web.dto.response.user.UserItemResponse;
import com.webquiz.web.dto.response.user.UserProfileResponse;
import com.webquiz.web.dto.request.user.UpdateProfileRequest;
import com.webquiz.web.dto.request.user.ChangePasswordRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
public class UserResources {

    private final UserService userService;

    @PutMapping("/block/{userId}")
    public ResponseEntity<Response<Void>> blockUser(@PathVariable String userId) {
        userService.blockUser(userId);

        return ResponseEntity.status(HttpStatus.OK).body(
                Response.<Void>builder()
                        .message("Block user thành công")
                        .build()
        );
    }

    @PutMapping("/active/{userId}")
    public ResponseEntity<Response<Void>> activeUser(@PathVariable String userId) {

        userService.activeUser(userId);

        return ResponseEntity.status(HttpStatus.OK).body(
                Response.<Void>builder()
                        .message("Mở khóa user thành công")
                        .build()
        );
    }

    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<Response<Void>> delete(@PathVariable String userId) {
        userService.delete(userId);
        return ResponseEntity.status(HttpStatus.OK).body(
                Response.<Void>builder()
                        .message("Xóa user thành công")
                        .build()
        );
    }

    @GetMapping("/getAll")
    public ResponseEntity<ResponsePage<List<UserItemResponse>>> getAllUsers(
            @RequestParam(required = false, defaultValue = "") String name,
            @RequestParam(required = false, defaultValue = "") String phone,
            @RequestParam(required = false, defaultValue = "") String email,
            Pageable pageable
    ) {

        return ResponseEntity.status(HttpStatus.OK).body(
                userService.getAllUsers(pageable, name, phone, email)
        );
    }

    @GetMapping("/profile")
    public ResponseEntity<Response<UserProfileResponse>> getProfile() {
        String userId = SessionUtil.getCurrentUser().getId();
        UserProfileResponse profile = userService.getUserProfile(userId);
        return ResponseEntity.status(HttpStatus.OK).body(
                Response.<UserProfileResponse>builder()
                        .data(profile)
                        .message("Lấy thông tin tài khoản thành công")
                        .build()
        );
    }

    @PutMapping("/profile")
    public ResponseEntity<Response<Void>> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        String userId = SessionUtil.getCurrentUser().getId();
        userService.updateProfile(userId, request);
        return ResponseEntity.status(HttpStatus.OK).body(
                Response.<Void>builder()
                        .message("Cập nhật thông tin tài khoản thành công")
                        .build()
        );
    }

    @PutMapping("/profile/change-password")
    public ResponseEntity<Response<Void>> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        String userId = SessionUtil.getCurrentUser().getId();
        userService.changePassword(userId, request);
        return ResponseEntity.status(HttpStatus.OK).body(
                Response.<Void>builder()
                        .message("Đổi mật khẩu thành công")
                        .build()
        );
    }
}
