const BASE_API = "http://localhost:8080/api";

let currentPage = 0;
let pageSize = 10;

// ================= INIT =================
document.addEventListener("DOMContentLoaded", () => {
    loadQuestions();

    document.getElementById("filterBtn").addEventListener("click", () => {
        currentPage = 0;
        loadQuestions();
    });
});

// ================= LOAD LIST =================
async function loadQuestions() {

    try {
        const params = {
            page: currentPage,
            size: pageSize,
            content: document.getElementById("content")?.value || "",
            type: document.getElementById("type")?.value || "",
            level: document.getElementById("level")?.value || ""
        };

        const queryString = buildQuery(params);
        const url = `${BASE_API}/question-bank/list${queryString ? "?" + queryString : ""}`;

        const response = await fetch(url);
        const result = await response.json();

        renderTable(result.data || []);
        renderPagination(result.totalPage || 0, result.pageIndex || 0);

    } catch (error) {
        console.error("Lỗi load questions:", error);
    }
}

// ================= TABLE =================
function renderTable(questions) {

    const tbody = document.getElementById("questionTable");
    tbody.innerHTML = "";

    if (!questions.length) {
        tbody.innerHTML = `
            <tr>
                <td colspan="6" class="text-center text-muted">
                    Không có dữ liệu
                </td>
            </tr>
        `;
        return;
    }

    questions.forEach((q, index) => {

        tbody.innerHTML += `
            <tr>
                <td>${index + 1}</td>

                <td>
                    <div class="fw-bold">${q.content ?? ""}</div>
                </td>

                <td>${q.categoryName ?? "Chưa có"}</td>

                <td><span class="badge bg-info">${q.type ?? ""}</span></td>

                <td><span class="badge bg-warning text-dark">${q.level ?? ""}</span></td>

                <td>
                    <div class="action-buttons d-flex justify-content-center gap-2">

                        <button class="btn btn-sm btn-info text-white view-question-btn"
                                data-id="${q.id}">
                            <i class="bi bi-eye-fill"></i>
                        </button>

                        <button class="btn btn-sm btn-warning text-white edit-question-btn"
                                data-id="${q.id}">
                            <i class="bi bi-pencil-fill"></i>
                        </button>

                        <button class="btn btn-sm btn-danger delete-question-btn"
                                data-id="${q.id}">
                            <i class="bi bi-trash-fill"></i>
                        </button>

                    </div>
                </td>
            </tr>
        `;
    });
}

// ================= CLICK EVENTS =================
document.addEventListener("click", function (e) {

    const viewBtn = e.target.closest(".view-question-btn");
    if (viewBtn) {
        openQuestionDetail(viewBtn.dataset.id);
        return;
    }

    const editBtn = e.target.closest(".edit-question-btn");
    if (editBtn) {
        window.location.href = `/admin/questions/update/${editBtn.dataset.id}`;
        return;
    }

    const deleteBtn = e.target.closest(".delete-question-btn");
    if (deleteBtn) {
        deleteQuestion(deleteBtn.dataset.id);
    }
});

// ================= DETAIL =================
async function openQuestionDetail(id) {

    try {
        const res = await fetch(`${BASE_API}/question-bank/detail/${id}`);
        const result = await res.json();

        if (!result.data) {
            alert("Không tìm thấy câu hỏi");
            return;
        }

        renderQuestionDetail(result.data);

        const modal = new bootstrap.Modal(
            document.getElementById("questionDetailModal")
        );
        modal.show();

    } catch (err) {
        console.error("Lỗi load detail:", err);
    }
}

// ================= DELETE =================
async function deleteQuestion(id) {

    if (!confirm("Bạn có chắc muốn xóa câu hỏi này?")) return;

    try {
        const res = await fetch(`${BASE_API}/question-bank/delete/${id}`, {
            method: "DELETE"
        });

        const result = await res.json().catch(() => null);

        if (!res.ok) {
            alert(result?.message || "Lỗi xóa câu hỏi");
            return;
        }

        alert(result?.message || "Xóa thành công!");
        loadQuestions();

    } catch (err) {
        console.error("Lỗi xóa:", err);
        alert("Không thể kết nối server");
    }
}

