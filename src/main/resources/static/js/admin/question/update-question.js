const API = "http://localhost:8080/api";

const parentCategory = document.getElementById("parentCategory");
const childCategory = document.getElementById("childCategory");
const optionsBox = document.getElementById("optionsBox");

// Lấy ID từ URL: /admin/questions/update/{id}
const questionId = window.location.pathname.split("/").pop();

// ================= INIT =================
document.addEventListener("DOMContentLoaded", async () => {
    await loadParents();

    document.getElementById("btnAddOption").onclick = addOption;
    document.getElementById("btnSave").onclick = submitUpdate;
    document.getElementById("questionType").addEventListener("change", onTypeChange);

    await loadQuestion();
});

// ================= LOAD CHILD CATEGORY (reuse) =================
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

// ================= LOAD QUESTION =================
async function loadQuestion() {
    const res = await fetch(`${API}/question-bank/detail/${questionId}`);
    const result = await res.json();
    const q = result.data;

    // Basic fields
    document.getElementById("questionContent").value = q.content || "";
    document.getElementById("questionType").value = q.type || "";
    document.getElementById("questionLevel").value = q.level || "";

    // Category
    parentCategory.value = q.parentCategoryId;
    await loadChildCategories(q.parentCategoryId, q.categoryId);

    // Options
    renderOptionsFromData(q);
}

// ================= CATEGORY =================
async function loadParents() {
    const res = await fetch(`${API}/category/parents`);
    const result = await res.json();
    parentCategory.innerHTML = `<option value="">Chọn</option>`;
    (result.data || []).forEach(c => {
        parentCategory.innerHTML += `<option value="${c.id}">${c.name}</option>`;
    });
}

async function loadChildren(parentId) {
    const res = await fetch(`${API}/category/children/${parentId}`);
    const result = await res.json();
    childCategory.innerHTML = `<option value="">Chọn</option>`;
    childCategory.disabled = false;
    (result.data || []).forEach(c => {
        childCategory.innerHTML += `<option value="${c.id}">${c.name}</option>`;
    });
}

parentCategory.onchange = async () => {
    const id = parentCategory.value;
    childCategory.innerHTML = `<option value="">Chọn</option>`;
    if (!id) {
        childCategory.disabled = true;
        return;
    }
    await loadChildren(id);
};

// ================= RENDER OPTIONS FROM DATA =================
function renderOptionsFromData(q) {
    optionsBox.innerHTML = "";

    const correctKeys = q.correctAnswerKeys || [];
    const correctAnswer = q.correctAnswer || "";

    (q.options || []).forEach(opt => {
        let isCorrect = false;
        if (q.type === "MULTIPLE_CHOICE") {
            isCorrect = correctKeys.includes(opt.key);
        } else {
            isCorrect = opt.key === correctAnswer;
        }
        addOptionRow(opt.key, opt.text, q.type === "TRUE_FALSE", isCorrect, q.type);
    });
}

// ================= ON TYPE CHANGE =================
function onTypeChange() {
    const type = document.getElementById("questionType").value;
    optionsBox.innerHTML = "";

    if (type === "TRUE_FALSE") {
        addOptionRow("TRUE", "Đúng", true, false, type);
        addOptionRow("FALSE", "Sai", true, false, type);
    } else {
        ["A", "B", "C", "D"].forEach(key => addOptionRow(key, "", false, false, type));
    }
}

// ================= ADD OPTION =================
function addOption() {
    const type = document.getElementById("questionType").value;
    addOptionRow("", "", false, false, type);
}

function addOptionRow(keyVal, textVal, readonly, isCorrect, type) {
    const inputType = type === "MULTIPLE_CHOICE" ? "checkbox" : "radio";
    const inputName = type === "MULTIPLE_CHOICE" ? "multi" : "single";
    const readonlyAttr = readonly ? "readonly" : "";
    const checkedAttr = isCorrect ? "checked" : "";

    const div = document.createElement("div");
    div.className = "row option-row align-items-center mb-2";
    div.innerHTML = `
        <div class="col-md-2">
            <input class="form-control opt-key" placeholder="A" value="${keyVal}" ${readonlyAttr}>
        </div>
        <div class="col-md-7">
            <input class="form-control opt-text" placeholder="Đáp án" value="${textVal}" ${readonlyAttr}>
        </div>
        <div class="col-md-1 text-center">
            <input type="${inputType}" class="form-check-input opt-answer" value="${keyVal}" name="${inputName}" style="width:20px;height:20px;" ${checkedAttr}>
        </div>
        <div class="col-md-2">
            ${!readonly
        ? `<button class="btn btn-danger w-100" onclick="this.closest('.option-row').remove()">Xóa</button>`
        : `<div></div>`
    }
        </div>
    `;

    // Sync key input với radio/checkbox value
    const keyInput = div.querySelector(".opt-key");
    const answerInput = div.querySelector(".opt-answer");
    keyInput.addEventListener("input", () => {
        answerInput.value = keyInput.value;
    });

    optionsBox.appendChild(div);
}

// ================= SUBMIT UPDATE =================
async function submitUpdate() {
    const type = document.getElementById("questionType").value;
    const level = document.getElementById("questionLevel").value;
    const content = document.getElementById("questionContent").value;
    const categoryId = childCategory.value;

    const options = [];
    document.querySelectorAll(".option-row").forEach(r => {
        options.push({
            key: r.querySelector(".opt-key").value,
            text: r.querySelector(".opt-text").value
        });
    });

    let correctAnswer = null;
    let correctAnswerKeys = [];

    if (type === "MULTIPLE_CHOICE") {
        document.querySelectorAll("input[name='multi']:checked")
            .forEach(i => correctAnswerKeys.push(i.value));
    } else {
        correctAnswer = document.querySelector("input[name='single']:checked")?.value;
    }

    const payload = { content, categoryId, type, level, options, correctAnswer, correctAnswerKeys };

    const res = await fetch(`${API}/question-bank/update/${questionId}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload)
    });

    if (!res.ok) {
        alert("Lỗi cập nhật câu hỏi");
        return;
    }

    alert("Cập nhật thành công!");
    window.location.href = "/admin/questions";
}