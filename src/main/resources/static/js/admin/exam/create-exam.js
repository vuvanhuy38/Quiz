const API = "http://localhost:8080/api";

// ELEMENT
const parentCategory = document.getElementById("parentCategory");
const childCategory = document.getElementById("childCategory");
const titleInput = document.getElementById("title");
const descriptionInput = document.getElementById("description");
const timeLimitInput = document.getElementById("timeLimit");
const statusInput = document.getElementById("status");
const saveExamBtn = document.getElementById("saveExamBtn");
const examIdInput = document.getElementById("examId");

const tabInfo = document.getElementById("tabInfo");
const tabQuestion = document.getElementById("tabQuestion");

const infoSection = document.getElementById("infoSection");
const questionSection = document.getElementById("questionSection");

const questionList = document.getElementById("questionList");

const btnAddManual = document.getElementById("btnAddManual");
const btnImportBank = document.getElementById("btnImportBank");
const btnSaveQuestions = document.getElementById("btnSaveQuestions");
const btnSaveManualQuestion = document.getElementById("btnSaveManualQuestion");

const questionType = document.getElementById("questionType");
const optionsContainer = document.getElementById("optionsContainer");
const btnAddOption = document.getElementById("btnAddOption");

const addQuestionModal = new bootstrap.Modal(
    document.getElementById("addQuestionModal")
);

const importBankModal = new bootstrap.Modal(
    document.getElementById("importBankModal")
);

let questions = [];
let editingIndex = null;
let pageSize = 4;
let currentPage = 0;
let bankPreviewData = [];
let bankTotalPages = 0;
let bankTotalElements = 0;

// DETECT MODE
function getExamIdFromUrl() {
    const parts = window.location.pathname.split("/").filter(Boolean);
    return window.location.pathname.includes("/update/")
        ? parts[parts.length - 1]
        : null;
}

const urlId = getExamIdFromUrl();
const IS_UPDATE = !!urlId;

// INIT
document.addEventListener("DOMContentLoaded", async () => {
    await loadParents();

    if (IS_UPDATE) {
        examIdInput.value = urlId;
        tabQuestion.classList.remove("disabled");
        await loadExam(urlId);
        showQuestionTab();
    } else {
        showInfoTab();
    }
});

// TAB
function showInfoTab() {
    setActive(tabInfo);
    infoSection.style.display = "block";
    questionSection.style.display = "none";
}

function showQuestionTab() {
    setActive(tabQuestion);
    infoSection.style.display = "none";
    questionSection.style.display = "block";
}

function setActive(activeTab) {
    tabInfo.classList.remove("active");
    tabQuestion.classList.remove("active");
    activeTab.classList.add("active");
}

tabInfo.onclick = showInfoTab;
tabQuestion.onclick = () => {
    if (tabQuestion.classList.contains("disabled")) return;
    showQuestionTab();
};

// LOAD CHILD CATEGORY
async function loadChildCategories(parentId, selectedId = null) {
    const res = await fetch(`${API}/category/children/${parentId}`);
    const result = await res.json();

    childCategory.disabled = false;
    childCategory.innerHTML = `
        <option value="">Chọn</option>
        ${(result.data || [])
        .map(c => `<option value="${c.id}">${c.name}</option>`)
        .join("")}
    `;

    if (selectedId) childCategory.value = selectedId;
}

// LOAD EXAM
async function loadExam(id) {
    const res = await fetch(`${API}/exams/detail/${id}`);
    const result = await res.json();
    const data = result.data;

    titleInput.value = data.title || "";
    descriptionInput.value = data.description || "";
    timeLimitInput.value = data.timeLimit || "";
    statusInput.value = data.status || "";

    parentCategory.value = data.parentCategoryId;
    await loadChildCategories(data.parentCategoryId, data.categoryId);

    questions = data.questions || [];
    document.getElementById("questionCount").innerText = questions.length;
    editingIndex = null;

    renderQuestions();
}

// RENDER QUESTIONS
function renderQuestions() {
    if (!questions.length) {
        questionList.innerHTML = `
            <div class="alert alert-secondary">
                Chưa có câu hỏi
            </div>`;
        return;
    }

    questionList.innerHTML = questions.map((q, index) => `
        <div class="card mb-3">
            <div class="card-body">

                <div class="d-flex justify-content-between">
                    <div>
                        <h5>Câu ${index + 1}</h5>
                        <span class="badge bg-dark">${q.type}</span>
                        <span class="badge bg-secondary">${q.level}</span>
                    </div>

                    <div class="d-flex gap-2">
                        <button class="btn btn-warning btn-sm" onclick="editQuestion(${index})">Sửa</button>
                        <button class="btn btn-danger btn-sm" onclick="deleteQuestion(${index})">Xóa</button>
                    </div>
                </div>

                <p class="fw-bold mt-3">${q.content}</p>

                ${(q.options || []).map(o => {
        const isCorrect =
            q.correctAnswer === o.key ||
            (q.correctAnswerKeys || []).includes(o.key);

        return `
                        <div class="border rounded p-2 mb-2 ${isCorrect ? 'border-success bg-light' : ''}">
                            <strong>${o.key}.</strong> ${o.text}
                        </div>`;
    }).join("")}

            </div>
        </div>
    `).join("");
}