// ================= RENDER DETAIL =================
function renderQuestionDetail(q) {

    document.getElementById("detailContent").innerText = q.content || "";
    document.getElementById("detailCategory").innerText = q.categoryName || "";
    document.getElementById("detailType").innerText = q.type || "";
    document.getElementById("detailLevel").innerText = q.level || "";

    const optionsBox = document.getElementById("detailOptions");
    optionsBox.innerHTML = "";

    if (!q.options?.length) {
        optionsBox.innerHTML = `<div class="text-muted">Không có dữ liệu đáp án</div>`;
        return;
    }

    q.options.forEach(opt => {

        let isCorrect = false;

        if (q.type === "SINGLE_CHOICE") {
            isCorrect = q.correctAnswer === opt.key;
        } else if (q.type === "MULTIPLE_CHOICE") {
            isCorrect = (q.correctAnswerKeys || []).includes(opt.key);
        } else if (q.type === "TRUE_FALSE") {
            isCorrect = q.correctAnswer === opt.key;
        }

        optionsBox.innerHTML += `
            <div class="form-check mb-2">
                <input class="form-check-input" type="checkbox" disabled ${isCorrect ? "checked" : ""}>
                <label class="form-check-label ${isCorrect ? "text-success fw-bold" : ""}">
                    <b>${opt.key}.</b> ${opt.text}
                </label>
            </div>
        `;
    });
}

// ================= PAGINATION =================
function renderPagination(totalPage, currentPage) {

    const pagination = document.getElementById("pagination");
    pagination.innerHTML = "";

    // Prev button
    pagination.innerHTML += `
        <li class="page-item ${currentPage === 0 ? "disabled" : ""}">
            <button class="page-link" onclick="changePage(${currentPage - 1})">‹</button>
        </li>
    `;

    // First page
    pagination.innerHTML += `
        <li class="page-item ${currentPage === 0 ? "active" : ""}">
            <button class="page-link" onclick="changePage(0)">1</button>
        </li>
    `;

    // Dấu ... đầu
    if (currentPage > 2) {
        pagination.innerHTML += `<li class="page-item disabled"><span class="page-link">...</span></li>`;
    }

    // Các page gần currentPage
    for (let i = currentPage - 1; i <= currentPage + 1; i++) {
        if (i > 0 && i < totalPage - 1) {
            pagination.innerHTML += `
                <li class="page-item ${i === currentPage ? "active" : ""}">
                    <button class="page-link" onclick="changePage(${i})">${i + 1}</button>
                </li>
            `;
        }
    }

    // Dấu ... cuối
    if (currentPage < totalPage - 3) {
        pagination.innerHTML += `<li class="page-item disabled"><span class="page-link">...</span></li>`;
    }

    // Last page
    if (totalPage > 1) {
        pagination.innerHTML += `
            <li class="page-item ${currentPage === totalPage - 1 ? "active" : ""}">
                <button class="page-link" onclick="changePage(${totalPage - 1})">${totalPage}</button>
            </li>
        `;
    }

    // Next button
    pagination.innerHTML += `
        <li class="page-item ${currentPage === totalPage - 1 ? "disabled" : ""}">
            <button class="page-link" onclick="changePage(${currentPage + 1})">›</button>
        </li>
    `;

    document.getElementById("tableInfo").innerText =
        `Trang ${currentPage + 1} / ${totalPage}`;
}

// ================= PAGE CHANGE =================
function changePage(page) {
    currentPage = page;
    loadQuestions();
}

// ================= QUERY BUILDER =================
function buildQuery(params) {

    const query = new URLSearchParams();

    for (const key in params) {
        const value = params[key];
        if (value !== null && value !== "" && value !== undefined) {
            query.append(key, value);
        }
    }

    return query.toString();
}