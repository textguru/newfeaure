<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>지사별 시행종목 조회</title>
    <style>
        * { box-sizing: border-box; margin: 0; padding: 0; }
        body { font-family: 'Malgun Gothic', sans-serif; font-size: 13px; color: #333; padding: 20px; }
        .page-title { font-size: 18px; font-weight: bold; margin-bottom: 16px; }
        .panel { border: 1px solid #ddd; border-radius: 6px; padding: 16px; margin-bottom: 12px; background: #fff; }
        .panel-title { font-size: 13px; font-weight: bold; margin-bottom: 12px; color: #555; }
        .condition-grid { display: grid; grid-template-columns: 1fr 2fr 1fr 120px; gap: 10px; align-items: end; }
        .field { display: flex; flex-direction: column; gap: 4px; }
        .field-label { font-size: 12px; color: #666; }
        .field-required { color: #d93025; margin-left: 2px; }
        select, input[type="text"] { height: 32px; padding: 0 8px; border: 1px solid #ccc; border-radius: 4px; font-size: 13px; }
        input[readonly] { background: #f5f5f5; }
        .btn { height: 32px; padding: 0 14px; border: 1px solid #ccc; border-radius: 4px; background: #fff; cursor: pointer; font-size: 13px; }
        .btn:disabled { opacity: 0.5; cursor: not-allowed; }
        .btn-primary { background: #1a73e8; color: #fff; border-color: #1a73e8; }
        .btn-success { background: #1e8e3e; color: #fff; border-color: #1e8e3e; }
        .test-row { margin-top: 10px; display: flex; gap: 8px; align-items: center; }
        .test-row .hint { font-size: 11px; color: #888; }
        .toolbar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px; }
        .toolbar-left { font-size: 12px; color: #555; }
        .toolbar-left strong { color: #000; }
        table { width: 100%; border-collapse: collapse; font-size: 12px; }
        th, td { padding: 8px 10px; border-bottom: 1px solid #e5e5e5; text-align: center; }
        th { background: #f7f7f7; color: #555; font-weight: 600; }
        td.left { text-align: left; }
        .badge { display: inline-block; padding: 2px 8px; border-radius: 12px; font-size: 11px; }
        .badge-y { background: #e6f4ea; color: #1e8e3e; }
        .badge-n { background: #f1f3f4; color: #666; }
        .paging { display: flex; justify-content: center; gap: 4px; margin-top: 12px; }
        .page-btn { width: 28px; height: 28px; border: 1px solid #ddd; border-radius: 4px; background: #fff; cursor: pointer; }
        .page-btn.active { background: #1a73e8; color: #fff; border-color: #1a73e8; }
        .empty-msg { padding: 24px; text-align: center; color: #888; }
        /* 팝업 */
        .popup-overlay { display: none; position: fixed; inset: 0; background: rgba(0,0,0,0.4); z-index: 1000; }
        .popup-overlay.active { display: flex; align-items: center; justify-content: center; }
        .popup { background: #fff; width: 520px; max-height: 70vh; border-radius: 8px; display: flex; flex-direction: column; }
        .popup-header { padding: 14px 16px; border-bottom: 1px solid #eee; display: flex; justify-content: space-between; align-items: center; }
        .popup-body { padding: 12px 16px; overflow-y: auto; flex: 1; }
        .popup-footer { padding: 12px 16px; border-top: 1px solid #eee; display: flex; justify-content: flex-end; gap: 8px; }
        .test-item { padding: 10px 12px; border: 1px solid #eee; border-radius: 4px; margin-bottom: 6px; cursor: pointer; }
        .test-item:hover { background: #f7f7f7; }
        .test-item.selected { background: #e8f0fe; border-color: #1a73e8; }
        .test-item .main { font-weight: 600; }
        .test-item .sub { font-size: 11px; color: #888; margin-top: 2px; }
    </style>
</head>
<body>

<div class="page-title">📋 지사별 시행종목 조회</div>

<!-- ① 조회 조건 영역 -->
<div class="panel">
    <div class="panel-title">조회 조건</div>
    <div class="condition-grid">
        <div class="field">
            <label class="field-label" for="brofCd">지사 <span class="field-required">*</span></label>
            <select id="brofCd">
                <option value="">지사 선택</option>
            </select>
        </div>
        <div class="field">
            <label class="field-label" for="testNm">시험정보 <span class="field-required">*</span></label>
            <input type="text" id="testNm" placeholder="시험선택 버튼으로 선택" readonly>
            <input type="hidden" id="testNo">
        </div>
        <div class="field">
            <label class="field-label" for="testYmd">시험일자 <span class="field-required">*</span></label>
            <input type="text" id="testYmd" readonly placeholder="자동 세팅">
        </div>
        <div class="field">
            <label class="field-label" style="visibility:hidden">action</label>
            <button type="button" id="btnSearch" class="btn btn-primary" disabled>🔍 조회</button>
        </div>
    </div>
    <div class="test-row">
        <button type="button" id="btnTestSelect" class="btn" disabled>시험선택</button>
        <span class="hint">지사 선택 후 클릭 → 팝업에서 해당 지사 시험정보 목록 표시</span>
    </div>
</div>

<!-- ② 목록 영역 -->
<div class="panel">
    <div class="toolbar">
        <div class="toolbar-left">전체 <strong id="totalCount">0</strong>건 | 페이지당 20건</div>
        <div>
            <button type="button" id="btnExcel" class="btn btn-success" disabled>📊 엑셀 다운로드</button>
        </div>
    </div>
    <div id="listArea">
        <div class="empty-msg">조회 조건을 설정하고 조회 버튼을 클릭하세요.</div>
    </div>
    <div id="pagingArea" class="paging"></div>
</div>

<!-- POP-001 시험정보 선택 팝업 -->
<div id="testPopup" class="popup-overlay">
    <div class="popup">
        <div class="popup-header">
            <strong>시험정보 선택</strong>
            <button type="button" class="btn" onclick="closeTestPopup()">✕</button>
        </div>
        <div class="popup-body" id="testPopupBody"></div>
        <div class="popup-footer">
            <button type="button" class="btn" onclick="closeTestPopup()">취소</button>
            <button type="button" id="btnTestConfirm" class="btn btn-primary" disabled onclick="confirmTestSelect()">선택</button>
        </div>
    </div>
</div>

<script>
(function() {
    'use strict';

    var ctx = '<c:url value="/" />'.replace(/\/$/, '');
    var apiBase = ctx + '/exam/branch-subject';

    var state = {
        brofCd: '',
        brofNm: '',
        testNo: '',
        testNm: '',
        testYmd: '',
        pageNo: 1,
        pageSize: 20,
        totalCount: 0,
        totalPages: 0,
        selectedTestInPopup: null
    };

    // ----- 초기 로드 -----
    function loadBrofList() {
        fetch(apiBase + '/brof-list')
            .then(function(res) { return res.json(); })
            .then(function(json) {
                var sel = document.getElementById('brofCd');
                json.data.forEach(function(b) {
                    var opt = document.createElement('option');
                    opt.value = b.brofCd;
                    opt.textContent = b.brofNm;
                    opt.dataset.brofNm = b.brofNm;
                    sel.appendChild(opt);
                });
            })
            .catch(function(err) { alert('지사 목록 조회 실패'); });
    }

    // ----- 지사 변경 -----
    document.getElementById('brofCd').addEventListener('change', function(e) {
        state.brofCd = e.target.value;
        var opt = e.target.selectedOptions[0];
        state.brofNm = opt ? opt.dataset.brofNm || '' : '';
        // 시험 정보 초기화
        state.testNo = ''; state.testNm = ''; state.testYmd = '';
        document.getElementById('testNm').value = '';
        document.getElementById('testNo').value = '';
        document.getElementById('testYmd').value = '';
        document.getElementById('btnTestSelect').disabled = !state.brofCd;
        document.getElementById('btnSearch').disabled = true;
    });

    // ----- 시험선택 버튼 -----
    document.getElementById('btnTestSelect').addEventListener('click', function() {
        if (!state.brofCd) { return; }
        fetch(apiBase + '/test-info-list?brofCd=' + encodeURIComponent(state.brofCd))
            .then(function(res) { return res.json(); })
            .then(function(json) { openTestPopup(json.data || []); })
            .catch(function(err) { alert('시험정보 조회 실패'); });
    });

    function openTestPopup(list) {
        var body = document.getElementById('testPopupBody');
        body.innerHTML = '';
        state.selectedTestInPopup = null;
        document.getElementById('btnTestConfirm').disabled = true;

        if (list.length === 0) {
            body.innerHTML = '<div class="empty-msg">조회된 시험정보가 없습니다.</div>';
        } else {
            list.forEach(function(t) {
                var div = document.createElement('div');
                div.className = 'test-item';
                div.dataset.testNo = t.testNo;
                div.dataset.testNm = t.testNm;
                div.dataset.testYmd = t.testYmd;
                div.innerHTML = '<div class="main">' + escapeHtml(t.testNm) + '</div>'
                    + '<div class="sub">시험일자: ' + formatYmd(t.testYmd)
                    + ' / 시행종목: ' + t.clsCount + '건</div>';
                div.addEventListener('click', function() {
                    document.querySelectorAll('.test-item').forEach(function(el) { el.classList.remove('selected'); });
                    div.classList.add('selected');
                    state.selectedTestInPopup = {
                        testNo: t.testNo, testNm: t.testNm, testYmd: t.testYmd
                    };
                    document.getElementById('btnTestConfirm').disabled = false;
                });
                body.appendChild(div);
            });
        }
        document.getElementById('testPopup').classList.add('active');
    }

    window.closeTestPopup = function() {
        document.getElementById('testPopup').classList.remove('active');
    };

    window.confirmTestSelect = function() {
        var sel = state.selectedTestInPopup;
        if (!sel) { return; }
        state.testNo = sel.testNo;
        state.testNm = sel.testNm;
        state.testYmd = sel.testYmd;
        document.getElementById('testNo').value = sel.testNo;
        document.getElementById('testNm').value = sel.testNm;
        document.getElementById('testYmd').value = formatYmd(sel.testYmd);
        document.getElementById('btnSearch').disabled = false;
        closeTestPopup();
    };

    // ----- 조회 -----
    document.getElementById('btnSearch').addEventListener('click', function() {
        if (!validateConditions()) {
            alert('조회 조건을 확인해 주세요.');
            return;
        }
        state.pageNo = 1;
        searchList();
    });

    function validateConditions() {
        return !!state.brofCd && !!state.testNo && !!state.testYmd;
    }

    function searchList() {
        var params = 'brofCd=' + encodeURIComponent(state.brofCd)
            + '&testNo=' + encodeURIComponent(state.testNo)
            + '&testYmd=' + encodeURIComponent(state.testYmd)
            + '&pageNo=' + state.pageNo
            + '&pageSize=' + state.pageSize;
        fetch(apiBase + '/list?' + params)
            .then(function(res) { return res.json(); })
            .then(function(json) { renderList(json.data); })
            .catch(function(err) { alert('목록 조회 실패'); });
    }

    function renderList(result) {
        state.totalCount = result.totalCount;
        state.totalPages = result.totalPages;
        document.getElementById('totalCount').textContent = result.totalCount;
        document.getElementById('btnExcel').disabled = result.totalCount === 0;

        var area = document.getElementById('listArea');
        if (result.totalCount === 0) {
            area.innerHTML = '<div class="empty-msg">조회된 데이터가 없습니다.</div>';
            document.getElementById('pagingArea').innerHTML = '';
            return;
        }

        var html = '<table><thead><tr>'
            + '<th>순번</th><th>종목코드</th><th>종목명</th>'
            + '<th>선택분야</th><th>선택분야명</th><th>작업구분</th>'
            + '<th>부</th><th>시험일정공개여부</th>'
            + '</tr></thead><tbody>';
        result.list.forEach(function(r) {
            var badge = r.testSchdRlsYn === 'Y'
                ? '<span class="badge badge-y">Y</span>'
                : '<span class="badge badge-n">N</span>';
            html += '<tr>'
                + '<td>' + r.rowNum + '</td>'
                + '<td>' + escapeHtml(r.clsNo || '') + '</td>'
                + '<td class="left">' + escapeHtml(r.clsNm || '') + '</td>'
                + '<td>' + escapeHtml(r.fldCd || '') + '</td>'
                + '<td class="left">' + escapeHtml(r.fldNm || '') + '</td>'
                + '<td>' + escapeHtml(r.jobSeNm || '') + '</td>'
                + '<td>' + escapeHtml(r.testPtNo || '') + '</td>'
                + '<td>' + badge + '</td>'
                + '</tr>';
        });
        html += '</tbody></table>';
        area.innerHTML = html;

        renderPaging();
    }

    function renderPaging() {
        var area = document.getElementById('pagingArea');
        if (state.totalPages <= 1) { area.innerHTML = ''; return; }
        var html = '';
        html += pageBtn('<<', 1, state.pageNo === 1);
        html += pageBtn('<', Math.max(1, state.pageNo - 1), state.pageNo === 1);
        for (var i = 1; i <= state.totalPages; i++) {
            html += pageBtn(i, i, false, i === state.pageNo);
        }
        html += pageBtn('>', Math.min(state.totalPages, state.pageNo + 1), state.pageNo === state.totalPages);
        html += pageBtn('>>', state.totalPages, state.pageNo === state.totalPages);
        area.innerHTML = html;
        area.querySelectorAll('button[data-page]').forEach(function(btn) {
            btn.addEventListener('click', function() {
                state.pageNo = parseInt(btn.dataset.page, 10);
                searchList();
            });
        });
    }

    function pageBtn(label, page, disabled, active) {
        return '<button type="button" class="page-btn' + (active ? ' active' : '') + '"'
            + ' data-page="' + page + '"' + (disabled ? ' disabled' : '') + '>'
            + label + '</button>';
    }

    // ----- 엑셀 다운로드 -----
    document.getElementById('btnExcel').addEventListener('click', function() {
        if (state.totalCount === 0) {
            alert('다운로드할 데이터가 없습니다.');
            return;
        }
        var url = apiBase + '/excel?brofCd=' + encodeURIComponent(state.brofCd)
            + '&testNo=' + encodeURIComponent(state.testNo)
            + '&testYmd=' + encodeURIComponent(state.testYmd)
            + '&brofNm=' + encodeURIComponent(state.brofNm)
            + '&testNm=' + encodeURIComponent(state.testNm);
        window.location.href = url;
    });

    // ----- 유틸 -----
    function escapeHtml(s) {
        return String(s).replace(/[&<>"']/g, function(c) {
            return { '&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;' }[c];
        });
    }
    function formatYmd(ymd) {
        if (!ymd || ymd.length !== 8) { return ymd || ''; }
        return ymd.substring(0,4) + '-' + ymd.substring(4,6) + '-' + ymd.substring(6,8);
    }

    // 초기화
    loadBrofList();
})();
</script>

</body>
</html>
