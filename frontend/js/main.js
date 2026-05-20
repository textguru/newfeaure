/* ==========================================================================
 * 지사별 시행종목 조회 - 메인 스크립트
 * REQ-001 ~ REQ-004 구현
 * ========================================================================== */

(function () {
    'use strict';

    // API Base URL - 개발 환경에서는 동일 호스트, 운영 시 게이트웨이 경로 조정
    const API_BASE = '/api/v1/exams';

    // ====== 화면 상태 ======
    const state = {
        selectedBranchCode: '',
        selectedBranchName: '',
        selectedExamId: null,
        selectedExamName: '',
        selectedExamDate: '',
        // 모달 내부 임시 선택값
        tempExamId: null,
        tempExamName: '',
        tempExamDate: '',
        // 페이징
        currentPage: 1,
        pageSize: 20
    };

    // ====== DOM 캐시 ======
    const $branchSelect = document.getElementById('branchSelect');
    const $examNameDisplay = document.getElementById('examNameDisplay');
    const $examIdHidden = document.getElementById('examIdHidden');
    const $examDateInput = document.getElementById('examDateInput');
    const $openExamModalBtn = document.getElementById('openExamModal');
    const $searchBtn = document.getElementById('searchBtn');
    const $excelDownloadBtn = document.getElementById('excelDownloadBtn');
    const $examModal = document.getElementById('examModal');
    const $closeExamModalBtn = document.getElementById('closeExamModal');
    const $cancelExamModalBtn = document.getElementById('cancelExamModal');
    const $confirmExamModalBtn = document.getElementById('confirmExamModal');
    const $examInfoTableBody = document.getElementById('examInfoTableBody');
    const $examItemTableBody = document.getElementById('examItemTableBody');
    const $pagination = document.getElementById('pagination');
    const $totalCount = document.getElementById('totalCount');
    const $resultCountBadge = document.getElementById('resultCountBadge');
    const $toast = document.getElementById('toast');

    // ====== 유틸 ======
    function showToast(message) {
        $toast.textContent = message;
        $toast.classList.add('show');
        clearTimeout(showToast._t);
        showToast._t = setTimeout(() => $toast.classList.remove('show'), 2500);
    }

    function escapeHtml(str) {
        if (str == null) return '';
        return String(str)
            .replace(/&/g, '&amp;')
            .replace(/</g, '&lt;')
            .replace(/>/g, '&gt;')
            .replace(/"/g, '&quot;')
            .replace(/'/g, '&#39;');
    }

    function formatExamDate(yyyymmdd) {
        if (!yyyymmdd || yyyymmdd.length !== 8) return yyyymmdd || '';
        return `${yyyymmdd.substring(0, 4)}-${yyyymmdd.substring(4, 6)}-${yyyymmdd.substring(6, 8)}`;
    }

    async function apiGet(url) {
        const res = await fetch(url, {
            method: 'GET',
            headers: { 'Accept': 'application/json' },
            credentials: 'same-origin'
        });
        if (!res.ok) {
            let message = '요청 처리 중 오류가 발생했습니다.';
            try {
                const body = await res.json();
                if (body && body.message) message = body.message;
            } catch (_) { /* ignore */ }
            const err = new Error(message);
            err.status = res.status;
            throw err;
        }
        return res.json();
    }

    // ====== REQ-001 외부 의존: 지사 목록 ======
    async function loadBranches() {
        try {
            const result = await apiGet(`${API_BASE}/branches`);
            const branches = result.data || [];
            $branchSelect.innerHTML = '<option value="">-- 지사 선택 --</option>'
                + branches.map(b => `<option value="${escapeHtml(b.branchCode)}">${escapeHtml(b.branchName)}</option>`).join('');
        } catch (e) {
            showToast(e.message);
        }
    }

    // ====== REQ-002 규칙 1: 지사 선택 시 시험선택 버튼 활성화 ======
    function onBranchChange() {
        state.selectedBranchCode = $branchSelect.value;
        state.selectedBranchName = $branchSelect.selectedOptions[0]?.text || '';

        // 지사 변경 시 시험/시험일자 초기화
        state.selectedExamId = null;
        state.selectedExamName = '';
        state.selectedExamDate = '';
        $examNameDisplay.value = '';
        $examIdHidden.value = '';
        $examDateInput.value = '';

        $openExamModalBtn.disabled = !state.selectedBranchCode;
    }

    // ====== REQ-001 시험정보 팝업 ======
    async function openExamModal() {
        if (!state.selectedBranchCode) {
            showToast('지사를 먼저 선택해 주세요.');
            return;
        }

        // 팝업 열기 + 로딩 표시
        $examInfoTableBody.innerHTML = '<tr><td colspan="5" class="empty-message">조회 중...</td></tr>';
        $examModal.classList.add('active');
        state.tempExamId = null;
        state.tempExamName = '';
        state.tempExamDate = '';

        try {
            const result = await apiGet(`${API_BASE}/infos?branchCode=${encodeURIComponent(state.selectedBranchCode)}`);
            const list = result.data || [];

            if (list.length === 0) {
                $examInfoTableBody.innerHTML = '<tr><td colspan="5" class="empty-message">조회된 시험정보가 없습니다.</td></tr>';
                return;
            }

            $examInfoTableBody.innerHTML = list.map((item, idx) => `
                <tr data-exam-id="${item.examId}"
                    data-exam-name="${escapeHtml(item.examName)}"
                    data-exam-date="${escapeHtml(item.examDate)}">
                    <td>${idx + 1}</td>
                    <td>${item.examId}</td>
                    <td style="text-align:left;">${escapeHtml(item.examName)}</td>
                    <td>${escapeHtml(formatExamDate(item.examDate))}</td>
                    <td>${item.itemCount ?? 0}</td>
                </tr>`).join('');

            // 행 클릭 -> 임시 선택
            $examInfoTableBody.querySelectorAll('tr').forEach(row => {
                row.addEventListener('click', () => {
                    $examInfoTableBody.querySelectorAll('tr').forEach(r => r.classList.remove('selected'));
                    row.classList.add('selected');
                    state.tempExamId = Number(row.dataset.examId);
                    state.tempExamName = row.dataset.examName;
                    state.tempExamDate = row.dataset.examDate;
                });
            });
        } catch (e) {
            $examInfoTableBody.innerHTML = `<tr><td colspan="5" class="empty-message">${escapeHtml(e.message)}</td></tr>`;
        }
    }

    function closeExamModal() {
        $examModal.classList.remove('active');
    }

    // ====== REQ-002 규칙 2: 시험정보 선택 시 시험일자 자동 세팅 (readonly) ======
    function confirmExamSelection() {
        if (!state.tempExamId) {
            showToast('시험정보를 선택해 주세요.');
            return;
        }
        state.selectedExamId = state.tempExamId;
        state.selectedExamName = state.tempExamName;
        state.selectedExamDate = state.tempExamDate;

        $examNameDisplay.value = state.selectedExamName;
        $examIdHidden.value = state.selectedExamId;
        $examDateInput.value = formatExamDate(state.selectedExamDate);

        closeExamModal();
    }

    // ====== REQ-003 시행종목 조회 ======
    async function searchExamItems(page = 1) {
        // REQ-003 예외 처리 2: 필수 조회 조건 미입력
        if (!state.selectedExamId || !state.selectedBranchCode || !state.selectedExamDate) {
            showToast('조회 조건을 확인해 주세요.');
            return;
        }

        state.currentPage = page;
        const params = new URLSearchParams({
            examId: state.selectedExamId,
            branchCode: state.selectedBranchCode,
            examDate: state.selectedExamDate,
            page: page,
            size: state.pageSize
        });

        $examItemTableBody.innerHTML = '<tr><td colspan="8" class="empty-message">조회 중...</td></tr>';
        $pagination.innerHTML = '';

        try {
            const result = await apiGet(`${API_BASE}/items?${params.toString()}`);
            const { list = [], pagination = {} } = result.data || {};

            renderExamItemList(list);
            renderPagination(pagination);
            updateTotals(pagination.totalCount || 0);
        } catch (e) {
            $examItemTableBody.innerHTML = `<tr><td colspan="8" class="empty-message">${escapeHtml(e.message)}</td></tr>`;
            updateTotals(0);
        }
    }

    function workTypeBadgeClass(workType) {
        switch (workType) {
            case '필기':
            case '객관식':
                return 'status-work-obj';
            case '실기':
            case '주관식':
                return 'status-work-sub';
            case '서술형':
            case '면접':
                return 'status-work-desc';
            default:
                return 'status-work-desc';
        }
    }

    function renderExamItemList(list) {
        if (!list || list.length === 0) {
            // REQ-003 예외 처리 1: 조회 결과 없음
            $examItemTableBody.innerHTML = '<tr><td colspan="8" class="empty-message">조회된 데이터가 없습니다.</td></tr>';
            return;
        }
        $examItemTableBody.innerHTML = list.map(item => `
            <tr>
                <td>${item.seq}</td>
                <td>${escapeHtml(item.itemCode)}</td>
                <td style="text-align:left;">${escapeHtml(item.itemName)}</td>
                <td>${escapeHtml(item.selectField || '')}</td>
                <td>${escapeHtml(item.selectFieldName || '')}</td>
                <td><span class="status-badge ${workTypeBadgeClass(item.workType)}">${escapeHtml(item.workType || '')}</span></td>
                <td>${escapeHtml(item.part || '')}</td>
                <td><span class="status-badge ${item.exposeYn === 'Y' ? 'status-y' : 'status-n'}">${escapeHtml(item.exposeYn || 'N')}</span></td>
            </tr>`).join('');
    }

    function renderPagination(pagination) {
        const { currentPage = 1, totalPages = 0, hasPrevious, hasNext } = pagination;
        if (totalPages <= 0) {
            $pagination.innerHTML = '';
            return;
        }

        const items = [];
        items.push(renderPageItem('«', !hasPrevious, currentPage - 1, false));

        // 최대 5개 표시 윈도우
        const windowSize = 5;
        const half = Math.floor(windowSize / 2);
        let start = Math.max(1, currentPage - half);
        let end = Math.min(totalPages, start + windowSize - 1);
        start = Math.max(1, end - windowSize + 1);

        for (let p = start; p <= end; p++) {
            items.push(renderPageItem(String(p), false, p, p === currentPage));
        }
        items.push(renderPageItem('»', !hasNext, currentPage + 1, false));

        $pagination.innerHTML = items.join('');

        $pagination.querySelectorAll('.page-item:not(.disabled):not(.active)').forEach(el => {
            el.addEventListener('click', () => {
                const page = Number(el.dataset.page);
                if (page && page !== state.currentPage) {
                    searchExamItems(page);
                }
            });
        });
    }

    function renderPageItem(label, disabled, page, active) {
        const cls = ['page-item'];
        if (disabled) cls.push('disabled');
        if (active) cls.push('active');
        return `<div class="${cls.join(' ')}" data-page="${page}">${label}</div>`;
    }

    function updateTotals(total) {
        $totalCount.textContent = total;
        $resultCountBadge.textContent = total;
        // REQ-004 규칙 1: 1건 이상일 때만 엑셀 버튼 활성화
        $excelDownloadBtn.disabled = total === 0;
    }

    // ====== REQ-004 엑셀 다운로드 ======
    async function downloadExcel() {
        if ($excelDownloadBtn.disabled) {
            showToast('다운로드할 데이터가 없습니다.');
            return;
        }
        if (!state.selectedExamId || !state.selectedBranchCode || !state.selectedExamDate) {
            showToast('조회 조건을 확인해 주세요.');
            return;
        }

        const params = new URLSearchParams({
            examId: state.selectedExamId,
            branchCode: state.selectedBranchCode,
            examDate: state.selectedExamDate
        });

        try {
            const res = await fetch(`${API_BASE}/items/excel?${params.toString()}`, {
                method: 'GET',
                credentials: 'same-origin'
            });
            if (!res.ok) {
                if (res.status === 404) {
                    showToast('다운로드할 데이터가 없습니다.');
                } else {
                    showToast('엑셀 다운로드에 실패했습니다.');
                }
                return;
            }

            const disposition = res.headers.get('Content-Disposition') || '';
            const fileName = extractFileName(disposition)
                || `지사별시행종목_${state.selectedBranchName}_${state.selectedExamName}_${formatExamDate(state.selectedExamDate)}.xlsx`;

            const blob = await res.blob();
            triggerDownload(blob, fileName);
        } catch (e) {
            showToast('엑셀 다운로드 중 오류가 발생했습니다.');
        }
    }

    function extractFileName(contentDisposition) {
        if (!contentDisposition) return null;
        const m = /filename\*=UTF-8''([^;]+)/i.exec(contentDisposition)
            || /filename="?([^";]+)"?/i.exec(contentDisposition);
        if (!m) return null;
        try {
            return decodeURIComponent(m[1]);
        } catch (_) {
            return m[1];
        }
    }

    function triggerDownload(blob, fileName) {
        const url = URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = fileName;
        document.body.appendChild(a);
        a.click();
        setTimeout(() => {
            URL.revokeObjectURL(url);
            a.remove();
        }, 0);
    }

    // ====== 이벤트 바인딩 ======
    function bindEvents() {
        $branchSelect.addEventListener('change', onBranchChange);
        $openExamModalBtn.addEventListener('click', openExamModal);
        $closeExamModalBtn.addEventListener('click', closeExamModal);
        $cancelExamModalBtn.addEventListener('click', closeExamModal);
        $confirmExamModalBtn.addEventListener('click', confirmExamSelection);
        $examModal.addEventListener('click', (e) => {
            if (e.target === $examModal) closeExamModal();
        });

        $searchBtn.addEventListener('click', () => searchExamItems(1));
        $excelDownloadBtn.addEventListener('click', downloadExcel);
    }

    // ====== 초기화 ======
    function init() {
        bindEvents();
        loadBranches();
    }

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', init);
    } else {
        init();
    }
})();
