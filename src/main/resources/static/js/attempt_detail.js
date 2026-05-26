document.addEventListener('DOMContentLoaded', () => {
    fetchDetail();
});

function fetchDetail() {
    const parts = window.location.pathname.split('/');
    const attemptId = parts[parts.length - 1];

    if (!attemptId || attemptId === 'history') return;

    fetch(`/api/attempts/detail/${attemptId}`)
        .then(res => res.json())
        .then(data => {
            if (data && data.data) {
                const detail = data.data;
                document.getElementById('detailExamTitle').innerText = detail.examTitle || 'Bài thi';
                document.getElementById('detailScore').innerText = detail.score !== null ? detail.score : '-';
                document.getElementById('detailMaxScore').innerText = 10; // Thang điểm 10
                
                const statusBadge = detail.status === 'COMPLETED' ? '<span class="badge-status badge-completed">Hoàn thành</span>' : '<span class="badge-status badge-progress">Đang làm</span>';
                document.getElementById('detailStatus').innerHTML = statusBadge;

                const duration = detail.durationSeconds ? `${Math.floor(detail.durationSeconds/60)}p ${detail.durationSeconds%60}s` : '-';
                document.getElementById('detailDuration').innerText = duration;

                renderQuestions(detail.answers);
            }
        })
        .catch(err => console.error("Error fetching detail:", err));
}

function renderQuestions(answers) {
    const container = document.getElementById('questionsContainer');
    container.innerHTML = '';

    if (!answers || answers.length === 0) {
        container.innerHTML = '<p class="text-muted">Không có thông tin chi tiết các câu trả lời.</p>';
        return;
    }

    answers.forEach((ans, index) => {
        const questionCard = document.createElement('div');
        questionCard.className = 'card mb-4 border-0 shadow-sm';
        questionCard.style.borderRadius = '12px';

        const isCorrectQuestion = (ans.correct !== undefined ? ans.correct : ans.isCorrect);
        const badgeClass = isCorrectQuestion ? 'bg-success' : 'bg-danger';
        const badgeText = isCorrectQuestion ? 'Đúng' : 'Sai';
        
        let optionsHtml = '';
        if (ans.options && ans.options.length > 0) {
            optionsHtml = '<div class="list-group list-group-flush mt-3">';
            ans.options.forEach(opt => {
                const isSelected = isAnswerSelected(opt.key, ans);
                const isCorrectOpt = isAnswerCorrect(opt.key, ans);
                
                let optStyle = 'border-radius: 8px; margin-bottom: 8px; padding: 14px 18px; border: 1px solid #e9ecef; transition: background-color 0.2s;';
                let iconHtml = '';

                if (isSelected) {
                    if (isCorrectOpt) {
                        optStyle += ' background-color: #e0f8e9; border-color: #198754; color: #198754; font-weight: 600;';
                        iconHtml = '<span class="badge bg-success ms-2">✓ Bạn chọn đúng</span>';
                    } else {
                        optStyle += ' background-color: #f8d7da; border-color: #dc3545; color: #dc3545; font-weight: 600;';
                        iconHtml = '<span class="badge bg-danger ms-2">✗ Bạn chọn sai</span>';
                    }
                } else if (isCorrectOpt) {
                    // Correct option that user didn't choose
                    optStyle += ' background-color: #f0f4ff; border-color: #0d6efd; border-style: dashed; color: #0d6efd; font-weight: 600;';
                    iconHtml = '<span class="badge bg-primary ms-2">★ Đáp án đúng</span>';
                }

                optionsHtml += `
                    <div class="list-group-item d-flex justify-content-between align-items-center" style="${optStyle}">
                        <div>
                            <span class="fw-bold me-2">${opt.key}.</span>
                            <span>${opt.text}</span>
                        </div>
                        <div>${iconHtml}</div>
                    </div>
                `;
            });
            optionsHtml += '</div>';
        }

        questionCard.innerHTML = `
            <div class="card-body p-4">
                <div class="d-flex justify-content-between align-items-center border-bottom pb-2 mb-3">
                    <span class="fw-bold text-muted">Câu hỏi ${index + 1}</span>
                    <span class="badge ${badgeClass} px-3 py-2" style="border-radius: 20px; font-size: 0.85rem;">${badgeText}</span>
                </div>
                <div class="fw-bold text-dark fs-5 mb-2">${ans.content || 'Câu hỏi không có nội dung'}</div>
                ${optionsHtml}
            </div>
        `;
        container.appendChild(questionCard);
    });
}

function isAnswerSelected(key, ans) {
    if (ans.selectedAnswer) {
        return ans.selectedAnswer.toUpperCase() === key.toUpperCase();
    }
    if (ans.selectedKeys) {
        return ans.selectedKeys.some(k => k.toUpperCase() === key.toUpperCase());
    }
    return false;
}

function isAnswerCorrect(key, ans) {
    if (ans.correctAnswer) {
        return ans.correctAnswer.toUpperCase() === key.toUpperCase();
    }
    if (ans.correctAnswerKeys) {
        return ans.correctAnswerKeys.some(k => k.toUpperCase() === key.toUpperCase());
    }
    return false;
}
