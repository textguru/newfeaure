#!/usr/bin/env python3
"""
이슈관리 프로그램 - CLI 인터페이스
사용법: python main.py <명령어> [옵션]
"""
import argparse
import sys
import db

# 허용 값 상수
STATUSES = ["열림", "진행중", "완료", "보류"]
PRIORITIES = ["높음", "중간", "낮음"]

# 터미널 컬러 코드
RESET = "\033[0m"
BOLD = "\033[1m"
COLOR = {
    "높음": "\033[91m",   # 빨강
    "중간": "\033[93m",   # 노랑
    "낮음": "\033[92m",   # 초록
    "열림": "\033[96m",   # 청록
    "진행중": "\033[94m", # 파랑
    "완료": "\033[92m",   # 초록
    "보류": "\033[90m",   # 회색
}


def colored(text, key):
    code = COLOR.get(key, "")
    return f"{code}{text}{RESET}" if code else text


def print_issue(issue, detail=False):
    priority_str = colored(issue["priority"], issue["priority"])
    status_str = colored(issue["status"], issue["status"])
    print(
        f"  [{BOLD}#{issue['id']}{RESET}] {issue['title']}  "
        f"상태:{status_str}  우선순위:{priority_str}"
        + (f"  담당:{issue['assignee']}" if issue["assignee"] else "")
    )
    if detail:
        if issue["description"]:
            print(f"       설명: {issue['description']}")
        print(f"       생성: {issue['created_at']}  수정: {issue['updated_at']}")


def print_table(issues):
    if not issues:
        print("  (이슈 없음)")
        return
    print(f"  {'ID':>4}  {'제목':<30}  {'상태':<8}  {'우선순위':<6}  담당자")
    print("  " + "-" * 68)
    for issue in issues:
        title = issue["title"][:30]
        print(
            f"  {issue['id']:>4}  {title:<30}  "
            f"{colored(issue['status'], issue['status']):<18}  "
            f"{colored(issue['priority'], issue['priority']):<16}  "
            f"{issue['assignee']}"
        )


# ---------------------------------------------------------------------------
# 서브 커맨드 핸들러
# ---------------------------------------------------------------------------

def cmd_add(args):
    issue_id = db.create_issue(
        title=args.title,
        description=args.description or "",
        priority=args.priority,
        assignee=args.assignee or "",
    )
    print(f"✔ 이슈 #{issue_id} 생성 완료: {args.title}")


def cmd_list(args):
    issues = db.list_issues(
        status=args.status,
        priority=args.priority,
        assignee=args.assignee,
    )
    label_parts = []
    if args.status:
        label_parts.append(f"상태={args.status}")
    if args.priority:
        label_parts.append(f"우선순위={args.priority}")
    if args.assignee:
        label_parts.append(f"담당={args.assignee}")
    label = " | ".join(label_parts) if label_parts else "전체"
    print(f"\n이슈 목록 [{label}]  총 {len(issues)}건")
    print_table(issues)
    print()


def cmd_view(args):
    issue = db.get_issue(args.id)
    if not issue:
        print(f"오류: 이슈 #{args.id} 를 찾을 수 없습니다.")
        sys.exit(1)
    print(f"\n─── 이슈 #{issue['id']} 상세 ───────────────────────────────────────")
    print(f"  제목     : {issue['title']}")
    print(f"  설명     : {issue['description'] or '(없음)'}")
    print(f"  상태     : {colored(issue['status'], issue['status'])}")
    print(f"  우선순위 : {colored(issue['priority'], issue['priority'])}")
    print(f"  담당자   : {issue['assignee'] or '(없음)'}")
    print(f"  생성일   : {issue['created_at']}")
    print(f"  수정일   : {issue['updated_at']}")
    print()


def cmd_update(args):
    issue = db.get_issue(args.id)
    if not issue:
        print(f"오류: 이슈 #{args.id} 를 찾을 수 없습니다.")
        sys.exit(1)
    kwargs = {}
    if args.title:
        kwargs["title"] = args.title
    if args.description is not None:
        kwargs["description"] = args.description
    if args.status:
        kwargs["status"] = args.status
    if args.priority:
        kwargs["priority"] = args.priority
    if args.assignee is not None:
        kwargs["assignee"] = args.assignee
    if not kwargs:
        print("오류: 수정할 항목을 하나 이상 지정하세요.")
        sys.exit(1)
    db.update_issue(args.id, **kwargs)
    print(f"✔ 이슈 #{args.id} 수정 완료")


