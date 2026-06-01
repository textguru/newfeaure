#!/usr/bin/env python3
"""더미 절차서 PDF 생성기 (외부 의존성 없음).

프로토타입에서 PDF 뷰어(iframe) 동작을 확인하기 위한 1페이지짜리 더미 PDF를
생성한다. 기본 PDF 폰트(Helvetica)는 한글을 표현하지 못하므로 본문은
ASCII 자리표시자로 채운다. 실제 운영 시에는 이 docs/ 폴더의 파일을
실제 절차서 PDF로 교체하면 된다.

사용법:
    python3 tools/gen_dummy_pdfs.py
"""
import os

# (파일이름, 표지 제목, 부제) — data.js 의 file 경로와 일치시킨다.
DOCS = [
    ("DOC-IQC-001.pdf", "Incoming Inspection Procedure", "DOC-IQC-001 / Rev.3"),
    ("DOC-WHS-001.pdf", "Material Storage Standard", "DOC-WHS-001 / Rev.2"),
    ("DOC-MAC-001.pdf", "Machining Work Standard", "DOC-MAC-001 / Rev.5"),
    ("DOC-MAC-002.pdf", "Machine Daily Checklist", "DOC-MAC-002 / Rev.1"),
    ("DOC-ASM-001.pdf", "Assembly Procedure", "DOC-ASM-001 / Rev.4"),
    ("DOC-QC-001.pdf", "In-Process Inspection Procedure", "DOC-QC-001 / Rev.2"),
    ("DOC-QC-002.pdf", "Final Inspection Control Plan", "DOC-QC-002 / Rev.3"),
    ("DOC-PKG-001.pdf", "Packaging Work Standard", "DOC-PKG-001 / Rev.1"),
    ("DOC-SHP-001.pdf", "Shipping / Delivery Procedure", "DOC-SHP-001 / Rev.2"),
]

OUT_DIR = os.path.join(os.path.dirname(__file__), "..", "procedure-site", "docs")


def _esc(text: str) -> str:
    return text.replace("\\", r"\\").replace("(", r"\(").replace(")", r"\)")


def build_pdf(title: str, subtitle: str) -> bytes:
    """간단한 1페이지 A4 PDF 바이트를 만든다."""
    content = (
        "BT\n"
        "/F1 24 Tf\n"
        "72 720 Td\n"
        f"({_esc(title)}) Tj\n"
        "/F1 13 Tf\n"
        "0 -34 Td\n"
        f"({_esc(subtitle)}) Tj\n"
        "/F1 11 Tf\n"
        "0 -60 Td\n"
        "(This is a placeholder PDF for the prototype.) Tj\n"
        "0 -20 Td\n"
        "(Replace the files in /docs with real procedure PDFs.) Tj\n"
        "0 -20 Td\n"
        "(Opens directly in the browser PDF viewer - offline / closed network OK.) Tj\n"
        "ET\n"
    )
    objs = []
    objs.append("<< /Type /Catalog /Pages 2 0 R >>")
    objs.append("<< /Type /Pages /Kids [3 0 R] /Count 1 >>")
    objs.append(
        "<< /Type /Page /Parent 2 0 R /MediaBox [0 0 595 842] "
        "/Resources << /Font << /F1 5 0 R >> >> /Contents 4 0 R >>"
    )
    objs.append(
        f"<< /Length {len(content.encode('latin-1'))} >>\nstream\n{content}endstream"
    )
    objs.append("<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>")

    out = b"%PDF-1.4\n"
    offsets = []
    for i, body in enumerate(objs, start=1):
        offsets.append(len(out))
        out += f"{i} 0 obj\n{body}\nendobj\n".encode("latin-1")

    xref_pos = len(out)
    out += f"xref\n0 {len(objs) + 1}\n".encode("latin-1")
    out += b"0000000000 65535 f \n"
    for off in offsets:
        out += f"{off:010d} 00000 n \n".encode("latin-1")
    out += (
        f"trailer\n<< /Size {len(objs) + 1} /Root 1 0 R >>\n"
        f"startxref\n{xref_pos}\n%%EOF\n"
    ).encode("latin-1")
    return out


def main() -> None:
    os.makedirs(OUT_DIR, exist_ok=True)
    for fname, title, subtitle in DOCS:
        path = os.path.join(OUT_DIR, fname)
        with open(path, "wb") as f:
            f.write(build_pdf(title, subtitle))
        print("wrote", os.path.relpath(path))


if __name__ == "__main__":
    main()
