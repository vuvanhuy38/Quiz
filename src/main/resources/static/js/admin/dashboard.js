document.addEventListener("DOMContentLoaded", fetchDashboardStats);

async function fetchDashboardStats() {
    try {
        const res = await fetch("/api/admin/dashboard/stats");
        if (!res.ok) throw new Error("Lỗi tải dữ liệu dashboard");

        const json = await res.json();
        const data = json.data;

        // ── Stat cards
        document.getElementById("statUsers").innerText       = fmt(data.totalUsers);
        document.getElementById("statExams").innerText       = fmt(data.totalExams);
        document.getElementById("statCategories").innerText  = fmt(data.totalCategories);
        document.getElementById("statAttempts").innerText    = fmt(data.totalAttempts);
        document.getElementById("statQuestions").innerText   = fmt(data.totalQuestions);

        // ── Charts
        renderTrendChart(data.trendLabels, data.trendData);

        // ── Table
        renderRecentAttempts(data.recentAttempts);

    } catch (err) {
        console.error(err);
        showError();
    }
}

// ─── Trend Line Chart ────────────────────────────────────────────────────────
function renderTrendChart(labels, values) {
    const ctx = document.getElementById("trendChart").getContext("2d");

    const gradient = ctx.createLinearGradient(0, 0, 0, 260);
    gradient.addColorStop(0, "rgba(79, 70, 229, 0.35)");
    gradient.addColorStop(1, "rgba(79, 70, 229, 0.0)");

    new Chart(ctx, {
        type: "line",
        data: {
            labels,
            datasets: [{
                label: "Lượt thi",
                data: values,
                borderColor: "#4f46e5",
                borderWidth: 2.5,
                backgroundColor: gradient,
                fill: true,
                tension: 0.35,
                pointBackgroundColor: "#4f46e5",
                pointBorderColor: "#fff",
                pointBorderWidth: 2,
                pointRadius: 4,
                pointHoverRadius: 6
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: true,
            aspectRatio: 2.2,
            plugins: {
                legend: { display: false },
                tooltip: {
                    backgroundColor: "#1e293b",
                    padding: 10,
                    cornerRadius: 8,
                    displayColors: false,
                    callbacks: {
                        label: ctx => ` ${ctx.raw} lượt thi`
                    }
                }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    grid: { color: "#f1f5f9" },
                    ticks: { color: "#94a3b8", font: { size: 11 }, stepSize: 1 }
                },
                x: {
                    grid: { display: false },
                    ticks: { color: "#94a3b8", font: { size: 11 } }
                }
            }
        }
    });
}

// ─── Recent Attempts Table ───────────────────────────────────────────────────
function renderRecentAttempts(attempts) {
    const tbody = document.getElementById("recentAttemptsTable");
    if (!tbody) return;

    if (!attempts || attempts.length === 0) {
        tbody.innerHTML = `<tr><td colspan="4" class="text-center py-4 text-muted">Chưa có lượt làm bài nào.</td></tr>`;
        return;
    }

    tbody.innerHTML = attempts.map(item => {
        const scoreBadge = item.score >= 8
            ? "bg-success-subtle text-success"
            : item.score >= 5
                ? "bg-primary-subtle text-primary"
                : "bg-danger-subtle text-danger";

        return `
            <tr>
                <td class="ps-3 fw-semibold">${esc(item.studentName)}</td>
                <td>
                    <span class="text-truncate d-inline-block" style="max-width:160px" title="${esc(item.examTitle)}">
                        ${esc(item.examTitle)}
                    </span>
                </td>
                <td class="text-center">
                    <span class="badge rounded-pill ${scoreBadge}">${item.score.toFixed(1)}</span>
                </td>
                <td class="pe-3 text-end text-muted small">${item.time}</td>
            </tr>`;
    }).join("");
}

// ─── Helpers ────────────────────────────────────────────────────────────────
function fmt(n)   { return Number(n).toLocaleString(); }
function esc(str) {
    if (!str) return "";
    return str.replace(/&/g,"&amp;").replace(/</g,"&lt;").replace(/>/g,"&gt;");
}

function showError() {
    ["statUsers","statExams","statCategories","statAttempts","statQuestions"]
        .forEach(id => {
            const el = document.getElementById(id);
            if (el) el.innerHTML = `<span class="text-danger fs-6">Lỗi</span>`;
        });
    const tbody = document.getElementById("recentAttemptsTable");
    if (tbody) tbody.innerHTML = `<tr><td colspan="4" class="text-center py-4 text-danger">Không thể tải dữ liệu.</td></tr>`;
}