// CREATE EXAM
async function createExam() {
    const payload = {
        title: titleInput.value.trim(),
        categoryId: childCategory.value || null,
        description: descriptionInput.value.trim(),
        timeLimit: Number(timeLimitInput.value),
        status: statusInput.value
    };

    const res = await fetch(`${API}/exams/create`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload)
    });

    const result = await res.json();
    const id = result.data;

    window.location.href = `/admin/exams/update/${id}`;
}

// UPDATE EXAM
async function updateExam() {
    const id = examIdInput.value;

    const payload = {
        title: titleInput.value.trim(),
        categoryId: childCategory.value || null,
        description: descriptionInput.value.trim(),
        timeLimit: Number(timeLimitInput.value),
        status: statusInput.value
    };

    await fetch(`${API}/exams/update/${id}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload)
    });

    alert("Cập nhật thành công");
}

saveExamBtn.onclick = async () => {
    if (!IS_UPDATE) await createExam();
    else await updateExam();
};

// SAVE QUESTIONS
btnSaveQuestions.onclick = async () => {
    const examId = examIdInput.value;

    const payload = questions.map(q => ({
        id: q.id || null,
        content: q.content,
        type: q.type,
        options: q.options,
        correctAnswer: q.correctAnswer || null,
        correctAnswerKeys: q.correctAnswerKeys || [],
        level: q.level
    }));

    try {
        const res = await fetch(`${API}/exam-question/create/${examId}`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(payload)
        });

        const result = await res.json();
        alert(result.message || "Lưu thành công");

        await loadExam(examId);
    } catch {
        alert("Lưu thất bại");
    }
};

// OPTION
function createOptionRow(key = "", type = "SINGLE_CHOICE", checked = false, text = "") {
    const inputType = type === "MULTIPLE_CHOICE" ? "checkbox" : "radio";

    return `
        <div class="row mb-2 option-item">
            <div class="col-2">
                <input class="form-control option-key" value="${key}">
            </div>

            <div class="col-7">
                <input class="form-control option-text" value="${text}" placeholder="Đáp án">
            </div>

            <div class="col-1 d-flex align-items-center">
                <input type="${inputType}" class="form-check-input option-correct" ${checked ? "checked" : ""}>
            </div>

            <div class="col-2">
                <button type="button" class="btn btn-danger btn-sm btn-remove-option">Xóa</button>
            </div>
        </div>
    `;
}

function renderOptionInputs(type) {
    if (type === "TRUE_FALSE") {
        btnAddOption.style.display = "none";
        optionsContainer.innerHTML =
            createOptionRow("TRUE", type, false, "Đúng") +
            createOptionRow("FALSE", type, false, "Sai");
        return;
    }

    btnAddOption.style.display = "inline-block";
    optionsContainer.innerHTML =
        createOptionRow("A", type) +
        createOptionRow("B", type) +
        createOptionRow("C", type) +
        createOptionRow("D", type);
}

questionType.onchange = () => renderOptionInputs(questionType.value);

// ADD QUESTION
btnAddManual.onclick = () => {
    editingIndex = null;

    document.getElementById("questionContent").value = "";
    document.getElementById("questionLevel").value = "EASY";
    questionType.value = "SINGLE_CHOICE";

    renderOptionInputs("SINGLE_CHOICE");
    addQuestionModal.show();
};

// SAVE QUESTION
btnSaveManualQuestion.onclick = () => {
    const content = document.getElementById("questionContent").value.trim();
    const type = questionType.value;
    const level = document.getElementById("questionLevel").value;

    if (!content) return alert("Nhập nội dung câu hỏi");

    const optionItems = document.querySelectorAll(".option-item");

    const options = [];
    let correctAnswer = null;
    let correctAnswerKeys = [];

    optionItems.forEach(item => {
        const key = item.querySelector(".option-key").value;
        const text = item.querySelector(".option-text").value;
        const checked = item.querySelector(".option-correct").checked;

        options.push({ key, text });

        if ((type === "SINGLE_CHOICE" || type === "TRUE_FALSE") && checked) {
            correctAnswer = key;
        }

        if (type === "MULTIPLE_CHOICE" && checked) {
            correctAnswerKeys.push(key);
        }
    });

    const questionData = {
        ...(editingIndex !== null && questions[editingIndex]?.id
            ? { id: questions[editingIndex].id }
            : {}),
        content,
        type,
        options,
        correctAnswer,
        correctAnswerKeys,
        level
    };

    if (editingIndex !== null) {
        questions[editingIndex] = questionData;
    } else {
        questions.push(questionData);
    }

    document.getElementById("questionCount").innerText = questions.length;
    renderQuestions();
    addQuestionModal.hide();
    editingIndex = null;
};

// DELETE
async function deleteQuestion(index) {
    const q = questions[index];

    if (!confirm("Xóa câu hỏi này?")) return;

    if (q.id) {
        await fetch(`${API}/exam-question/delete/${q.id}`, {
            method: "DELETE"
        });
    }

    questions.splice(index, 1);
    document.getElementById("questionCount").innerText = questions.length;
    renderQuestions();
}

// EDIT
function editQuestion(index) {
    const q = questions[index];
    editingIndex = index;

    document.getElementById("questionContent").value = q.content;
    questionType.value = q.type;
    document.getElementById("questionLevel").value = q.level;

    optionsContainer.innerHTML = "";

    q.options.forEach(o => {
        const checked =
            q.correctAnswer === o.key ||
            (q.correctAnswerKeys || []).includes(o.key);

        optionsContainer.innerHTML += createOptionRow(o.key, q.type, checked, o.text);
    });

    btnAddOption.style.display = q.type === "TRUE_FALSE" ? "none" : "inline-block";

    addQuestionModal.show();
}

// ================= BANK MODAL =================
btnImportBank.onclick = () => {
    if (!childCategory.value) {
        alert("Vui lòng chọn danh mục con");
        return;
    }

    currentPage = 0;
    bankPreviewData = [];

    document.getElementById("bankQuestionList").innerHTML = "";
    document.getElementById("bankSelectBar").style.display = "none";

    loadBankModal();
    importBankModal.show();
};

async function loadBankModal() {
    const params = {
        page: currentPage,
        size: pageSize,
        content: document.getElementById("bankContent")?.value || "",
        type: document.getElementById("bankType")?.value || "",
        level: document.getElementById("bankLevel")?.value || "",
        categoryId: childCategory.value || ""
    };

    const res = await fetch(`${API}/question-bank/modal?${buildQuery(params)}`);
    const result = await res.json();

    bankPreviewData = result.data || [];
    bankTotalPages = result.totalPage || 0;
    bankTotalElements = result.totalElement || 0;

    renderBankList();
    renderBankPagination(bankTotalPages, currentPage);
}

// ================= RENDER BANK =================
function renderBankList() {
    const container = document.getElementById("bankQuestionList");
    const bar = document.getElementById("bankSelectBar");

    if (!bankPreviewData.length) {
        container.innerHTML = `
            <div class="alert alert-secondary">
                Không tìm thấy câu hỏi
            </div>
        `;
        bar.style.display = "none";
        return;
    }

    bar.style.display = "flex";
    updateBankSelectedCount();

    container.innerHTML = bankPreviewData.map((q, i) => {
        const levelClass =
            q.level === "EASY"
                ? "bg-success"
                : q.level === "MEDIUM"
                    ? "bg-warning text-dark"
                    : "bg-danger";

        const optionsHtml = (q.options || []).map(o => {
            const isCorrect =
                q.correctAnswer === o.key ||
                (q.correctAnswerKeys || []).includes(o.key);

            return `
                <div class="border rounded p-2 mb-2 ${isCorrect ? 'border-success bg-light' : ''}">
                    <div class="d-flex justify-content-between align-items-center">
                        <div>
                            <strong>${o.key}.</strong> ${o.text}
                        </div>
                        ${isCorrect ? `<span class="badge bg-success">Đúng</span>` : ''}
                    </div>
                </div>
            `;
        }).join("");

        return `
            <div class="card mb-3 shadow-sm">
                <div class="card-body">
                    <div class="d-flex gap-3 align-items-start">

                        <!-- CHECKBOX -->
                        <div class="pt-1">
                            <input type="checkbox"
                                   class="form-check-input bank-check"
                                   data-index="${i}">
                        </div>

                        <!-- CONTENT -->
                        <div class="flex-grow-1">

                            <!-- HEADER -->
                            <div class="d-flex justify-content-between align-items-start flex-wrap gap-2">
                                <div class="d-flex flex-wrap gap-2">
                                    <span class="badge bg-dark">${q.type}</span>
                                    <span class="badge ${levelClass}">${q.level}</span>
                                    <span class="badge bg-info text-dark">${q.categoryName || 'Không có danh mục'}</span>
                                </div>

                                <!-- TOGGLE -->
                                <button class="btn btn-sm btn-outline-dark"
                                        type="button"
                                        data-bs-toggle="collapse"
                                        data-bs-target="#bankOption${i}">
                                    <i class="bi bi-eye"></i>
                                    Xem đáp án
                                </button>
                            </div>

                            <!-- QUESTION -->
                            <p class="fw-bold fs-5 mt-3 mb-2">${q.content}</p>

                            <!-- OPTIONS -->
                            <div class="collapse" id="bankOption${i}">
                                <div class="mt-3">
                                    ${optionsHtml}
                                </div>
                            </div>

                        </div>
                    </div>
                </div>
            </div>
        `;
    }).join("");

    document.querySelectorAll(".bank-check").forEach(cb => {
        cb.onchange = updateBankSelectedCount;
    });
}

function updateBankSelectedCount() {
    const total = document.querySelectorAll(".bank-check:checked").length;
    document.getElementById("bankSelectedCount").innerText = `${total} câu được chọn`;
}

// ================= SELECT ALL =================
document.getElementById("selectAllBank").onchange = function () {
    document.querySelectorAll(".bank-check").forEach(cb => {
        cb.checked = this.checked;
    });
    updateBankSelectedCount();
};

// ================= SEARCH BANK =================
document.getElementById("btnSearchBank").onclick = async () => {
    document.getElementById("selectAllBank").checked = false;
    currentPage = 0;
    await loadBankModal();
};

// ================= CONFIRM IMPORT =================
document.getElementById("btnConfirmImport").onclick = async () => {
    const checkedQuestions = document.querySelectorAll(".bank-check:checked");

    if (checkedQuestions.length === 0) {
        alert("Chưa chọn câu hỏi");
        return;
    }

    const questionBankIds = [];

    checkedQuestions.forEach(checkbox => {
        const questionIndex = checkbox.dataset.index;
        const questionData = bankPreviewData[questionIndex];
        questionBankIds.push(questionData.id);
    });

    const response = await fetch(`${API}/exam-question/preview`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ questionBankIds })
    });

    const result = await response.json();
    const previewQuestions = result.data || [];

    previewQuestions.forEach(question => {
        questions.push(question);
    });

    document.getElementById("questionCount").innerText = questions.length;
    renderQuestions();
    importBankModal.hide();
};

// ================= PAGINATION =================
function changePage(page) {
    currentPage = page;
    loadBankModal();
}

function renderBankPagination(totalPage, currentPage) {
    const pagination = document.getElementById("bankPagination");

    let html = `<nav><ul class="pagination">`;

    // PREVIOUS
    html += `
        <li class="page-item ${currentPage === 0 ? 'disabled' : ''}">
            <button class="page-link" onclick="changePage(${currentPage - 1})">
                <i class="bi bi-chevron-left"></i>
            </button>
        </li>
    `;

    // PAGE NUMBERS
    for (let i = 0; i < totalPage; i++) {
        html += `
            <li class="page-item ${currentPage === i ? 'active' : ''}">
                <button class="page-link" onclick="changePage(${i})">${i + 1}</button>
            </li>
        `;
    }

    if (totalPage === 0) {
        html += `
            <li class="page-item active">
                <button class="page-link">1</button>
            </li>
        `;
    }

    // NEXT
    html += `
        <li class="page-item ${currentPage >= totalPage - 1 ? 'disabled' : ''}">
            <button class="page-link" onclick="changePage(${currentPage + 1})">
                <i class="bi bi-chevron-right"></i>
            </button>
        </li>
    `;

    html += `</ul></nav>`;
    pagination.innerHTML = html;
}

// ================= CATEGORY =================
async function loadParents() {
    const res = await fetch(`${API}/category/parents`);
    const result = await res.json();

    parentCategory.innerHTML = `
        <option value="">Chọn</option>
        ${(result.data || [])
        .map(c => `<option value="${c.id}">${c.name}</option>`)
        .join("")}
    `;
}

parentCategory.onchange = async () => {
    const id = parentCategory.value;

    if (!id) {
        childCategory.disabled = true;
        childCategory.innerHTML = `<option value="">Chọn</option>`;
        return;
    }

    await loadChildCategories(id);
};

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

// ================= SHUFFLE QUESTIONS =================
document.getElementById("btnShuffleQuestions").onclick = () => {
    if (!questions.length) return;

    for (let i = questions.length - 1; i > 0; i--) {
        const j = Math.floor(Math.random() * (i + 1));
        [questions[i], questions[j]] = [questions[j], questions[i]];
    }

    renderQuestions();
};