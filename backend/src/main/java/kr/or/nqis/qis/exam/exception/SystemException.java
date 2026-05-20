package kr.or.nqis.qis.exam.exception;

import lombok.Getter;

/**
 * 시스템 예외 (DB 오류, 파일 IO 등 인프라 오류).
 *
 * @author NQIS-AR
 * @since 1.0.0
 */
@Getter
public class SystemException extends RuntimeException {

    public SystemException(String message) {
        super(message);
    }

    public SystemException(String message, Throwable cause) {
        super(message, cause);
    }
}
