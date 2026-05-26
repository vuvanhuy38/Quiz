package com.webquiz.web.controller.resources;

import com.webquiz.domain.service.DashboardService;
import com.webquiz.web.dto.common.Response;
import com.webquiz.web.dto.response.dashboard.DashboardResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/dashboard")
@AllArgsConstructor
public class DashboardResources {

    private final DashboardService dashboardService;

    @GetMapping("/stats")
    public ResponseEntity<Response<DashboardResponse>> getStats() {
        return ResponseEntity.status(HttpStatus.OK).body(
                Response.<DashboardResponse>builder()
                        .message("Lấy dữ liệu dashboard thành công")
                        .data(dashboardService.getStats())
                        .build()
        );
    }
}