def cmd_delete(args):
    issue = db.get_issue(args.id)
    if not issue:
        print(f"오류: 이슈 #{args.id} 를 찾을 수 없습니다.")
        sys.exit(1)
    if not args.yes:
        confirm = input(f"이슈 #{args.id} '{issue['title']}' 을(를) 삭제하시겠습니까? [y/N] ")
        if confirm.strip().lower() not in ("y", "yes"):
            print("취소되었습니다.")
            return
    db.delete_issue(args.id)
    print(f"✔ 이슈 #{args.id} 삭제 완료")


def cmd_close(args):
    issue = db.get_issue(args.id)
    if not issue:
        print(f"오류: 이슈 #{args.id} 를 찾을 수 없습니다.")
        sys.exit(1)
    db.update_issue(args.id, status="완료")
    print(f"✔ 이슈 #{args.id} 완료 처리")


def cmd_search(args):
    issues = db.search_issues(args.keyword)
    print(f"\n검색 결과 '{args.keyword}'  총 {len(issues)}건")
    print_table(issues)
    print()


# ---------------------------------------------------------------------------
# 파서 구성
# ---------------------------------------------------------------------------

def build_parser():
    parser = argparse.ArgumentParser(
        prog="이슈관리",
        description="이슈관리 프로그램 - 간단한 CLI 기반 이슈 트래커",
    )
    sub = parser.add_subparsers(dest="command", metavar="명령어")
    sub.required = True

    # add
    p_add = sub.add_parser("add", help="새 이슈 생성")
    p_add.add_argument("title", help="이슈 제목")
    p_add.add_argument("-d", "--description", help="이슈 설명", default="")
    p_add.add_argument(
        "-p", "--priority", help="우선순위 (기본: 중간)", choices=PRIORITIES, default="중간"
    )
    p_add.add_argument("-a", "--assignee", help="담당자", default="")
    p_add.set_defaults(func=cmd_add)

    # list
    p_list = sub.add_parser("list", aliases=["ls"], help="이슈 목록 조회")
    p_list.add_argument("-s", "--status", help="상태 필터", choices=STATUSES)
    p_list.add_argument("-p", "--priority", help="우선순위 필터", choices=PRIORITIES)
    p_list.add_argument("-a", "--assignee", help="담당자 필터")
    p_list.set_defaults(func=cmd_list)

    # view
    p_view = sub.add_parser("view", help="이슈 상세 조회")
    p_view.add_argument("id", type=int, help="이슈 ID")
    p_view.set_defaults(func=cmd_view)

    # update
    p_update = sub.add_parser("update", help="이슈 수정")
    p_update.add_argument("id", type=int, help="이슈 ID")
    p_update.add_argument("-t", "--title", help="새 제목")
    p_update.add_argument("-d", "--description", help="새 설명")
    p_update.add_argument("-s", "--status", help="새 상태", choices=STATUSES)
    p_update.add_argument("-p", "--priority", help="새 우선순위", choices=PRIORITIES)
    p_update.add_argument("-a", "--assignee", help="새 담당자")
    p_update.set_defaults(func=cmd_update)

    # delete
    p_delete = sub.add_parser("delete", aliases=["del", "rm"], help="이슈 삭제")
    p_delete.add_argument("id", type=int, help="이슈 ID")
    p_delete.add_argument("-y", "--yes", action="store_true", help="확인 없이 삭제")
    p_delete.set_defaults(func=cmd_delete)

    # close
    p_close = sub.add_parser("close", help="이슈를 완료 상태로 변경")
    p_close.add_argument("id", type=int, help="이슈 ID")
    p_close.set_defaults(func=cmd_close)

    # search
    p_search = sub.add_parser("search", help="키워드로 이슈 검색")
    p_search.add_argument("keyword", help="검색 키워드")
    p_search.set_defaults(func=cmd_search)

    return parser


def main():
    db.init_db()
    parser = build_parser()
    args = parser.parse_args()
    args.func(args)


if __name__ == "__main__":
    main()
