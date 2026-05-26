const API = "http://localhost:8080/api";

const parentCategory = document.getElementById("parentCategory");
const childCategory = document.getElementById("childCategory");
const optionsBox = document.getElementById("optionsBox");

// ================= INIT =================
document.addEventListener("DOMContentLoaded", () => {
    loadParents();
    document.getElementById("btnAddOption").onclick = addOption;
    document.getElementById("btnSave").onclick = submitQuestion;
    document.getElementById("questionType").addEventListener("change", renderUI);
    renderUI();
});

// ================= CATEGORY =================
async function loadParents() {
    const res = await fetch(`${API}/category/parents`);
    const result = await res.json();
    parentCategory.innerHTML = `<option value="">Chọn</option>`;
    (result.data || []).forEach(c => {
        parentCategory.innerHTML += `<option value="${c.id}">${c.name}</option>`;
    });
}

parentCategory.onchange = async () => {
    const id = parentCategory.value;
    childCategory.innerHTML = `<option value="">Chọn</option>`;
    if (!id) {
        childCategory.disabled = true;
        return;
    }
    const res = await fetch(`${API}/category/children/${id}`);
    const result = await res.json();
    childCategory.disabled = false;
    (result.data || []).forEach(c => {
        childCategory.innerHTML += `<option value="${c.id}">${c.name}</option>`;
    });
};

// ================= RENDER UI =================
function renderUI() {
    const type = document.getElementById("questionType").value;
    optionsBox.innerHTML = "";

    if (type === "TRUE_FALSE") {
        addOptionRow("A", "Đúng", true);
        addOptionRow("B", "Sai", true);
    } else {
        // Mặc định 4 ô A, B, C, D
        ["A", "B", "C", "D"].forEach(key => addOptionRow(key, "", false));
    }
}

// ================= ADD OPTION =================
function addOption() {
    addOptionRow("", "", false);
}

function addOptionRow(keyVal, textVal, readonly) {
    const type = document.getElementById("questionType").value;
    const inputType = type === "MULTIPLE_CHOICE" ? "checkbox" : "radio";
    const inputName = type === "MULTIPLE_CHOICE" ? "multi" : "single";
    const readonlyAttr = readonly ? "readonly" : "";

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
            <input type="${inputType}" class="form-check-input opt-answer" value="${keyVal}" name="${inputName}" style="width:20px;height:20px;">
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

// ================= SUBMIT =================
async function submitQuestion() {
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

    const res = await fetch(`${API}/question-bank/create`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload)
    });

    if (!res.ok) {
        alert("Lỗi tạo câu hỏi");
        return;
    }

    alert("Tạo thành công!");

    window.location.href = `/admin/questions`;
}