package com.webquiz.domain.service;

import com.webquiz.web.dto.request.auth.RegisterRequest;

public interface AuthService {
    void register(RegisterRequest request);
}
