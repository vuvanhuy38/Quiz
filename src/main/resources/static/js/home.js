async function loadHomeData() {
    try {
        const response = await fetch("http://localhost:8080/api/exams/home");
        const result = await response.json();
        const data = result.data;

        renderExams("featuredExams", data.featuredExams);
        renderExams("latestExams", data.latestExams);
    } catch (error) {

        console.error(error);
        document.getElementById("featuredExams").innerHTML =
            `<div class="text-danger">Không thể tải dữ liệu</div>`;
        document.getElementById("latestExams").innerHTML =
            `<div class="text-danger">Không thể tải dữ liệu</div>`;
    }
}

async function searchExam() {
    const keyword = document.getElementById("searchInput").value.trim();
    if (!keyword) {
        // Hiện lại 2 section khi xóa từ khóa
        document.getElementById("featured").style.display = "";
        document.getElementById("latestSection").style.display = "";
        document.getElementById("searchResults").style.display = "none";

        loadHomeData();
        return;
    }
    try {
        const response = await fetch(
            `http://localhost:8080/api/exams/search?title=${encodeURIComponent(keyword)}`
        );
        const result = await response.json();
        // Ẩn 2 section chính
        document.getElementById("featured").style.display = "none";
        document.getElementById("latestSection").style.display = "none";

        // Hiện section kết quả tìm kiếm
        const searchSection = document.getElementById("searchResults");
        searchSection.style.display = "";
        renderExams("searchResultExams", result.data);
    } catch (error) {
        console.error(error);
        document.getElementById("searchResultExams").innerHTML =
            `<div class="text-danger">Không tìm kiếm được dữ liệu</div>`;
    }
}

function renderExams(containerId, exams) {

    const container = document.getElementById(containerId);
    if (!exams || exams.length === 0) {
        container.innerHTML = `
            <div class="col-12">
                <div class="alert alert-warning">
                    Không có đề thi nào
                </div>
            </div>
        `;
        return;
    }
    container.innerHTML = exams.map(exam => `
        <div class="col-md-4">
            <div class="card exam-card">
                <div class="card-body d-flex flex-column">
                    <div class="d-flex justify-content-between align-items-start mb-3">
                        <h5 class="exam-title">
                            ${exam.title}
                        </h5>
                        <span class="badge-time">
                         ${exam.timeLimit} phút
                        </span>
                    </div>
                    <p class="text-muted">${exam.description ?? "Không có mô tả"}</p>
                    <div class="mt-auto">
                        <div class="info-text mb-2">
                            Số câu hỏi: <strong>${exam.totalQuestions ?? 0}</strong>
                        </div>
                        <div class="info-text mb-2">
                            Lượt làm: <strong>${exam.attemptCount ?? 0}</strong>
                        </div>
                        <div class="info-text mb-3">
                            Ngày tạo:<strong>${formatDate(exam.createdAt)}</strong>
                        </div>
                        <a href="/exams/${exam.id}" class="btn btn-primary w-100">Chi tiết</a>
                    </div>
                </div>
            </div>
        </div>
    `).join("");
}

function formatDate(dateString) {
    const date = new Date(dateString);
    return date.toLocaleDateString("vi-VN");
}

document.getElementById("searchBtn")
    .addEventListener("click", searchExam);

document.getElementById("searchInput")
    .addEventListener("keypress", function (event) {
        if (event.key === "Enter") {
            searchExam();
        }
    });

loadHomeData();