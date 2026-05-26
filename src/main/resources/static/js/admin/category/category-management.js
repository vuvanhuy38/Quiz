const API = "http://localhost:8080/api";

const tbody = document.getElementById("categoryTableBody");
const searchInput = document.getElementById("searchInput");
const sortFilter = document.getElementById("sortFilter");

const createModal = new bootstrap.Modal(document.getElementById("createCategoryModal"));
const updateModal = new bootstrap.Modal(document.getElementById("updateCategoryModal"));

let currentPage = 0;
let pageSize = 5;
let updatingId = null;

document.addEventListener("DOMContentLoaded", () => {
    loadCategories();

    document.getElementById("filterBtn").addEventListener("click", () => {
        currentPage = 0;
        loadCategories();
    });
});

// ================= LOAD =================
async function loadCategories() {
    try {
        const params = {
            page: currentPage,
            size: pageSize,
            name: searchInput.value.trim(),
            sort: sortFilter.value
        };

        const res = await fetch(`${API}/category/getlist?${buildQuery(params)}`);
        const result = await res.json();

        renderTable(result.data || []);
        renderPagination(result.totalPage, result.pageIndex);

    } catch (err) {
        console.error(err);
        tbody.innerHTML = `
            <tr>
                <td colspan="7" class="text-center text-danger">Không tải được dữ liệu</td>
            </tr>
        `;
    }
}

// ================= RENDER TABLE =================
function renderTable(data) {
    if (!data.length) {
        tbody.innerHTML = `
            <tr>
                <td colspan="7" class="text-center">Không có dữ liệu</td>
            </tr>
        `;
        document.getElementById("tableInfo").innerText = "";
        return;
    }

    let html = "";

    data.forEach((parent, index) => {

        // PARENT ROW
        html += `
            <tr class="parent-row">
                <td>${currentPage * pageSize + index + 1}</td>
                <td><div class="category-name parent-name">${parent.name}</div></td>
                <td>${parent.description || "-"}</td>
                <td><span class="badge bg-primary">Danh mục cha</span></td>
                <td>-</td>
                <td>${parent.children?.length || 0}</td>
                <td class="text-center">
                    <button class="btn btn-sm btn-warning me-1 edit-btn" data-id="${parent.id}">
                        <i class="bi bi-pencil-square"></i>
                    </button>
                    <button class="btn btn-sm btn-danger delete-btn" data-id="${parent.id}">
                        <i class="bi bi-trash"></i>
                    </button>
                </td>
            </tr>
        `;

        // CHILD ROWS
        (parent.children || []).forEach(child => {
            html += `
                <tr class="child-row">
                    <td></td>
                    <td><div class="category-name child-name">${child.name}</div></td>
                    <td>${child.description || "-"}</td>
                    <td><span class="badge bg-success">Danh mục con</span></td>
                    <td>${parent.name}</td>
                    <td>0</td>
                    <td class="text-center">
                        <button class="btn btn-sm btn-warning me-1 edit-btn" data-id="${child.id}">
                            <i class="bi bi-pencil-square"></i>
                        </button>
                        <button class="btn btn-sm btn-danger delete-btn" data-id="${child.id}">
                            <i class="bi bi-trash"></i>
                        </button>
                    </td>
                </tr>
            `;
        });
    });

    tbody.innerHTML = html;
}

// ================= PAGINATION =================
function renderPagination(totalPage, currentPage) {
    const pagination = document.getElementById("pagination");
    if (!pagination) return;

    pagination.innerHTML = "";

    // PREV
    pagination.innerHTML += `
        <li class="page-item ${currentPage === 0 ? 'disabled' : ''}">
            <button class="page-link" onclick="changePage(${currentPage - 1})">‹</button>
        </li>
    `;

    // First page
    pagination.innerHTML += `
        <li class="page-item ${currentPage === 0 ? 'active' : ''}">
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
                <li class="page-item ${i === currentPage ? 'active' : ''}">
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
            <li class="page-item ${currentPage === totalPage - 1 ? 'active' : ''}">
                <button class="page-link" onclick="changePage(${totalPage - 1})">${totalPage}</button>
            </li>
        `;
    }

    // NEXT
    pagination.innerHTML += `
        <li class="page-item ${currentPage === totalPage - 1 ? 'disabled' : ''}">
            <button class="page-link" onclick="changePage(${currentPage + 1})">›</button>
        </li>
    `;

    document.getElementById("tableInfo").innerText = `Trang ${currentPage + 1} / ${totalPage}`;
}

