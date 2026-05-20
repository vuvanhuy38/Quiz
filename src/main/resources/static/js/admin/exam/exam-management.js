const API_URL = "http://localhost:8080/api/exams/getAll";

let currentPage = 0;
let pageSize = 10;

document.addEventListener("DOMContentLoaded", () => {
    loadExams();

    document.getElementById("filterBtn").addEventListener("click", () => {
        loadExams();
    });
});

document.addEventListener("click", function (e) {

    const btn = e.target.closest(".edit-exam-btn");
    if (!btn) return;

    const examId = btn.getAttribute("data-id");

    goToEditExam(examId);
});

async function loadExams() {

    try {
        const params = {
            page: currentPage,
            size: pageSize,
            title: document.getElementById("searchInput").value,
            categoryId: document.getElementById("categoryFilter").value,
            status: document.getElementById("statusFilter").value,
            sort: document.getElementById("sortFilter").value
        };

        const queryString = buildQuery(params);

        const url = "http://localhost:8080/api/exams/getAll" + (queryString ? "?" + queryString : "");

        const response = await fetch(url);

        const result = await response.json();

        renderTable(result.data || []);

        renderPagination(result.totalPage, result.pageIndex);

    }  catch (error) {
        console.error("Lỗi load exams:", error);
    }
}

function renderTable(exams) {

    const tbody = document.getElementById("examTableBody");
    tbody.innerHTML = "";

    if (!exams.length) {
        tbody.innerHTML = `
            <tr>
                <td colspan="8" class="text-center text-muted">
                    Không có dữ liệu
                </td>
            </tr>
        `;
        return;
    }

    exams.forEach((exam, index) => {

        const row = `
            <tr>
                <td>${index + 1}</td>

                <td>
                    <div class="exam-title">
                        <h6 class="mb-1">${exam.title ?? ""}</h6>
                        <small class="text-muted">${exam.description ?? ""}</small>
                    </div>
                </td>

                <td>${exam.categoryName ?? "Chưa có"}</td>

                <td>${exam.totalQuestions ?? 0} câu</td>

                <td>${exam.timeLimit ?? 0} phút</td>

                <td>
                    ${renderStatus(exam.status)}
                </td>

                <td>${formatDate(exam.createdAt)}</td>

                <td>
                    <div class="action-buttons d-flex justify-content-center gap-2">
                        <button class="btn btn-sm btn-info text-white">
                            <i class="bi bi-eye-fill"></i>
                        </button>

                        <button class="btn btn-sm btn-warning text-white edit-exam-btn"
                                data-id="${exam.id}">
                            <i class="bi bi-pencil-fill"></i>
                        </button>

                        <button class="btn btn-sm btn-danger">
                            <i class="bi bi-trash-fill"></i>
                        </button>
                    </div>
                </td>
            </tr>
        `;

        tbody.innerHTML += row;
    });
}

function renderPagination(totalPage, currentPage) {

    const pagination = document.getElementById("pagination");
    pagination.innerHTML = "";

    // Prev button
    pagination.innerHTML += `
        <li class="page-item ${currentPage === 0 ? "disabled" : ""}">
            <button class="page-link"
                    onclick="changePage(${currentPage - 1})">
                ‹
            </button>
        </li>
    `;

    // First page
    pagination.innerHTML += `
        <li class="page-item ${currentPage === 0 ? "active" : ""}">
            <button class="page-link"
                    onclick="changePage(0)">
                1
            </button>
        </li>
    `;

    // Dấu ...
    if (currentPage > 2) {
        pagination.innerHTML += `
            <li class="page-item disabled">
                <span class="page-link">...</span>
            </li>
        `;
    }

    // Các page gần current page
    for (let i = currentPage - 1; i <= currentPage + 1; i++) {

        if (i > 0 && i < totalPage - 1) {

            pagination.innerHTML += `
                <li class="page-item ${i === currentPage ? "active" : ""}">
                    <button class="page-link"
                            onclick="changePage(${i})">
                        ${i + 1}
                    </button>
                </li>
            `;
        }
    }

    // Dấu ...
    if (currentPage < totalPage - 3) {
        pagination.innerHTML += `
            <li class="page-item disabled">
                <span class="page-link">...</span>
            </li>
        `;
    }

    // Last page
    if (totalPage > 1) {
        pagination.innerHTML += `
            <li class="page-item ${currentPage === totalPage - 1 ? "active" : ""}">
                <button class="page-link"
                        onclick="changePage(${totalPage - 1})">
                    ${totalPage}
                </button>
            </li>
        `;
    }

    // Next button
    pagination.innerHTML += `
        <li class="page-item ${currentPage === totalPage - 1 ? "disabled" : ""}">
            <button class="page-link"
                    onclick="changePage(${currentPage + 1})">
                ›
            </button>
        </li>
    `;

    document.getElementById("tableInfo").innerText =
        `Trang ${currentPage + 1} / ${totalPage}`;
}

function changePage(page) {
    currentPage = page;
    loadExams();
}

function renderStatus(status) {
    if (status === "ACTIVE") {
        return `<span class="badge bg-success">Hoạt động</span>`;
    }
    if (status === "INACTIVE") {
        return `<span class="badge bg-secondary">Ẩn</span>`;
    }
    return `<span class="badge bg-dark">Không rõ</span>`;
}

function formatDate(dateStr) {
    if (!dateStr) return "";
    const date = new Date(dateStr);
    return date.toLocaleDateString("vi-VN");
}

function buildQuery(params) {
    const query = new URLSearchParams();

    for (const key in params) {
        if (params[key] !== null && params[key] !== "" && params[key] !== undefined) {
            query.append(key, params[key]);
        }
    }

    return query.toString();
}

function goToEditExam(examId) {
    window.location.href = `/admin/exams/update/${examId}`;
}