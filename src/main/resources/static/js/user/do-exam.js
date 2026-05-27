/**
 * exam-taking.js
 * Trang làm bài thi
 */

const BASE_URL = "http://localhost:8080";

// ─── State ─────────────────────────────────────────────────────────────────

const state = {
    attemptId: null,
    examId: getExamIdFromUrl(),
    questions: [],
    answers: {},
    timeLimit: null,
    timerInterval: null,
    secondsLeft: 0,
};

// ─── Bootstrap Modal ────────────────────────────────────────────────────────

let submitModal;

// ─── Init ───────────────────────────────────────────────────────────────────

document.addEventListener("DOMContentLoaded", init);

async function init() {

    try {

        // Init modal
        submitModal = new bootstrap.Modal(
            document.getElementById("submitModal")
        );

        // Validate examId
        if (!state.examId) {
            throw new Error("Không tìm thấy ID đề thi trong URL.");
        }

        // 1. Start attempt
        await startAttempt();

        // 2. Load questions
        await loadQuestions();

        // 3. Render UI
        renderExam();

        // 4. Start timer
        if (state.timeLimit > 0) {

            startTimer(state.timeLimit * 60);

        } else {

            document.getElementById("timer-display").textContent = "∞";
        }

        // Show exam screen
        document.getElementById("loading-screen").style.display = "none";

        document.getElementById("exam-screen").style.display = "block";

    } catch (err) {

        showError(err.message || "Không thể tải đề thi.");
    }
}

// ─── API Calls ─────────────────────────────────────────────────────────────

async function startAttempt() {

    const res = await fetch(
        `${BASE_URL}/api/attempts/start/${state.examId}`,
        {
            method: "POST",
            credentials: "include",
        }
    );

    if (!res.ok) {

        const body = await res.json().catch(() => ({}));

        throw new Error(
            body.message || `Lỗi khi bắt đầu làm bài (${res.status})`
        );
    }

    const result = await res.json();

    const data = result.data || result;

    state.attemptId = data.attemptId;

    state.timeLimit = data.timeLimit || 0;

    if (!state.attemptId) {
        throw new Error("Server không trả về attemptId.");
    }
}

async function loadQuestions() {

    const res = await fetch(
        `${BASE_URL}/api/attempts/${state.attemptId}/questions`,
        {
            credentials: "include",
        }
    );

    if (!res.ok) {

        const body = await res.json().catch(() => ({}));

        throw new Error(
            body.message || `Lỗi khi tải câu hỏi (${res.status})`
        );
    }

    const result = await res.json();

    state.questions = Array.isArray(result)
        ? result
        : (result.data || []);

    if (state.questions.length === 0) {
        throw new Error("Đề thi không có câu hỏi.");
    }
}

// ─── Render ────────────────────────────────────────────────────────────────

function renderExam() {

    renderQuestions();

    renderSidebar();

    updateProgress();
}

function renderQuestions() {

    const container = document.getElementById("questions-container");
    container.innerHTML = state.questions.map((q, index) => {

        const isMulti = q.type === "MULTIPLE_CHOICE";

        const optionsHtml = (q.options || []).map(opt => `
            <div class="option-item ${isMulti ? "multi" : ""}" data-qid="${q.questionId}" data-key="${opt.key}"
                 onclick="selectAnswer('${q.questionId}', '${opt.key}', ${isMulti})">
                <span class="option-key">${opt.key}</span>
                <span class="option-text">${opt.text}</span>
            </div>
        `).join("");

        return `
            <div class="question-card"
                 id="q-card-${q.questionId}"
                 data-index="${index}">

                <div class="question-badges">${q.level ? `<span class="badge-level">${q.level}</span>` : ""}
                    ${q.type ? `<span class="badge-type">${formatType(q.type)}</span>` : ""}
                </div>

                <div class="question-number">Câu ${index + 1}</div>

                <div class="question-content">${q.content}</div>

                <div class="options-list"id="opts-${q.questionId}">${optionsHtml}</div>
            </div>
        `;

    }).join("");
}

function renderSidebar() {

    const grid =
        document.getElementById("question-grid");

    grid.innerHTML = state.questions.map((q, i) => `
        <button class="q-nav-btn" id="nav-btn-${q.questionId}"
                onclick="scrollToQuestion('${q.questionId}', ${i})"
                title="Câu ${i + 1}">
            ${i + 1}
        </button>
    `).join("");
}

// ─── Answer Selection ──────────────────────────────────────────────────────

function selectAnswer(questionId, key, isMulti) {

    if (isMulti) {

        if (!state.answers[questionId]) {
            state.answers[questionId] = [];
        }
        const arr = state.answers[questionId];
        const idx = arr.indexOf(key);

        if (idx === -1) {
            arr.push(key);
        } else {
            arr.splice(idx, 1);
        }

        if (arr.length === 0) {
            delete state.answers[questionId];
        }

    } else {
        state.answers[questionId] = key;
    }

    refreshOptionUI(questionId, isMulti);

    refreshCardState(questionId);

    refreshNavBtn(questionId);

    updateProgress();
}

function refreshOptionUI(questionId, isMulti) {

    const opts = document.querySelectorAll(`[data-qid="${questionId}"]`);

    const selected = state.answers[questionId];

    opts.forEach(el => {

        const key = el.dataset.key;

        let isSelected;

        if (isMulti) {
            isSelected = Array.isArray(selected) && selected.includes(key);
        } else {
            isSelected = selected === key;
        }

        el.classList.toggle("selected", isSelected);
    });
}