function changePage(page) {
    currentPage = page;
    loadCategories();
}

// ================= EDIT / DELETE =================
document.addEventListener("click", (e) => {
    const editBtn = e.target.closest(".edit-btn");
    if (editBtn) {
        openUpdateModal(editBtn.dataset.id);
    }

    const deleteBtn = e.target.closest(".delete-btn");
    if (deleteBtn) {
        deleteCategory(deleteBtn.dataset.id);
    }
});

// ================= CREATE =================
document.getElementById("createCategoryModal")
    .addEventListener("show.bs.modal", async () => {
        document.getElementById("categoryName").value = "";
        document.getElementById("categoryDescription").value = "";
        document.getElementById("categoryParent").value = "";

        const res = await fetch(`${API}/category/parents`);
        const result = await res.json();

        const select = document.getElementById("categoryParent");
        select.innerHTML = `<option value="">Không có (là danh mục cha)</option>`;
        (result.data || []).forEach(p => {
            select.innerHTML += `<option value="${p.id}">${p.name}</option>`;
        });
    });

document.getElementById("btnSaveCategory").onclick = async () => {
    const name = document.getElementById("categoryName").value.trim();
    const description = document.getElementById("categoryDescription").value.trim();
    const parentId = document.getElementById("categoryParent").value || null;

    if (!name) {
        alert("Vui lòng nhập tên danh mục");
        return;
    }

    try {
        const res = await fetch(`${API}/category`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ name, description, parentId })
        });

        const result = await res.json();

        if (!res.ok) {
            alert(result.message || "Tạo thất bại");
            return;
        }

        createModal.hide();
        currentPage = 0;
        loadCategories();

    } catch (err) {
        console.error(err);
        alert("Tạo thất bại");
    }
};

// ================= UPDATE =================
async function openUpdateModal(id) {
    updatingId = id;

    try {
        const res = await fetch(`${API}/category/detail/${id}`);
        const result = await res.json();
        const data = result.data;

        document.getElementById("updateCategoryName").value = data.name || "";
        document.getElementById("updateCategoryDescription").value = data.description || "";

        updateModal.show();

    } catch (err) {
        console.error(err);
        alert("Không tải được thông tin danh mục");
    }
}

document.getElementById("btnUpdateCategory").onclick = async () => {
    const name = document.getElementById("updateCategoryName").value.trim();
    const description = document.getElementById("updateCategoryDescription").value.trim();

    if (!name) {
        alert("Vui lòng nhập tên danh mục");
        return;
    }

    try {
        const res = await fetch(`${API}/category/update/${updatingId}`, {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ name, description })
        });

        const result = await res.json();

        if (!res.ok) {
            alert(result.message || "Cập nhật thất bại");
            return;
        }

        updateModal.hide();
        loadCategories();

    } catch (err) {
        console.error(err);
        alert("Cập nhật thất bại");
    }
};

// ================= DELETE =================
async function deleteCategory(id) {
    if (!confirm("Bạn có chắc muốn xóa danh mục này?")) return;

    try {
        const res = await fetch(`${API}/category/delete/${id}`, {
            method: "DELETE"
        });

        const result = await res.json();

        if (!res.ok) {
            alert(result.message || "Xóa thất bại");
            return;
        }

        loadCategories();

    } catch (err) {
        console.error(err);
        alert("Xóa thất bại");
    }
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