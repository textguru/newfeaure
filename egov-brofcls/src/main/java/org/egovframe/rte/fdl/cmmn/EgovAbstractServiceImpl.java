package org.egovframe.rte.fdl.cmmn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * eGovFrame RTE {@code EgovAbstractServiceImpl} 호환 경량 베이스 클래스.
 *
 * <p>이 샌드박스에서는 eGovFrame Maven 저장소 접근이 차단되어 정식 RTE jar를
 * 받을 수 없으므로, 빌드/실행 검증을 위해 동일 패키지/클래스명으로 최소 API만
 * 제공한다. 실제 eGovFrame 환경에서는 이 파일을 삭제하고
 * {@code org.egovframe.rte.fdl.cmmn} jar를 의존성으로 사용한다.</p>
 */
public abstract class EgovAbstractServiceImpl {

    protected final Logger log = LoggerFactory.getLogger(getClass());
}
