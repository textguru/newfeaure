newfeaure
=========

## 표준 절차서 포털 (오프라인 / 폐쇄망용 정적 HTML)

서버 없이 브라우저에서 바로 열리는 절차서 열람 포털 프로토타입입니다.
공정도 기준 초기화면 → 목록 → 상세(PDF 뷰어) 흐름을 제공하며,
인터넷 연결 없이 동작합니다.

- 소스/문서: [`procedure-site/`](procedure-site/) (자세한 사용·운영 방법은 해당 폴더의 README 참고)
- 배포 묶음 만들기: `bash tools/make_bundle.sh` → `dist/절차서포털.zip`
- 더미 PDF 재생성: `python3 tools/gen_dummy_pdfs.py`

> 현재는 더미 데이터 기반 프로토타입입니다. 실제 절차서 PDF 와
> 공정-절차서 매핑표를 `procedure-site/docs/` 및 `assets/js/data.js` 에
> 반영하면 그대로 운영용으로 사용할 수 있습니다.
