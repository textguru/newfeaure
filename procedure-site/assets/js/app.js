/*
 * 절차서 포털 공용 스크립트
 * 각 페이지(index/list/detail)에서 App.initXxx() 를 호출한다.
 * 외부 라이브러리 없음, file:// 환경에서 그대로 동작.
 */
(function () {
  "use strict";

  var D = window.PROC_DATA || { processSteps: [], documents: [], docTypes: [] };

  function esc(s) {
    return String(s == null ? "" : s)
      .replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;")
      .replace(/"/g, "&quot;").replace(/'/g, "&#39;");
  }
  function qs(name) {
    var m = new RegExp("[?&]" + name + "=([^&]*)").exec(location.search);
    return m ? decodeURIComponent(m[1].replace(/\+/g, " ")) : "";
  }
  function stepById(id) {
    for (var i = 0; i < D.processSteps.length; i++)
      if (D.processSteps[i].id === id) return D.processSteps[i];
    return null;
  }
  function stepName(id) { var s = stepById(id); return s ? s.name : "-"; }
  function docCount(stepId) {
    return D.documents.filter(function (d) { return d.step === stepId; }).length;
  }
  function docById(id) {
    for (var i = 0; i < D.documents.length; i++)
      if (D.documents[i].id === id) return D.documents[i];
    return null;
  }

  // 검색: 제목/문서번호/담당/키워드/공정명 부분일치
  function search(docs, q) {
    q = (q || "").trim().toLowerCase();
    if (!q) return docs;
    var terms = q.split(/\s+/);
    return docs.filter(function (d) {
      var hay = [
        d.id, d.title, d.owner, d.type, stepName(d.step),
        (d.keywords || []).join(" "),
      ].join(" ").toLowerCase();
      return terms.every(function (t) { return hay.indexOf(t) !== -1; });
    });
  }

  // ---------- 초기화면 (공정도) ----------
  function initIndex() {
    var flow = document.getElementById("flow");
    if (flow) {
      flow.innerHTML = D.processSteps.map(function (s, i) {
        return (
          '<div class="flow-step">' +
            '<a class="flow-node" href="list.html?step=' + encodeURIComponent(s.id) + '" ' +
              'title="' + esc(s.desc) + '">' +
              '<span class="code">' + esc(s.code) + '</span>' +
              '<span class="num">' + (i + 1) + '</span>' +
            '</a>' +
            '<div class="name">' + esc(s.name) + '</div>' +
            '<div class="count">절차서 ' + docCount(s.id) + '건</div>' +
          '</div>'
        );
      }).join("");
    }

    // 공정별 절차서 카드
    var cards = document.getElementById("step-cards");
    if (cards) {
      cards.innerHTML = D.processSteps.map(function (s) {
        var docs = D.documents.filter(function (d) { return d.step === s.id; });
        var list = docs.map(function (d) {
          return '<div class="meta">· <a href="detail.html?id=' +
            encodeURIComponent(d.id) + '">' + esc(d.title) + '</a> ' +
            '<span class="badge type">' + esc(d.type) + '</span></div>';
        }).join("") || '<div class="meta">등록된 절차서 없음</div>';
        return (
          '<div class="card">' +
            '<h3><a href="list.html?step=' + encodeURIComponent(s.id) + '">' +
              esc(s.name) + '</a> <span class="badge">' + esc(s.code) + '</span></h3>' +
            '<div class="meta">' + esc(s.desc) + '</div>' +
            list +
          '</div>'
        );
      }).join("");
    }

    // 전역 검색 → list.html 로 이동
    var form = document.getElementById("search-form");
    if (form) {
      form.addEventListener("submit", function (e) {
        e.preventDefault();
        var q = document.getElementById("search-input").value;
        location.href = "list.html?q=" + encodeURIComponent(q);
      });
    }
  }

  // ---------- 목록 페이지 ----------
  function initList() {
    var stepSel = document.getElementById("f-step");
    var typeSel = document.getElementById("f-type");
    var input = document.getElementById("f-q");
    var tbody = document.getElementById("doc-rows");
    var count = document.getElementById("result-count");
    if (!tbody) return;

    // 필터 옵션 채우기
    stepSel.innerHTML = '<option value="">전체 공정</option>' +
      D.processSteps.map(function (s) {
        return '<option value="' + s.id + '">' + esc(s.name) + '</option>';
      }).join("");
    typeSel.innerHTML = '<option value="">전체 종류</option>' +
      D.docTypes.map(function (t) {
        return '<option value="' + esc(t) + '">' + esc(t) + '</option>';
      }).join("");

    // URL 초기값 반영
    if (qs("step")) stepSel.value = qs("step");
    if (qs("type")) typeSel.value = qs("type");
    if (qs("q")) input.value = qs("q");

    function render() {
      var rows = D.documents.slice();
      if (stepSel.value) rows = rows.filter(function (d) { return d.step === stepSel.value; });
      if (typeSel.value) rows = rows.filter(function (d) { return d.type === typeSel.value; });
      rows = search(rows, input.value);

      count.textContent = "총 " + rows.length + "건";
      if (!rows.length) {
        tbody.innerHTML = '<tr><td colspan="6" class="empty">조건에 맞는 절차서가 없습니다.</td></tr>';
        return;
      }
      tbody.innerHTML = rows.map(function (d) {
        return (
          "<tr>" +
            '<td><span class="badge">' + esc(stepName(d.step)) + '</span></td>' +
            '<td class="title"><a href="detail.html?id=' + encodeURIComponent(d.id) +
              '">' + esc(d.title) + '</a></td>' +
            "<td>" + esc(d.id) + "</td>" +
            '<td><span class="badge type">' + esc(d.type) + "</span></td>" +
            "<td>" + esc(d.rev) + "</td>" +
            "<td>" + esc(d.date) + "</td>" +
          "</tr>"
        );
      }).join("");
    }

    stepSel.addEventListener("change", render);
    typeSel.addEventListener("change", render);
    input.addEventListener("input", render);
    document.getElementById("f-reset").addEventListener("click", function () {
      stepSel.value = ""; typeSel.value = ""; input.value = ""; render();
    });
    render();
  }

  // ---------- 상세 페이지 ----------
  function initDetail() {
    var wrap = document.getElementById("detail");
    if (!wrap) return;
    var d = docById(qs("id"));
    if (!d) {
      wrap.innerHTML = '<div class="empty">문서를 찾을 수 없습니다. ' +
        '<a href="list.html">목록으로</a></div>';
      return;
    }
    document.title = d.title + " - 절차서 포털";
    var kw = (d.keywords || []).map(function (k) { return "<span>#" + esc(k) + "</span>"; }).join("");
    wrap.innerHTML =
      '<div class="detail-head">' +
        '<h2>' + esc(d.title) + '</h2>' +
        '<span class="badge">' + esc(stepName(d.step)) + '</span>' +
        '<span class="badge type">' + esc(d.type) + '</span>' +
      '</div>' +
      '<div class="kw">' + kw + '</div>' +
      '<div class="detail-meta">' +
        metaItem("문서번호", d.id) +
        metaItem("개정", d.rev) +
        metaItem("제·개정일", d.date) +
        metaItem("관리부서", d.owner) +
        metaItem("공정단계", stepName(d.step)) +
      '</div>' +
      '<div class="toolbar">' +
        '<a class="btn" href="' + esc(d.file) + '" target="_blank">새 창에서 열기</a>' +
        '<a class="btn secondary" href="' + esc(d.file) + '" download>PDF 다운로드</a>' +
        '<a class="btn secondary" href="list.html?step=' + encodeURIComponent(d.step) +
          '">같은 공정 보기</a>' +
      '</div>' +
      '<div class="pdf-wrap"><iframe src="' + esc(d.file) +
        '" title="' + esc(d.title) + '"></iframe></div>';
  }
  function metaItem(k, v) {
    return '<div><div class="k">' + esc(k) + '</div><div class="v">' + esc(v) + "</div></div>";
  }

  // 헤더/푸터 공통 텍스트
  function fillChrome() {
    var t = document.querySelector("[data-site-title]");
    if (t) t.textContent = D.siteTitle || "절차서 포털";
    var s = document.querySelector("[data-site-sub]");
    if (s) s.textContent = D.siteSubtitle || "";
    var o = document.querySelector("[data-org]");
    if (o) o.textContent = D.org || "";
  }

  window.App = {
    init: function (page) {
      fillChrome();
      if (page === "index") initIndex();
      else if (page === "list") initList();
      else if (page === "detail") initDetail();
    },
  };
})();
