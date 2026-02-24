"""
데이터베이스 관리 모듈 - SQLite 기반 이슈 저장소
"""
import sqlite3
import os
from datetime import datetime

DB_FILE = os.path.join(os.path.dirname(__file__), "issues.db")


def get_connection():
    conn = sqlite3.connect(DB_FILE)
    conn.row_factory = sqlite3.Row
    return conn


def init_db():
    conn = get_connection()
    cursor = conn.cursor()
    cursor.execute("""
        CREATE TABLE IF NOT EXISTS issues (
            id        INTEGER PRIMARY KEY AUTOINCREMENT,
            title     TEXT NOT NULL,
            description TEXT DEFAULT '',
            status    TEXT NOT NULL DEFAULT '열림',
            priority  TEXT NOT NULL DEFAULT '중간',
            assignee  TEXT DEFAULT '',
            created_at TEXT NOT NULL,
            updated_at TEXT NOT NULL
        )
    """)
    conn.commit()
    conn.close()


def _now():
    return datetime.now().strftime("%Y-%m-%d %H:%M:%S")


def create_issue(title, description="", priority="중간", assignee=""):
    conn = get_connection()
    now = _now()
    cursor = conn.cursor()
    cursor.execute(
        """
        INSERT INTO issues (title, description, status, priority, assignee, created_at, updated_at)
        VALUES (?, ?, '열림', ?, ?, ?, ?)
        """,
        (title, description, priority, assignee, now, now),
    )
    conn.commit()
    issue_id = cursor.lastrowid
    conn.close()
    return issue_id


def list_issues(status=None, priority=None, assignee=None):
    conn = get_connection()
    cursor = conn.cursor()
    query = "SELECT * FROM issues WHERE 1=1"
    params = []
    if status:
        query += " AND status = ?"
        params.append(status)
    if priority:
        query += " AND priority = ?"
        params.append(priority)
    if assignee:
        query += " AND assignee = ?"
        params.append(assignee)
    query += " ORDER BY id ASC"
    cursor.execute(query, params)
    rows = cursor.fetchall()
    conn.close()
    return [dict(r) for r in rows]


def get_issue(issue_id):
    conn = get_connection()
    cursor = conn.cursor()
    cursor.execute("SELECT * FROM issues WHERE id = ?", (issue_id,))
    row = cursor.fetchone()
    conn.close()
    return dict(row) if row else None


def update_issue(issue_id, **kwargs):
    """
    수정 가능한 필드: title, description, status, priority, assignee
    """
    allowed = {"title", "description", "status", "priority", "assignee"}
    fields = {k: v for k, v in kwargs.items() if k in allowed and v is not None}
    if not fields:
        return False
    fields["updated_at"] = _now()
    set_clause = ", ".join(f"{k} = ?" for k in fields)
    values = list(fields.values()) + [issue_id]
    conn = get_connection()
    cursor = conn.cursor()
    cursor.execute(f"UPDATE issues SET {set_clause} WHERE id = ?", values)
    affected = cursor.rowcount
    conn.commit()
    conn.close()
    return affected > 0


def delete_issue(issue_id):
    conn = get_connection()
    cursor = conn.cursor()
    cursor.execute("DELETE FROM issues WHERE id = ?", (issue_id,))
    affected = cursor.rowcount
    conn.commit()
    conn.close()
    return affected > 0


def search_issues(keyword):
    conn = get_connection()
    cursor = conn.cursor()
    pattern = f"%{keyword}%"
    cursor.execute(
        """
        SELECT * FROM issues
        WHERE title LIKE ? OR description LIKE ? OR assignee LIKE ?
        ORDER BY id ASC
        """,
        (pattern, pattern, pattern),
    )
    rows = cursor.fetchall()
    conn.close()
    return [dict(r) for r in rows]