function refreshCardState(questionId) {

    const card = document.getElementById(`q-card-${questionId}`);

    if (!card) return;

    const hasAnswer = questionId in state.answers;

    card.classList.toggle("answered", hasAnswer);
}

function refreshNavBtn(questionId) {

    const btn =
        document.getElementById(`nav-btn-${questionId}`);

    if (!btn) return;

    const hasAnswer = questionId in state.answers;

    btn.classList.toggle("answered", hasAnswer);
}

// ─── Scroll ────────────────────────────────────────────────────────────────

function scrollToQuestion(questionId) {

    document
        .querySelectorAll(".q-nav-btn")
        .forEach(b => b.classList.remove("current"));

    const navBtn =
        document.getElementById(`nav-btn-${questionId}`);

    if (navBtn) {
        navBtn.classList.add("current");
    }

    const card =
        document.getElementById(`q-card-${questionId}`);

    if (card) {

        card.scrollIntoView({
            behavior: "smooth",
            block: "start"
        });
    }
}

// ─── Progress ──────────────────────────────────────────────────────────────

function updateProgress() {

    const total = state.questions.length;

    const answered =
        Object.keys(state.answers).length;

    document.getElementById("progress-text").textContent =
        `${answered} / ${total}`;
}

// ─── Timer ─────────────────────────────────────────────────────────────────

function startTimer(totalSeconds) {

    state.secondsLeft = totalSeconds;
    renderTimer();
    state.timerInterval = setInterval(() => {

        state.secondsLeft--;

        renderTimer();

        // Warning 5 phút
        if (state.secondsLeft <= 300) {
            document.getElementById("timer-box").classList.add("warning");
        }

        // Hết giờ
        if (state.secondsLeft <= 0) {
            clearInterval(state.timerInterval);
            autoSubmit();
        }

    }, 1000);
}

function renderTimer() {

    const hours = Math.floor(state.secondsLeft / 3600);

    const minutes = Math.floor((state.secondsLeft % 3600) / 60);

    const seconds = state.secondsLeft % 60;

    const h = String(hours).padStart(2, "0");

    const m = String(minutes).padStart(2, "0");

    const s = String(seconds).padStart(2, "0");

    document.getElementById("timer-display").textContent =
        `${h}:${m}:${s}`;
}

// ─── Submit ────────────────────────────────────────────────────────────────

function confirmSubmit() {

    const total = state.questions.length;

    const answered = Object.keys(state.answers).length;
    // Chưa làm hết
    if (answered < total) {
        const unanswered = total - answered;

        alert(`Bạn còn ${unanswered} câu chưa trả lời. ` +
            `Vui lòng hoàn thành tất cả câu hỏi trước khi nộp bài.`
        );
        return;
    }

    // Đã làm hết
    const summary = `Bạn đã trả lời <strong>${answered}/${total}</strong> câu hỏi.`;

    document.getElementById("submit-summary").innerHTML = summary;

    submitModal.show();
}

async function submitExam() {

    submitModal.hide();
    clearInterval(state.timerInterval);

    try {
        const answers = [];

        // Duyệt TẤT CẢ câu, không chỉ câu đã làm
        state.questions.forEach(q => {
            const answer = state.answers[q.questionId];
            const isMulti = q.type === "MULTIPLE_CHOICE";

            if (isMulti) {
                answers.push({
                    examQuestionId: q.questionId,
                    selectedKeys: Array.isArray(answer) ? answer : []
                });
            } else {
                answers.push({
                    examQuestionId: q.questionId,
                    selectedAnswer: answer ?? null  // null = chưa làm → server chấm sai
                });
            }
        });

        const payload = { answers };

        const res = await fetch(
            `${BASE_URL}/api/attempts/${state.attemptId}/submit`,
            {
                method: "POST",
                credentials: "include",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(payload)
            }
        );

        if (!res.ok) {
            const body = await res.json().catch(() => ({}));
            throw new Error(body.message || `Lỗi khi nộp bài (${res.status})`);
        }

        const result = await res.json();
        const data = result.data || result;

        data.wrongCount = data.totalQuestions - data.correctCount;
        data.correctPercentage = Math.round(
            (data.correctCount / data.totalQuestions) * 100
        );

        sessionStorage.setItem("examResult", JSON.stringify(data));
        window.location.href = "/user/result";

    } catch (err) {
        alert("Có lỗi khi nộp bài: " + err.message);
    }
}

function autoSubmit() {

    document.getElementById("submit-summary").innerHTML =
        "Hết giờ! Bài làm sẽ được nộp tự động.";

    submitModal.show();

    setTimeout(() => {
        submitExam();
    }, 2500);
}

// ─── Helpers ───────────────────────────────────────────────────────────────

function getExamIdFromUrl() {
    return window.location.pathname.split("/").filter(Boolean).pop();
}

function formatType(type) {

    const map = {
        SINGLE_CHOICE: "Một đáp án",
        MULTIPLE_CHOICE: "Nhiều đáp án",
        TRUE_FALSE: "Đúng / Sai",
    };
    return map[type] || type;
}

function showError(message) {
    document.getElementById("loading-screen").style.display = "none";

    document.getElementById("error-message").textContent = message;

    document.getElementById("error-screen").style.display = "flex";
}