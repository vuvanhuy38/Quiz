const API_URL = "http://localhost:8080/api/question-bank/list";

let currentPage = 0;
let pageSize = 10;

document.addEventListener("DOMContentLoaded", () => {
    loadQuestions();

    document.getElementById("filterBtn").addEventListener("click", () => {
        loadQuestions();
    });
});

// LOAD DATA
async function loadQuestions() {

    try {
        const params = {
            page: currentPage,
            size: pageSize,
            content: document.getElementById("content").value,
            type: document.getElementById("type").value,
            level: document.getElementById("level").value
        };

        const queryString = buildQuery(params);
        const url = API_URL + (queryString ? "?" + queryString : "");

        const response = await fetch(url);
        const result = await response.json();

        renderTable(result.data || []);
        renderPagination(result.totalPage, result.pageIndex);

    } catch (error) {
        console.error("Lỗi load questions:", error);
    }
}

// TABLE
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
                    <div>
                        <div class="fw-bold">${q.content ?? ""}</div>
                    </div>
                </td>

                <td>${q.categoryName ?? "Chưa có"}</td>

                <td><span class="badge bg-info">${q.type ?? ""}</span></td>

                <td><span class="badge bg-warning text-dark">${q.level ?? ""}</span></td>

                <td>
                    <div class="action-buttons d-flex justify-content-center gap-2">
                
                        <!-- VIEW -->
                        <button class="btn btn-sm btn-info text-white view-question-btn"
                                data-id="${q.id}">
                            <i class="bi bi-eye-fill"></i>
                        </button>
                
                        <!-- EDIT (UPDATE) -->
                        <button class="btn btn-sm btn-warning text-white edit-question-btn"
                                data-id="${q.id}">
                            <i class="bi bi-pencil-fill"></i>
                        </button>
                
                        <!-- DELETE -->
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

// PAGINATION
function renderPagination(totalPage, current) {

    const pagination = document.getElementById("pagination");
    pagination.innerHTML = "";

    pagination.innerHTML += `
        <li class="page-item ${current === 0 ? "disabled" : ""}">
            <button class="page-link" onclick="changePage(${current - 1})">‹</button>
        </li>
    `;

    for (let i = 0; i < totalPage; i++) {
        pagination.innerHTML += `
            <li class="page-item ${i === current ? "active" : ""}">
                <button class="page-link" onclick="changePage(${i})">
                    ${i + 1}
                </button>
            </li>
        `;
    }

    pagination.innerHTML += `
        <li class="page-item ${current === totalPage - 1 ? "disabled" : ""}">
            <button class="page-link" onclick="changePage(${current + 1})">›</button>
        </li>
    `;

    document.getElementById("tableInfo").innerText =
        `Trang ${current + 1} / ${totalPage}`;
}

// CHANGE PAGE
function changePage(page) {
    currentPage = page;
    loadQuestions();
}

// BUILD QUERY (FIX NULL / EMPTY)
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