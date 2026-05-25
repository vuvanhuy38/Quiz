const API = "http://localhost:8080/api";

let currentPage = 0;
let pageSize = 10;

document.addEventListener("DOMContentLoaded", () => {
    loadUsers();

    document.getElementById("filterBtn").addEventListener("click", () => {
        currentPage = 0;
        loadUsers();
    });
});

// click action (block / active / delete)
document.addEventListener("click", function (e) {

    const blockBtn = e.target.closest(".block-user-btn");
    if (blockBtn) {
        updateStatus(blockBtn.dataset.id, "block");
        return;
    }

    const activeBtn = e.target.closest(".active-user-btn");
    if (activeBtn) {
        updateStatus(activeBtn.dataset.id, "active");
        return;
    }

    const deleteBtn = e.target.closest(".delete-user-btn");
    if (deleteBtn) {
        deleteUser(deleteBtn.dataset.id);
    }
});

async function loadUsers() {

    try {
        const params = {
            page: currentPage,
            size: pageSize,
            name: document.getElementById("nameSearch").value,
            email: document.getElementById("emailSearch").value,
            phone: document.getElementById("phoneSearch").value
        };

        const queryString = buildQuery(params);

        const res = await fetch(`${API}/users/getAll?${queryString}`);
        const result = await res.json();

        const data = result.data || [];

        renderTable(data);
        renderPagination(result.totalPage, result.pageIndex);

    } catch (err) {
        console.error("Load users error:", err);
    }
}

function renderTable(users) {

    const tbody = document.getElementById("userTableBody");
    tbody.innerHTML = "";

    if (!users.length) {
        tbody.innerHTML = `
            <tr>
                <td colspan="8" class="text-center text-muted">
                    Không có dữ liệu
                </td>
            </tr>
        `;
        return;
    }

    users.forEach((u, index) => {

        tbody.innerHTML += `
            <tr>
                <td>${index + 1}</td>

                <td>${u.fullName ?? ""}</td>

                <td>${u.username ?? ""}</td>

                <td>${u.email ?? ""}</td>

                <td>${u.phone ?? ""}</td>

                <td>
                    <span class="badge bg-primary">${u.role ?? ""}</span>
                </td>

                <td>${renderStatus(u.status)}</td>

                <td class="text-center">

                    <button class="btn btn-sm btn-success active-user-btn"
                            data-id="${u.id}">
                        Active
                    </button>

                    <button class="btn btn-sm btn-warning block-user-btn"
                            data-id="${u.id}">
                        Block
                    </button>

                    <button class="btn btn-sm btn-danger delete-user-btn"
                            data-id="${u.id}">
                        Delete
                    </button>

                </td>
            </tr>
        `;
    });
}

function renderStatus(status) {
    if (status === "ACTIVE") {
        return `<span class="badge bg-success">ACTIVE</span>`;
    }
    if (status === "BLOCKED") {
        return `<span class="badge bg-secondary">BLOCKED</span>`;
    }
    return `<span class="badge bg-dark">UNKNOWN</span>`;
}

function renderPagination(totalPage, pageIndex) {

    const pagination = document.getElementById("pagination");
    pagination.innerHTML = "";

    pagination.innerHTML += `
        <li class="page-item ${pageIndex === 0 ? "disabled" : ""}">
            <button class="page-link" onclick="changePage(${pageIndex - 1})">‹</button>
        </li>
    `;

    for (let i = 0; i < totalPage; i++) {
        pagination.innerHTML += `
            <li class="page-item ${i === pageIndex ? "active" : ""}">
                <button class="page-link" onclick="changePage(${i})">
                    ${i + 1}
                </button>
            </li>
        `;
    }

    pagination.innerHTML += `
        <li class="page-item ${pageIndex === totalPage - 1 ? "disabled" : ""}">
            <button class="page-link" onclick="changePage(${pageIndex + 1})">›</button>
        </li>
    `;

    document.getElementById("tableInfo").innerText =
        `Trang ${pageIndex + 1} / ${totalPage}`;
}

function changePage(page) {
    currentPage = page;
    loadUsers();
}

function buildQuery(params) {
    const query = new URLSearchParams();

    for (const k in params) {
        if (params[k] !== "" && params[k] !== null && params[k] !== undefined) {
            query.append(k, params[k]);
        }
    }

    return query.toString();
}

async function updateStatus(id, type) {

    try {

        const url = type === "block"
            ? `${API}/users/block/${id}`
            : `${API}/users/active/${id}`;

        const res = await fetch(url, { method: "PUT" });
        const result = await res.json();

        alert(result.message);

        loadUsers();

    } catch (err) {
        console.error(err);
    }
}

async function deleteUser(id) {

    if (!confirm("Xoá user này?")) return;

    try {

        const res = await fetch(`${API}/users/delete/${id}`, {
            method: "DELETE"
        });

        const result = await res.json();

        alert(result.message);

        loadUsers();

    } catch (err) {
        console.error(err);
    }
}