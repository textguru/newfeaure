# 이슈관리 프로그램

Python 표준 라이브러리만을 사용한 CLI 기반 이슈 트래커입니다.
SQLite 데이터베이스로 이슈를 로컬에 저장합니다.

## 요구사항

- Python 3.7 이상
- 외부 패키지 불필요 (표준 라이브러리만 사용)

## 사용법

```bash
python main.py <명령어> [옵션]
```

## 명령어

### 이슈 생성

```bash
python main.py add "버그: 로그인 실패"
python main.py add "기능 추가: 다크모드" -d "사용자 요청 기능" -p 높음 -a 홍길동
```

옵션:
- `-d / --description` : 설명
- `-p / --priority`    : 우선순위 (`높음` / `중간` / `낮음`, 기본값: `중간`)
- `-a / --assignee`    : 담당자

---

### 이슈 목록 조회

```bash
python main.py list
python main.py list -s 열림
python main.py list -p 높음
python main.py list -a 홍길동
```

옵션:
- `-s / --status`   : 상태 필터 (`열림` / `진행중` / `완료` / `보류`)
- `-p / --priority` : 우선순위 필터
- `-a / --assignee` : 담당자 필터

---

### 이슈 상세 조회

```bash
python main.py view 1
```

---

### 이슈 수정

```bash
python main.py update 1 -s 진행중
python main.py update 1 -t "새 제목" -a 김철수
```

옵션:
- `-t / --title`       : 새 제목
- `-d / --description` : 새 설명
- `-s / --status`      : 새 상태
- `-p / --priority`    : 새 우선순위
- `-a / --assignee`    : 새 담당자

---

### 이슈 완료 처리

```bash
python main.py close 1
```

---

### 이슈 삭제

```bash
python main.py delete 1
python main.py delete 1 -y   # 확인 없이 삭제
```

---

### 이슈 검색

```bash
python main.py search "로그인"
```

---

## 이슈 상태

| 상태 | 설명 |
|------|------|
| 열림 | 신규 이슈 (기본값) |
| 진행중 | 처리 중인 이슈 |
| 완료 | 해결된 이슈 |
| 보류 | 보류/대기 중인 이슈 |

## 이슈 우선순위

| 우선순위 | 설명 |
|----------|------|
| 높음 | 즉시 처리 필요 |
| 중간 | 일반 우선순위 (기본값) |
| 낮음 | 여유 있을 때 처리 |

## 데이터 저장 위치

`issues.db` (SQLite) 파일에 로컬 저장됩니다.
