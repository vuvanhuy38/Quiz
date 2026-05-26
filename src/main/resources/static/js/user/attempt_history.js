let currentPage = 0;
const pageSize = 10;
let chartInstance = null;

document.addEventListener('DOMContentLoaded', () => {
    fetchHistory();
});

function applyFilters() {
    currentPage = 0;
    fetchHistory();
}

async function fetchHistory() {
    const params = {
        page: currentPage,
        size: pageSize,
        examTitle: document.getElementById("filterExamTitle").value,
        startDateFrom: document.getElementById("filterStartDateFrom").value,
        startDateTo: document.getElementById("filterStartDateTo").value
    };

    const queryString = buildQuery(params);
    const url = `/api/attempts/history?${queryString}`;

    try {
        const res = await fetch(url);
        const data = await res.json();

        if (data && data.data) {
            renderTable(data.data);
            renderPagination(data.totalPage, data.pageIndex);

            if (currentPage === 0) {
                renderChart(data.data);
            }
        }
    } catch (err) {
        console.error("Error fetching history:", err);
    }
}

function renderTable(content) {
    const tbody = document.getElementById('historyTableBody');
    tbody.innerHTML = '';

    if (!content || content.length === 0) {
        tbody.innerHTML = '<tr><td colspan="6" class="text-center">Không có lịch sử làm bài</td></tr>';
        return;
    }

    content.forEach(attempt => {
        const tr = document.createElement('tr');
        
        const date = new Date(attempt.startedAt).toLocaleString('vi-VN');
        const statusBadge = attempt.status === 'COMPLETED' ? '<span class="badge-status badge-completed">Hoàn thành</span>' : '<span class="badge-status badge-progress">Đang làm</span>';
        const score = attempt.score !== null ? attempt.score : '-';
        const maxScore = 10;
        
        const duration = attempt.durationSeconds ? `${Math.floor(attempt.durationSeconds/60)}p ${attempt.durationSeconds%60}s` : '-';

        tr.innerHTML = `
            <td class="fw-bold">${attempt.examTitle || 'Bài thi'}</td>
            <td><span class="text-primary fw-bold">${score}</span> / ${maxScore}</td>
            <td>${statusBadge}</td>
            <td>${date}</td>
            <td>${duration}</td>
            <td><a href="/user/history/${attempt.attemptId}" class="btn btn-sm btn-outline-primary">Chi tiết</a></td>
        `;
        tbody.appendChild(tr);
    });
}

function renderPagination(totalPage, currentPage) {
    const pagination = document.getElementById('pagination');
    pagination.innerHTML = '';

    // Prev button
    pagination.innerHTML += `
        <li class="page-item ${currentPage === 0 ? 'disabled' : ''}">
            <button class="page-link" onclick="goToPage(${currentPage - 1})">‹</button>
        </li>
    `;

    // First page
    pagination.innerHTML += `
        <li class="page-item ${currentPage === 0 ? 'active' : ''}">
            <button class="page-link" onclick="goToPage(0)">1</button>
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
                    <button class="page-link" onclick="goToPage(${i})">${i + 1}</button>
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
                <button class="page-link" onclick="goToPage(${totalPage - 1})">${totalPage}</button>
            </li>
        `;
    }

    // Next button
    pagination.innerHTML += `
        <li class="page-item ${currentPage === totalPage - 1 ? 'disabled' : ''}">
            <button class="page-link" onclick="goToPage(${currentPage + 1})">›</button>
        </li>
    `;

    document.getElementById("tableInfo").innerText =
        `Trang ${currentPage + 1} / ${totalPage}`;
}

function goToPage(page) {
    currentPage = page;
    fetchHistory();
}

function renderChart(content) {
    if (!content || content.length === 0) return;
    
    // Reverse for chronological order
    const sortedContent = [...content].reverse();
    
    const labels = sortedContent.map(item => new Date(item.startedAt).toLocaleDateString('vi-VN'));
    const dataPoints = sortedContent.map(item => item.score || 0);

    const ctx = document.getElementById('trendChart').getContext('2d');
    
    if (chartInstance) {
        chartInstance.destroy();
    }
    
    chartInstance = new Chart(ctx, {
        type: 'line',
        data: {
            labels: labels,
            datasets: [{
                label: 'Điểm số',
                data: dataPoints,
                borderColor: '#0d6efd',
                backgroundColor: 'rgba(13, 110, 253, 0.1)',
                tension: 0.3,
                fill: true
            }]
        },
        options: {
            responsive: true,
            scales: {
                y: {
                    beginAtZero: true
                }
            }
        }
    });
}

function buildQuery(params) {
    const query = new URLSearchParams();

    for (const key in params) {
        if (params[key] !== null && params[key] !== "" && params[key] !== undefined) {
            query.append(key, params[key]);
        }
    }

    return query.toString();
}
