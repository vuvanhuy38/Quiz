const pathParts = window.location.pathname.split("/");
const examId = pathParts[pathParts.length - 1];

async function loadExamDetail() {

    try {

        const response = await fetch(`http://localhost:8080/api/exams/detail/${examId}`);

        const result = await response.json();

        const exam = result.data;

        let questionHtml = "";

        exam.questions.forEach((question, index) => {

            let optionHtml = "";

            question.options.forEach(option => {

                optionHtml += `
                    <div class="list-group-item">
                        <span class="fw-bold">${option.key}.</span>
                        <span>${option.text}</span>
                    </div>
                `;
            });

            questionHtml += `
                <div class="question-card">
            
                    <div class="d-flex justify-content-between align-items-start mb-3">
            
                        <h5 class="fw-bold mb-0">
                            Câu ${index + 1}: ${question.content}
                        </h5>
            
                        <div class="text-end">
            
                            <span class="badge bg-secondary me-1">
                                ${question.level}
                            </span>
          
                            <span class="badge bg-info">
                                ${question.type}
                            </span>
            
                        </div>
            
                    </div>
            
                    <div class="list-group mb-3">
                        ${optionHtml}
                    </div>
            
                </div>
            `;
        });

        document.getElementById("exam-detail").innerHTML = `
        
            <div class="card shadow-sm border-0 mb-4">

                <div class="card-body">

                    <h2 class="fw-bold mb-3">
                        ${exam.title}
                    </h2>

                    <p class="text-muted">
                        ${exam.description}
                    </p>

                    <div class="row mt-4">

                        <div class="col-md-4 mb-3">
                            <div class="info-box">
                                <h6>Thời gian</h6>
                                <span class="fw-bold">
                                    ${exam.timeLimit} phút
                                </span>
                            </div>
                        </div>

                        <div class="col-md-4 mb-3">
                            <div class="info-box">
                                <h6>Số câu hỏi</h6>
                                <span class="fw-bold">
                                    ${exam.totalQuestions}
                                </span>
                            </div>
                        </div>

                        <div class="col-md-4 mb-3">
                            <div class="info-box">
                                <h6>Tổng số lượt làm</h6>
                                <span class="fw-bold">
                                    ${exam.attemptCount}
                                </span>
                            </div>
                        </div>

                    </div>

                    <div class="mt-4">
                        <a href="/user/do-exam/${examId}" 
                           class="btn btn-primary">
                            Làm bài
                        </a>
                    </div>

                </div>

            </div>

            ${questionHtml}
        `;

    } catch (error) {

        console.error(error);

        document.getElementById("exam-detail").innerHTML = `
            <div class="alert alert-danger">
                Không thể tải chi tiết đề thi
            </div>
        `;
    }
}

loadExamDetail();