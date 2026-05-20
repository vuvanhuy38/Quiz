const API = "http://localhost:8080/api";

// ELEMENT
const parentCategory =
    document.getElementById("parentCategory");

const childCategory =
    document.getElementById("childCategory");

const titleInput =
    document.getElementById("title");

const descriptionInput =
    document.getElementById("description");

const timeLimitInput =
    document.getElementById("timeLimit");

const statusInput =
    document.getElementById("status");

const saveExamBtn =
    document.getElementById("saveExamBtn");

const examIdInput =
    document.getElementById("examId");

const tabInfo =
    document.getElementById("tabInfo");

const tabQuestion =
    document.getElementById("tabQuestion");

const infoSection =
    document.getElementById("infoSection");

const questionSection =
    document.getElementById("questionSection");

const questionList =
    document.getElementById("questionList");

const btnAddManual =
    document.getElementById("btnAddManual");

const btnImportBank =
    document.getElementById("btnImportBank");

const btnSaveQuestions =
    document.getElementById("btnSaveQuestions");

const btnSaveManualQuestion =
    document.getElementById("btnSaveManualQuestion");

const questionType =
    document.getElementById("questionType");

const optionsContainer =
    document.getElementById("optionsContainer");

const btnAddOption =
    document.getElementById("btnAddOption");

const addQuestionModal =
    new bootstrap.Modal(
        document.getElementById("addQuestionModal")
    );

let questions = [];

let editingIndex = null;

// DETECT MODE
function getExamIdFromUrl() {

    const parts =
        window.location.pathname
            .split("/")
            .filter(Boolean);

    return window.location.pathname.includes("/update/")
        ? parts[parts.length - 1]
        : null;
}

const urlId = getExamIdFromUrl();

const IS_UPDATE = !!urlId;

// INIT
document.addEventListener(
    "DOMContentLoaded",
    async () => {

        await loadParents();

        if (IS_UPDATE) {

            examIdInput.value = urlId;

            tabQuestion.classList.remove("disabled");

            await loadExam(urlId);

            showQuestionTab();

        } else {

            showInfoTab();
        }
    }
);

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

// CLICK TAB
tabInfo.onclick = () => {

    showInfoTab();
};

tabQuestion.onclick = () => {

    if (
        tabQuestion.classList.contains("disabled")
    ) {
        return;
    }

    showQuestionTab();
};

// LOAD EXAM
async function loadExam(id) {

    const res =
        await fetch(`${API}/exams/detail/${id}`);

    const result =
        await res.json();

    const data =
        result.data;

    titleInput.value =
        data.title || "";

    descriptionInput.value =
        data.description || "";

    timeLimitInput.value =
        data.timeLimit || "";

    statusInput.value =
        data.status || "";

    // CATEGORY
    if (data.parentCategoryId) {

        // set category cha
        parentCategory.value =
            data.parentCategoryId;

        // load category con
        const childRes =
            await fetch(
                `${API}/category/children/${data.parentCategoryId}`
            );

        const childResult =
            await childRes.json();

        childCategory.disabled = false;

        childCategory.innerHTML = `
            <option value="">
                Chọn
            </option>
        `;

        (childResult.data || []).forEach(c => {

            childCategory.innerHTML += `
                <option value="${c.id}">
                    ${c.name}
                </option>
            `;
        });

        // set category con
        childCategory.value =
            data.categoryId || "";
    }

    questions =
        data.questions || [];

    document.getElementById(
        "questionCount"
    ).innerText =
        questions.length;

    renderQuestions();
}
// RENDER QUESTIONS
function renderQuestions() {

    if (!questions.length) {

        questionList.innerHTML = `
            <div class="alert alert-secondary">
                Chưa có câu hỏi
            </div>
        `;

        return;
    }

    questionList.innerHTML =
        questions.map((q, index) => {

            return `
                <div class="card mb-3">

                    <div class="card-body">

                        <div class="d-flex justify-content-between">

                            <div>

                                <h5>
                                    Câu ${index + 1}
                                </h5>

                                <span class="badge bg-dark">
                                    ${q.type}
                                </span>

                                <span class="badge bg-secondary">
                                    ${q.level}
                                </span>

                            </div>

                            <div class="d-flex gap-2 align-items-center">

                                <button class="btn btn-warning btn-sm"
                                        onclick="editQuestion(${index})">

                                    Sửa

                                </button>

                                <button class="btn btn-danger btn-sm"
                                        onclick="deleteQuestion(${index})">

                                    Xóa

                                </button>

                            </div>

                        </div>

                        <p class="fw-bold mt-3">
                            ${q.content}
                        </p>

                        ${q.options.map(o => {

                const isCorrect =
                    q.correctAnswer === o.key ||
                    (q.correctAnswerKeys || [])
                        .includes(o.key);

                return `
                                <div class="border rounded p-2 mb-2
                                    ${isCorrect
                    ? 'border-success bg-light'
                    : ''}">

                                    <strong>${o.key}.</strong>

                                    ${o.text}

                                </div>
                            `;
            }).join("")}

                    </div>

                </div>
            `;
        }).join("");
}

