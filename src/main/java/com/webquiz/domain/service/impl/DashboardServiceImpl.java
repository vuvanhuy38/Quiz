package com.webquiz.domain.service.impl;

import com.webquiz.domain.entity.*;
import com.webquiz.domain.repository.*;
import com.webquiz.domain.service.DashboardService;
import com.webquiz.web.dto.response.dashboard.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final UserRepository userRepository;
    private final ExamRepository examRepository;
    private final CategoryRepository categoryRepository;
    private final ExamAttemptRepository examAttemptRepository;
    private final QuestionBankRepository questionBankRepository;

    @Override
    public DashboardResponse getStats() {

        List<ExamAttempt> attempts = examAttemptRepository.findAll();

        return DashboardResponse.builder()
                                .totalUsers(userRepository.count())
                                .totalExams(examRepository.count())
                                .totalCategories(categoryRepository.count())
                                .totalAttempts((long) attempts.size())
                                .totalQuestions(questionBankRepository.count())
                                .recentAttempts(buildRecentAttempts(attempts))
                                .trendLabels(buildTrendLabels())
                                .trendData(buildTrendData(attempts))
                                .build();
    }

    private List<RecentAttemptDto> buildRecentAttempts(List<ExamAttempt> attempts) {

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");

        return attempts.stream()
                       .filter(a -> a != null)
                       .sorted(Comparator.comparing(
                               a -> a.getCreatedAt() != null ? a.getCreatedAt() : LocalDateTime.MIN,
                               Comparator.reverseOrder()))
                       .limit(5)
                       .map(a -> RecentAttemptDto.builder()
                                                 .id(a.getId())
                                                 .studentName(resolveStudentName(a.getUserId()))
                                                 .examTitle(resolveExamTitle(a.getExamId()))
                                                 .score(a.getScore() != null ? a.getScore() : 0.0)
                                                 .status(a.getStatus() != null ? a.getStatus().name() : "COMPLETED")
                                                 .time(resolveTime(a.getCreatedAt(), a.getStartedAt(), fmt))
                                                 .build())
                       .collect(Collectors.toList());
    }

    private List<String> buildTrendLabels() {

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM");
        List<String> labels = new ArrayList<>();

        for (int i = 6; i >= 0; i--) {
            labels.add(LocalDate.now().minusDays(i).format(fmt));
        }
        return labels;
    }

    private List<Long> buildTrendData(List<ExamAttempt> attempts) {

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM");
        Map<String, Long> map = new LinkedHashMap<>();

        for (int i = 6; i >= 0; i--) {
            map.put(LocalDate.now().minusDays(i).format(fmt), 0L);
        }

        LocalDate sevenDaysAgo = LocalDate.now().minusDays(6);

        attempts.stream()
                .filter(a -> a != null && a.getCreatedAt() != null)
                .forEach(a -> {
                    LocalDate date = a.getCreatedAt().toLocalDate();
                    if (!date.isBefore(sevenDaysAgo) && !date.isAfter(LocalDate.now())) {
                        String key = date.format(fmt);
                        map.put(key, map.getOrDefault(key, 0L) + 1);
                    }
                });

        return new ArrayList<>(map.values());
    }

    // ─── Utility ─────────────────────────────────────────────────────────────

    private String resolveStudentName(String userId) {
        if (userId == null) return "Học viên ẩn danh";
        return userRepository.findById(userId)
                             .map(this::buildFullName)
                             .orElse("Học viên ẩn danh");
    }

    private String resolveExamTitle(String examId) {
        if (examId == null) return "Không xác định";
        return examRepository.findById(examId)
                             .map(e -> e.getTitle() != null ? e.getTitle() : "Không xác định")
                             .orElse("Không xác định");
    }

    private String resolveTime(LocalDateTime createdAt, LocalDateTime startedAt, DateTimeFormatter fmt) {
        if (createdAt != null) return createdAt.format(fmt);
        if (startedAt != null) return startedAt.format(fmt);
        return "";
    }

    private String buildFullName(User user) {
        String first = user.getFirstName() != null ? user.getFirstName() : "";
        String last  = user.getLastName()  != null ? user.getLastName()  : "";
        String full  = (first + " " + last).trim();
        return full.isEmpty() ? (user.getUsername() != null ? user.getUsername() : "Chưa đặt tên") : full;
    }
}
