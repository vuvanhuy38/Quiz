// exam-result.js

document.addEventListener("DOMContentLoaded", () => {

    const raw = sessionStorage.getItem("examResult");

    if (!raw) {
        alert("Không tìm thấy dữ liệu kết quả bài thi");
        window.location.href = "/";
        return;
    }

    const data = JSON.parse(raw);

    document.getElementById("score").textContent = data.score ?? 0;

    document.getElementById("scoreCircle").textContent = data.score ?? 0;

    document.getElementById("correctCount").textContent = data.correctCount ?? 0;

    document.getElementById("totalQuestions").textContent = data.totalQuestions ?? 0;

    document.getElementById("status").textContent = data.status ?? "COMPLETED";

    if (data.finishedAt) {
        const date = new Date(data.finishedAt);

        document.getElementById("finishedAt").textContent = date.toLocaleString("vi-VN");
    }
});