// CREATE EXAM
async function createExam() {

    const payload = {

        title:
            titleInput.value.trim(),

        categoryId:
            childCategory.value || null,

        description:
            descriptionInput.value.trim(),

        timeLimit:
            Number(timeLimitInput.value),

        status:
        statusInput.value
    }

    const res = await fetch(
        `${API}/exams/create`,
        {
            method: "POST",

            headers: {
                "Content-Type":
                    "application/json"
            },

            body:
                JSON.stringify(payload)
        }
    );

    const result = await res.json();

    const id = result.data;

    window.location.href = `/admin/exams/update/${id}`;
}

// UPDATE EXAM
async function updateExam() {

    const id = examIdInput.value;

    const payload = {

        title:
            titleInput.value.trim(),

        categoryId:
            childCategory.value || null,

        description:
            descriptionInput.value.trim(),

        timeLimit:
            Number(timeLimitInput.value),

        status:
        statusInput.value
    };

    await fetch(`${API}/exams/${id}`, {

        method: "PUT",

        headers: {
            "Content-Type":
                "application/json"
        },

        body:
            JSON.stringify(payload)
    });

    alert("Cập nhật thành công");
}

// SAVE EXAM
saveExamBtn.onclick = async () => {

    if (!IS_UPDATE) {

        await createExam();

    } else {

        await updateExam();
    }
};

// SAVE QUESTIONS
btnSaveQuestions.onclick = async () => {

    const examId = examIdInput.value;

    const payload = questions.map(q => ({

        id: q.id || null,

        content: q.content,

        type: q.type,

        options: q.options,

        correctAnswer:
            q.correctAnswer || null,

        correctAnswerKeys:
            q.correctAnswerKeys || [],

        level: q.level
    }));

    try {

        const res = await fetch(
            `${API}/exam-question/create/${examId}`,
            {
                method: "POST",

                headers: {
                    "Content-Type":
                        "application/json"
                },

                body:
                    JSON.stringify(payload)
            }
        );

        const result = await res.json();

        alert(
            result.message ||
            "Lưu câu hỏi thành công"
        );

        await loadExam(examId);

    } catch (e) {

        alert("Lưu thất bại");
    }
};

// OPTION ROW
function createOptionRow(
    key = "",
    type = "SINGLE_CHOICE",
    checked = false,
    text = ""
) {

    const inputType =
        type === "MULTIPLE_CHOICE"
            ? "checkbox"
            : "radio";

    return `
        <div class="row mb-2 option-item">

            <div class="col-2">

                <input type="text"
                       class="form-control option-key"
                       value="${key}">

            </div>

            <div class="col-7">

                <input type="text"
                       class="form-control option-text"
                       value="${text}"
                       placeholder="Đáp án">

            </div>

            <div class="col-1 d-flex align-items-center">

                <input type="${inputType}"
                       class="form-check-input option-correct"
                       name="correctAnswer"
                       ${checked ? "checked" : ""}>

            </div>

            <div class="col-2">

                <button type="button"
                        class="btn btn-danger btn-sm btn-remove-option">

                    Xóa

                </button>

            </div>

        </div>
    `;
}

// RENDER OPTION INPUT
function renderOptionInputs(type) {

    if (type === "TRUE_FALSE") {

        btnAddOption.style.display = "none";

        optionsContainer.innerHTML = `
            ${createOptionRow(
            "TRUE",
            type,
            false,
            "Đúng"
        )}

            ${createOptionRow(
            "FALSE",
            type,
            false,
            "Sai"
        )}
        `;

        return;
    }

    btnAddOption.style.display = "inline-block";

    optionsContainer.innerHTML = `
        ${createOptionRow("A", type)}
        ${createOptionRow("B", type)}
        ${createOptionRow("C", type)}
        ${createOptionRow("D", type)}
    `;
}

// CHANGE TYPE
questionType.onchange = () => {

    renderOptionInputs(
        questionType.value
    );
};

// OPEN MODAL
btnAddManual.onclick = () => {

    editingIndex = null;

    document.getElementById(
        "questionContent"
    ).value = "";

    document.getElementById(
        "questionLevel"
    ).value = "EASY";

    document.getElementById(
        "questionType"
    ).value = "SINGLE_CHOICE";

    renderOptionInputs(
        "SINGLE_CHOICE"
    );

    addQuestionModal.show();
};

// ADD OPTION
btnAddOption.onclick = () => {

    const type =
        questionType.value;

    const count =
        document.querySelectorAll(
            ".option-item"
        ).length;

    const nextKey =
        String.fromCharCode(65 + count);

    optionsContainer.insertAdjacentHTML(
        "beforeend",
        createOptionRow(nextKey, type)
    );
};

// REMOVE OPTION
optionsContainer.addEventListener(
    "click",
    e => {

        if (
            e.target.classList.contains(
                "btn-remove-option"
            )
        ) {

            e.target
                .closest(".option-item")
                .remove();
        }
    }
);

