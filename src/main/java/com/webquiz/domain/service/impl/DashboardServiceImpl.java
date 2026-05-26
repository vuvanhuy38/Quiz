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

        long totalUsers      = userRepository.count();
        long totalExams      = examRepository.count();
        long totalCategories = categoryRepository.count();
        long totalAttempts   = examAttemptRepository.count();
        long totalQuestions  = questionBankRepository.count();

        List<ExamAttempt> attempts = examAttemptRepository.findAll();
        List<Exam>        exams    = examRepository.findAll();
        List<User>        users    = userRepository.findAll();
        List<Category>    categories = categoryRepository.findAll();

        return DashboardResponse.builder()
                .totalUsers(totalUsers)
                .totalExams(totalExams)
                .totalCategories(totalCategories)
                .totalAttempts(totalAttempts)
                .totalQuestions(totalQuestions)
                .recentAttempts(buildRecentAttempts(attempts))
                .popularExams(buildPopularExams(exams))
                .recentUsers(buildRecentUsers(users))
                .trendLabels(buildTrendLabels())
                .trendData(buildTrendData(attempts))
                .categoryLabels(buildCategoryLabels(exams, categories))
                .categoryData(buildCategoryData(exams, categories))
                .build();
    }

    // ─── Private helpers ─────────────────────────────────────────────────────

    private List<RecentAttemptDto> buildRecentAttempts(List<ExamAttempt> attempts) {

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");

        return attempts.stream()
                .filter(a -> a != null)
                .sorted(Comparator.comparing(
                        a -> a.getCreatedAt() != null ? a.getCreatedAt() : LocalDateTime.MIN,
                        Comparator.reverseOrder()))
                .limit(5)
                .map(a -> {
                    String studentName = resolveStudentName(a.getUserId());
                    String examTitle   = resolveExamTitle(a.getExamId());
                    String time        = resolveTime(a.getCreatedAt(), a.getStartedAt(), fmt);

                    return RecentAttemptDto.builder()
                            .id(a.getId())
                            .studentName(studentName)
                            .examTitle(examTitle)
                            .score(a.getScore() != null ? a.getScore() : 0.0)
                            .status(a.getStatus() != null ? a.getStatus().name() : "COMPLETED")
                            .time(time)
                            .build();
                })
                .collect(Collectors.toList());
    }

    private List<PopularExamDto> buildPopularExams(List<Exam> exams) {

        return exams.stream()
                .filter(e -> e != null)
                .sorted(Comparator.comparingLong(
                        (Exam e) -> e.getAttemptCount() != null ? e.getAttemptCount() : 0L)
                        .reversed())
                .limit(5)
                .map(e -> PopularExamDto.builder()
                        .id(e.getId())
                        .title(e.getTitle() != null ? e.getTitle() : "Không xác định")
                        .totalQuestions(e.getTotalQuestions() != null ? e.getTotalQuestions() : 0)
                        .attemptCount(e.getAttemptCount() != null ? e.getAttemptCount() : 0L)
                        .status(e.getStatus() != null ? e.getStatus().name() : "ACTIVE")
                        .build())
                .collect(Collectors.toList());
    }

    private List<RecentUserDto> buildRecentUsers(List<User> users) {

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        return users.stream()
                .filter(u -> u != null)
                .sorted(Comparator.comparing(
                        u -> u.getCreatedAt() != null ? u.getCreatedAt() : LocalDateTime.MIN,
                        Comparator.reverseOrder()))
                .limit(5)
                .map(u -> {
                    String fullName = buildFullName(u);
                    String createdAt = u.getCreatedAt() != null
                            ? u.getCreatedAt().format(fmt) : "";

                    return RecentUserDto.builder()
                            .id(u.getId())
                            .username(u.getUsername() != null ? u.getUsername() : "")
                            .fullName(fullName)
                            .email(u.getEmail() != null ? u.getEmail() : "")
                            .createdAt(createdAt)
                            .status(u.getStatus() != null ? u.getStatus().name() : "ACTIVE")
                            .build();
                })
                .collect(Collectors.toList());
    }

    // Trend chart: labels cho 7 ngày qua
    private List<String> buildTrendLabels() {

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM");
        List<String> labels = new ArrayList<>();

        for (int i = 6; i >= 0; i--) {
            labels.add(LocalDate.now().minusDays(i).format(fmt));
        }
        return labels;
    }

    // Trend chart: số lượt thi theo từng ngày trong 7 ngày qua
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

    // Doughnut chart: labels danh mục
    private List<String> buildCategoryLabels(List<Exam> exams, List<Category> categories) {
        return new ArrayList<>(buildCategoryMap(exams, categories).keySet());
    }

    // Doughnut chart: data số đề thi theo danh mục
    private List<Long> buildCategoryData(List<Exam> exams, List<Category> categories) {
        return new ArrayList<>(buildCategoryMap(exams, categories).values());
    }

    private Map<String, Long> buildCategoryMap(List<Exam> exams, List<Category> categories) {

        Map<String, String> categoryNameMap = categories.stream()
                .filter(c -> c != null && c.getId() != null)
                .collect(Collectors.toMap(Category::getId, Category::getName, (v1, v2) -> v1));

        Map<String, Long> result = new LinkedHashMap<>();
        exams.stream()
                .filter(e -> e != null)
                .forEach(e -> {
                    String catName = e.getCategoryId() != null
                            ? categoryNameMap.getOrDefault(e.getCategoryId(), "Khác")
                            : "Không danh mục";
                    result.put(catName, result.getOrDefault(catName, 0L) + 1);
                });

        return result;
    }

    // ─── Utility methods ─────────────────────────────────────────────────────

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
        if (createdAt != null)  return createdAt.format(fmt);
        if (startedAt != null)  return startedAt.format(fmt);
        return "";
    }

    private String buildFullName(User user) {
        String first = user.getFirstName() != null ? user.getFirstName() : "";
        String last  = user.getLastName()  != null ? user.getLastName()  : "";
        String full  = (first + " " + last).trim();
        return full.isEmpty() ? (user.getUsername() != null ? user.getUsername() : "Chưa đặt tên") : full;
    }
}