// SAVE QUESTION
btnSaveManualQuestion.onclick = () => {

    const content =
        document.getElementById(
            "questionContent"
        ).value.trim();

    const type =
        questionType.value;

    const level =
        document.getElementById(
            "questionLevel"
        ).value;

    if (!content) {

        alert("Nhập nội dung câu hỏi");

        return;
    }

    const optionItems =
        document.querySelectorAll(
            ".option-item"
        );

    const options = [];

    let correctAnswer = null;

    let correctAnswerKeys = [];

    optionItems.forEach(item => {

        const key =
            item.querySelector(
                ".option-key"
            ).value;

        const text =
            item.querySelector(
                ".option-text"
            ).value;

        const checked =
            item.querySelector(
                ".option-correct"
            ).checked;

        options.push({
            key,
            text
        });

        if (
            (
                type === "SINGLE_CHOICE" ||
                type === "TRUE_FALSE"
            ) &&
            checked
        ) {

            correctAnswer = key;
        }

        if (
            type === "MULTIPLE_CHOICE" &&
            checked
        ) {

            correctAnswerKeys.push(key);
        }
    });

    const questionData = {

        ...(editingIndex !== null &&
        questions[editingIndex]?.id
            ? {
                id:
                questions[editingIndex].id
            }
            : {}),

        content,

        type,

        options,

        correctAnswer,

        correctAnswerKeys,

        level
    };

    if (editingIndex !== null) {

        questions[editingIndex] =
            questionData;

    } else {

        questions.push(questionData);
    }

    document.getElementById(
        "questionCount"
    ).innerText = questions.length;

    renderQuestions();

    addQuestionModal.hide();

    editingIndex = null;
};

// DELETE QUESTION
async function deleteQuestion(index) {

    const q = questions[index];

    if (!confirm("Xóa câu hỏi này?")) {
        return;
    }

    if (q.id) {

        try {

            const res = await fetch(
                `${API}/exam-question/delete/${q.id}`,
                {
                    method: "DELETE"
                }
            );

            const result =
                await res.json();

            alert(
                result.message ||
                "Xóa thành công"
            );

        } catch (e) {

            alert("Xóa thất bại");

            return;
        }
    }

    questions.splice(index, 1);

    document.getElementById(
        "questionCount"
    ).innerText = questions.length;

    renderQuestions();
}

// EDIT QUESTION
function editQuestion(index) {

    const q = questions[index];

    editingIndex = index;

    document.getElementById(
        "questionContent"
    ).value = q.content;

    questionType.value = q.type;

    document.getElementById(
        "questionLevel"
    ).value = q.level;

    optionsContainer.innerHTML = "";

    q.options.forEach(o => {

        const checked =
            q.correctAnswer === o.key ||
            (q.correctAnswerKeys || [])
                .includes(o.key);

        optionsContainer.innerHTML +=
            createOptionRow(
                o.key,
                q.type,
                checked,
                o.text
            );
    });

    if (q.type === "TRUE_FALSE") {

        btnAddOption.style.display =
            "none";

    } else {

        btnAddOption.style.display =
            "inline-block";
    }

    addQuestionModal.show();
}

// IMPORT BANK
btnImportBank.onclick = async () => {

    const idsText = prompt(
        "Nhập question bank ids cách nhau dấu phẩy"
    );

    if (!idsText) {
        return;
    }

    const ids =
        idsText
            .split(",")
            .map(x => x.trim())
            .filter(Boolean);

    const res = await fetch(
        `${API}/exam-question/preview`,
        {
            method: "POST",

            headers: {
                "Content-Type":
                    "application/json"
            },

            body: JSON.stringify({
                questionBankIds: ids
            })
        }
    );

    const result = await res.json();

    questions = [
        ...questions,
        ...(result.data || [])
    ];

    document.getElementById(
        "questionCount"
    ).innerText = questions.length;

    renderQuestions();
};

// CATEGORY
async function loadParents() {

    const res =
        await fetch(
            `${API}/category/parents`
        );

    const result =
        await res.json();

    parentCategory.innerHTML = `
        <option value="">
            Chọn
        </option>
    `;

    (result.data || []).forEach(c => {

        parentCategory.innerHTML += `
            <option value="${c.id}">
                ${c.name}
            </option>
        `;
    });
}

parentCategory.onchange = async () => {

    const id =
        parentCategory.value;

    if (!id) {

        childCategory.disabled = true;

        childCategory.innerHTML = `
            <option value="">
                Chọn
            </option>
        `;

        return;
    }

    const res =
        await fetch(
            `${API}/category/children/${id}`
        );

    const result = await res.json();

    childCategory.disabled = false;

    childCategory.innerHTML = `
        <option value="">
            Chọn
        </option>
    `;

    (result.data || []).forEach(c => {

        childCategory.innerHTML += `
            <option value="${c.id}">
                ${c.name}
            </option>
        `;
    });
};